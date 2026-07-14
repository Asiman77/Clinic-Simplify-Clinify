package az.clinify.demo;

import az.clinify.demo.entity.Appointment;
import az.clinify.demo.entity.Department;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.enums.AppointmentType;
import az.clinify.demo.repository.AppointmentRepository;
import az.clinify.demo.repository.DepartmentRepository;
import az.clinify.demo.repository.DoctorProfileRepository;
import az.clinify.demo.repository.MedicalRecordRepository;
import az.clinify.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
