package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.controller.MedicalrecordController;
import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.dto.response.DoctorPatientResponse;
import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.service.DoctorMedicalRecordService;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.MedicalRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import az.clinify.demo.dto.response.MedicalRecordSummaryDto;
import az.clinify.demo.dto.request.UpdateMedicalRecordRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(MedicalrecordController.class)
@Import(SecurityConfig.class)
class MedicalRecordControllerTest {

        @Autowired
        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        @MockitoBean
        private MedicalRecordService medicalRecordService;

        @MockitoBean
        private JwtTokenProvider jwtTokenProvider;

        @MockitoBean
        private DoctorMedicalRecordService doctorMedicalRecordService;

        @MockitoBean
        private UserDetailsService userDetailsService;

        private MedicalRecordResponseDTO sampleResponse() {
                MedicalRecordResponseDTO dto = new MedicalRecordResponseDTO();
                dto.setId(1L);
                dto.setPatientId(10L);
                dto.setPatientFullName("Ali Aliyev");
                dto.setDoctorId(20L);
                dto.setDoctorFullName("Dr. Vusal");
                dto.setDiagnosis("Flu");
                dto.setSymptoms("Fever");
                dto.setReceipt("Medicine");
                dto.setRecordDate(LocalDateTime.now());
                dto.setCreatedAt(LocalDateTime.now());
                dto.setUpdatedAt(LocalDateTime.now());

                LabResponseResponseDTO labResponse = new LabResponseResponseDTO(
                                1L, // id
                                1L, // medicalRecordId
                                30L, // labTechnicianId
                                "Lab User", // labTechnicianFullName
                                "Blood Test", // testName
                                LabStatuses.PENDING, // status
                                "Negative", // resultText
                                "Everything normal", // note
                                new ArrayList<>(), // files (metadata list)
                                LocalDateTime.now(), // createdAt
                                LocalDateTime.now() // updatedAt
                );

                dto.setLabResponses(List.of(labResponse));
                return dto;
        }

        private UsernamePasswordAuthenticationToken doctorAuthentication() {
                return new UsernamePasswordAuthenticationToken(
                                "doctor",
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_DOCTOR")));
        }

        @Test
        void createMedicalRecord_ShouldReturnCreated() throws Exception {

                MedicalRecordRequestDTO request = new MedicalRecordRequestDTO(
                                10L,
                                "Flu",
                                "Fever",
                                "Medicine",
                                new ArrayList<>() // labTests siyahısı
                );

                when(doctorMedicalRecordService.create(
                                any(MedicalRecordRequestDTO.class),
                                eq("doctor")))
                                .thenReturn(sampleResponse());

                mockMvc.perform(
                                post("/api/records")
                                                .with(authentication(
                                                                new UsernamePasswordAuthenticationToken(
                                                                                "doctor",
                                                                                null,
                                                                                List.of(new SimpleGrantedAuthority(
                                                                                                "ROLE_DOCTOR")))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.diagnosis").value("Flu"));

                verify(doctorMedicalRecordService).create(
                                any(MedicalRecordRequestDTO.class),
                                eq("doctor"));
        }

        @Test
        void createMedicalRecord_ShouldReturnForbidden_WhenPatient() throws Exception {
                MedicalRecordRequestDTO request = new MedicalRecordRequestDTO(
                                10L,
                                "Flu",
                                "Fever",
                                "Medicine",
                                new ArrayList<>());

                mockMvc.perform(
                                post("/api/records")
                                                .with(authentication(
                                                                new UsernamePasswordAuthenticationToken(
                                                                                "patient",
                                                                                null,
                                                                                List.of(new SimpleGrantedAuthority(
                                                                                                "ROLE_PATIENT")))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());

                verify(doctorMedicalRecordService, never())
                                .create(any(MedicalRecordRequestDTO.class), anyString());
        }

        @Test
        void getMedicalRecord_ShouldReturnOk_WhenAdmin() throws Exception {
                when(medicalRecordService.returnMedicalRecord(1L))
                                .thenReturn(sampleResponse());

                mockMvc.perform(
                                get("/api/records/{id}", 1L)
                                                .with(authentication(
                                                                new UsernamePasswordAuthenticationToken("admin", null,
                                                                                List.of(new SimpleGrantedAuthority(
                                                                                                "ROLE_ADMIN"))))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.diagnosis").value("Flu"));

                verify(medicalRecordService).returnMedicalRecord(1L);
        }

        @Test
        void getPatientRecords_ShouldReturnOk_WhenAdmin() throws Exception {

                MedicalRecordSummaryDto summary = new MedicalRecordSummaryDto(
                                1L,
                                "Flu",
                                LocalDateTime.now(),
                                "Dr. Vusal",
                                1L);

                Page<MedicalRecordSummaryDto> page = new PageImpl<>(
                                List.of(summary),
                                PageRequest.of(0, 10),
                                1);

                when(medicalRecordService.getPatientMedicalRecords(eq(10L), any(Pageable.class)))
                                .thenReturn(page);

                mockMvc.perform(
                                get("/api/records/patient/{patientId}", 10L)
                                                .param("page", "0")
                                                .param("size", "10")
                                                .with(authentication(
                                                                new UsernamePasswordAuthenticationToken(
                                                                                "admin",
                                                                                null,
                                                                                List.of(new SimpleGrantedAuthority(
                                                                                                "ROLE_ADMIN"))))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].diagnosis").value("Flu"))
                                .andExpect(jsonPath("$.content[0].doctorFullName").value("Dr. Vusal"))
                                .andExpect(jsonPath("$.content[0].labTestCount").value(1))
                                .andExpect(jsonPath("$.totalElements").value(1));

                verify(medicalRecordService)
                                .getPatientMedicalRecords(eq(10L), any(Pageable.class));
        }

        @Test
        void updateRecord_ShouldReturnOk_WhenDoctor() throws Exception {
                UpdateMedicalRecordRequest request = new UpdateMedicalRecordRequest(
                                "Updated Diagnosis",
                                "Updated Symptoms",
                                "Updated Receipt");

                when(doctorMedicalRecordService.update(
                                eq(1L),
                                any(UpdateMedicalRecordRequest.class),
                                eq("doctor")))
                                .thenReturn(sampleResponse());
                mockMvc.perform(
                                put("/api/records/{id}", 1L)
                                                .with(authentication(
                                                                new UsernamePasswordAuthenticationToken(
                                                                                "doctor",
                                                                                null,
                                                                                List.of(new SimpleGrantedAuthority(
                                                                                                "ROLE_DOCTOR")))))
                                                .principal(() -> "doctor")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());

                verify(doctorMedicalRecordService)
                                .update(eq(1L), any(UpdateMedicalRecordRequest.class), eq("doctor"));
        }

        @Test
        void updateRecord_ShouldReturnForbidden_WhenPatient() throws Exception {
                UpdateMedicalRecordRequest request = new UpdateMedicalRecordRequest(
                                "Updated Diagnosis",
                                "Updated Symptoms",
                                "Updated Receipt");

                mockMvc.perform(
                                put("/api/records/{id}", 1L)
                                                .with(authentication(
                                                                new UsernamePasswordAuthenticationToken(
                                                                                "patient",
                                                                                null,
                                                                                List.of(new SimpleGrantedAuthority(
                                                                                                "ROLE_PATIENT")))))
                                                .principal(() -> "patient")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());

                verify(doctorMedicalRecordService, never())
                                .update(any(), any(UpdateMedicalRecordRequest.class), anyString());
        }

        @Test
        void getCurrentDoctorRecords_ShouldReturnOk_WhenDoctor()
                        throws Exception {

                Page<MedicalRecordResponseDTO> page = new PageImpl<>(
                                List.of(sampleResponse()),
                                PageRequest.of(0, 10),
                                1);

                when(doctorMedicalRecordService.getCurrentDoctorRecords(
                                eq("doctor"), any(Pageable.class)))
                                .thenReturn(page);

                mockMvc.perform(get("/api/records/doctor/mine")
                                .with(authentication(doctorAuthentication())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalElements").value(1))
                                .andExpect(jsonPath("$.content[0].id").value(1));

                verify(doctorMedicalRecordService)
                                .getCurrentDoctorRecords(
                                                eq("doctor"), any(Pageable.class));
        }

        @Test
        void getCurrentDoctorRecord_ShouldReturnOk_WhenDoctor()
                        throws Exception {

                when(doctorMedicalRecordService
                                .getCurrentDoctorRecord(1L, "doctor"))
                                .thenReturn(sampleResponse());

                mockMvc.perform(get("/api/records/doctor/mine/{id}", 1L)
                                .with(authentication(doctorAuthentication())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));

                verify(doctorMedicalRecordService)
                                .getCurrentDoctorRecord(1L, "doctor");
        }

        @Test
        void getCurrentDoctorPatients_ShouldReturnOk_WhenDoctor()
                        throws Exception {

                when(doctorMedicalRecordService.getCurrentDoctorPatients("doctor"))
                                .thenReturn(List.of(
                                                new DoctorPatientResponse(
                                                                10L, "Ali", "Aliyev", "ali@example.com")));

                mockMvc.perform(get("/api/records/doctor/patients")
                                .with(authentication(doctorAuthentication())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(10))
                                .andExpect(jsonPath("$[0].firstName").value("Ali"));

                verify(doctorMedicalRecordService)
                                .getCurrentDoctorPatients("doctor");
        }
}
