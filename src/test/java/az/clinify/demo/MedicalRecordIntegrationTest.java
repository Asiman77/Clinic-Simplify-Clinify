package az.clinify.demo;

import az.clinify.demo.entity.Appointment;
import az.clinify.demo.entity.Department;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.enums.AppointmentType;
import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.repository.AppointmentRepository;
import az.clinify.demo.repository.DepartmentRepository;
import az.clinify.demo.repository.DoctorProfileRepository;
import az.clinify.demo.repository.MedicalRecordRepository;
import az.clinify.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MedicalRecordIntegrationTest {

    private static final String DOCTOR_FIN = "DOC0001";
    private static final String PATIENT_FIN = "PAT0001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getMedicalRecord_returnsPersistedRecord_whenUserIsAdmin() throws Exception {
        Fixture fixture = persistFixture(AppointmentStatus.APPROVED);
        MedicalRecord record = saveRecord(
                fixture,
                "Seasonal allergy",
                "Sneezing and itchy eyes",
                "Cetirizine once daily",
                LocalDateTime.of(2026, 7, 10, 9, 30));

        mockMvc.perform(get("/api/records/{id}", record.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(record.getId()))
                .andExpect(jsonPath("$.patientId").value(fixture.patient().getId()))
                .andExpect(jsonPath("$.patientFullName").value("Aylin Mammadova"))
                .andExpect(jsonPath("$.doctorId").value(fixture.doctor().getId()))
                .andExpect(jsonPath("$.doctorFullName").value("Kamran Aliyev"))
                .andExpect(jsonPath("$.diagnosis").value("Seasonal allergy"))
                .andExpect(jsonPath("$.symptoms").value("Sneezing and itchy eyes"))
                .andExpect(jsonPath("$.receipt").value("Cetirizine once daily"))
                .andExpect(jsonPath("$.labResponses").isEmpty());
    }

    @Test
    void createMedicalRecord_persistsRecordAndPendingLabTests_forEligiblePatient() throws Exception {
        Fixture fixture = persistFixture(AppointmentStatus.COMPLETED);
        Map<String, Object> request = Map.of(
                "patientId", fixture.patient().getId(),
                "diagnosis", "Iron deficiency anemia",
                "symptoms", "Fatigue and dizziness",
                "receipt", "Iron supplement",
                "labTests", List.of(
                        Map.of("testName", "Complete blood count", "note", "Check hemoglobin"),
                        Map.of("testName", "Ferritin", "note", "Fasting sample")));

        mockMvc.perform(post("/api/records")
                        .with(user(DOCTOR_FIN).roles("DOCTOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").value(fixture.patient().getId()))
                .andExpect(jsonPath("$.doctorId").value(fixture.doctor().getId()))
                .andExpect(jsonPath("$.diagnosis").value("Iron deficiency anemia"))
                .andExpect(jsonPath("$.labResponses.length()").value(2))
                .andExpect(jsonPath("$.labResponses[0].testName").value("Complete blood count"))
                .andExpect(jsonPath("$.labResponses[0].status").value("PENDING"));

        assertThat(medicalRecordRepository.findAll())
                .singleElement()
                .satisfies(saved -> {
                    assertThat(saved.getDoctor().getId()).isEqualTo(fixture.doctor().getId());
                    assertThat(saved.getPatient().getId()).isEqualTo(fixture.patient().getId());
                    assertThat(saved.getLabResponses()).hasSize(2);
                    assertThat(saved.getLabResponses())
                            .allSatisfy(lab -> assertThat(lab.getStatus()).isEqualTo(LabStatuses.PENDING));
                });
    }

    @Test
    void createMedicalRecord_returnsBadRequest_forBlankDiagnosis() throws Exception {
        Fixture fixture = persistFixture(AppointmentStatus.APPROVED);

        mockMvc.perform(post("/api/records")
                        .with(user(DOCTOR_FIN).roles("DOCTOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "patientId", fixture.patient().getId(),
                                "diagnosis", "   "))))
                .andExpect(status().isBadRequest());

        assertThat(medicalRecordRepository.count()).isZero();
    }

    @Test
    void createMedicalRecord_returnsForbidden_withoutEligibleAppointment() throws Exception {
        Fixture fixture = persistFixture(AppointmentStatus.REQUESTED);

        mockMvc.perform(post("/api/records")
                        .with(user(DOCTOR_FIN).roles("DOCTOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "patientId", fixture.patient().getId(),
                                "diagnosis", "Migraine"))))
                .andExpect(status().isForbidden());

        assertThat(medicalRecordRepository.count()).isZero();
    }

    @Test
    void getPatientRecords_returnsNewestRecordsFirst_whenUserIsAdmin() throws Exception {
        Fixture fixture = persistFixture(AppointmentStatus.APPROVED);
        saveRecord(fixture, "Older diagnosis", "Old symptoms", "Old receipt",
                LocalDateTime.of(2026, 7, 8, 10, 0));
        saveRecord(fixture, "Newest diagnosis", "New symptoms", "New receipt",
                LocalDateTime.of(2026, 7, 12, 14, 0));

        mockMvc.perform(get("/api/records/patient/{patientId}", fixture.patient().getId())
                        .param("page", "0")
                        .param("size", "10")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].diagnosis").value("Newest diagnosis"))
                .andExpect(jsonPath("$.content[1].diagnosis").value("Older diagnosis"));
    }

    @Test
    void getCurrentDoctorRecords_returnsOnlyAuthenticatedDoctorsRecords() throws Exception {
        Fixture fixture = persistFixture(AppointmentStatus.COMPLETED);
        MedicalRecord ownRecord = saveRecord(fixture, "Own diagnosis", null, null,
                LocalDateTime.of(2026, 7, 11, 11, 0));
        DoctorProfile otherDoctor = saveAdditionalDoctor(fixture.doctor().getDepartment());
        saveRecord(new Fixture(otherDoctor, fixture.patient()), "Other diagnosis", null, null,
                LocalDateTime.of(2026, 7, 12, 11, 0));

        mockMvc.perform(get("/api/records/doctor/mine")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(DOCTOR_FIN).roles("DOCTOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(ownRecord.getId()))
                .andExpect(jsonPath("$.content[0].diagnosis").value("Own diagnosis"));
    }

    @Test
    void updateMedicalRecord_changesOwnedRecord_andPersistsFields() throws Exception {
        Fixture fixture = persistFixture(AppointmentStatus.APPROVED);
        MedicalRecord record = saveRecord(fixture, "Initial diagnosis", "Initial symptoms", "Initial receipt",
                LocalDateTime.of(2026, 7, 10, 12, 0));

        mockMvc.perform(put("/api/records/{id}", record.getId())
                        .with(user(DOCTOR_FIN).roles("DOCTOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "diagnosis", "  Updated diagnosis  ",
                                "symptoms", "Updated symptoms",
                                "receipt", "Updated receipt"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Updated diagnosis"))
                .andExpect(jsonPath("$.symptoms").value("Updated symptoms"))
                .andExpect(jsonPath("$.receipt").value("Updated receipt"));

        MedicalRecord persisted = medicalRecordRepository.findById(record.getId()).orElseThrow();
        assertThat(persisted.getDiagnosis()).isEqualTo("Updated diagnosis");
        assertThat(persisted.getSymptoms()).isEqualTo("Updated symptoms");
        assertThat(persisted.getReceipt()).isEqualTo("Updated receipt");
    }

    @Test
    void getCurrentDoctorRecord_returnsNotFound_forAnotherDoctorsRecord() throws Exception {
        Fixture fixture = persistFixture(AppointmentStatus.APPROVED);
        DoctorProfile otherDoctor = saveAdditionalDoctor(fixture.doctor().getDepartment());
        MedicalRecord otherRecord = saveRecord(
                new Fixture(otherDoctor, fixture.patient()),
                "Private diagnosis",
                null,
                null,
                LocalDateTime.of(2026, 7, 13, 15, 0));

        mockMvc.perform(get("/api/records/doctor/mine/{id}", otherRecord.getId())
                        .with(user(DOCTOR_FIN).roles("DOCTOR")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not found"));
    }

    @Test
    void getCurrentDoctorPatients_returnsEligiblePatientsWithoutDuplicates() throws Exception {
        Fixture fixture = persistFixture(AppointmentStatus.APPROVED);

        Appointment duplicateAppointment = new Appointment();
        duplicateAppointment.setPatient(fixture.patient());
        duplicateAppointment.setDoctor(fixture.doctor());
        duplicateAppointment.setCreatedBy(fixture.patient());
        duplicateAppointment.setType(AppointmentType.WALK_IN);
        duplicateAppointment.setStatus(AppointmentStatus.COMPLETED);
        duplicateAppointment.setStartTime(LocalDateTime.of(2026, 7, 14, 10, 0));
        duplicateAppointment.setEndTime(LocalDateTime.of(2026, 7, 14, 10, 30));
        appointmentRepository.saveAndFlush(duplicateAppointment);

        mockMvc.perform(get("/api/records/doctor/patients")
                        .with(user(DOCTOR_FIN).roles("DOCTOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(fixture.patient().getId()))
                .andExpect(jsonPath("$[0].firstName").value("Aylin"))
                .andExpect(jsonPath("$[0].lastName").value("Mammadova"));
    }

    @Test
    void getMedicalRecord_returnsForbidden_whenUserIsNotAdmin() throws Exception {
        Fixture fixture = persistFixture(AppointmentStatus.APPROVED);
        MedicalRecord record = saveRecord(fixture, "Protected diagnosis", null, null,
                LocalDateTime.of(2026, 7, 10, 16, 0));

        mockMvc.perform(get("/api/records/{id}", record.getId())
                        .with(user(PATIENT_FIN).roles("PATIENT")))
                .andExpect(status().isForbidden());
    }

    private Fixture persistFixture(AppointmentStatus appointmentStatus) {
        Department department = new Department();
        department.setName("Internal Medicine");
        department.setDescription("General adult medicine");
        department.setActive(true);
        Department savedDepartment = departmentRepository.saveAndFlush(department);

        User doctorUser = saveUser(
                DOCTOR_FIN,
                "Kamran",
                "Aliyev",
                "kamran.aliyev@clinify.test",
                "+994501110001");

        User patient = saveUser(
                PATIENT_FIN,
                "Aylin",
                "Mammadova",
                "aylin.mammadova@clinify.test",
                "+994501110002");

        DoctorProfile doctor = new DoctorProfile();
        doctor.setUser(doctorUser);
        doctor.setDepartment(savedDepartment);
        doctor.setSpecialization("Internal Medicine");
        doctor.setBio("Integration test doctor");
        doctor.setExperienceYears(8);
        doctor.setActive(true);
        DoctorProfile savedDoctor = doctorProfileRepository.saveAndFlush(doctor);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(savedDoctor);
        appointment.setCreatedBy(patient);
        appointment.setType(AppointmentType.ONLINE);
        appointment.setStatus(appointmentStatus);
        appointment.setStartTime(LocalDateTime.of(2026, 7, 10, 9, 0));
        appointment.setEndTime(LocalDateTime.of(2026, 7, 10, 9, 30));
        appointment.setReason("Medical record integration test");
        appointmentRepository.saveAndFlush(appointment);

        return new Fixture(savedDoctor, patient);
    }

    private User saveUser(
            String fin,
            String firstName,
            String lastName,
            String email,
            String phoneNumber) {
        User user = new User();
        user.setFin(fin);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setGender("OTHER");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setHasAccount(true);
        return userRepository.saveAndFlush(user);
    }

    private DoctorProfile saveAdditionalDoctor(Department department) {
        User doctorUser = saveUser(
                "DOC0002",
                "Leyla",
                "Hasanova",
                "leyla.hasanova@clinify.test",
                "+994501110003");

        DoctorProfile doctor = new DoctorProfile();
        doctor.setUser(doctorUser);
        doctor.setDepartment(department);
        doctor.setSpecialization("Family Medicine");
        doctor.setBio("Second integration test doctor");
        doctor.setExperienceYears(5);
        doctor.setActive(true);
        return doctorProfileRepository.saveAndFlush(doctor);
    }

    private MedicalRecord saveRecord(
            Fixture fixture,
            String diagnosis,
            String symptoms,
            String receipt,
            LocalDateTime recordDate) {
        MedicalRecord record = new MedicalRecord();
        record.setDoctor(fixture.doctor());
        record.setPatient(fixture.patient());
        record.setDiagnosis(diagnosis);
        record.setSymptoms(symptoms);
        record.setReceipt(receipt);
        record.setRecordDate(recordDate);
        return medicalRecordRepository.saveAndFlush(record);
    }

    private record Fixture(DoctorProfile doctor, User patient) {
    }
}
