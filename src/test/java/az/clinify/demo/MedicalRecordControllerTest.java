package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.controller.MedicalrecordController;
import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.MedicalRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import az.clinify.demo.dto.request.MedicalRecordStatusRequest;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import az.clinify.demo.dto.response.MedicalRecordSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(MedicalrecordController.class)
@Import(SecurityConfig.class)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

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

        dto.setLabStatus(LabStatuses.PENDING);
        dto.setTestName("Blood Test");

        dto.setRecordDate(LocalDateTime.now());

        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());

        dto.setLabResponses(List.of(
                new LabResponseResponseDTO(
                        1L,
                        1L,
                        30L,
                        "Lab User",
                        "Negative",
                        "Everything normal",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        ));

        return dto;
    }

    private MedicalRecord sampleMedicalRecord() {

        MedicalRecord record = new MedicalRecord();

        record.setId(1L);
        record.setDiagnosis("Updated Diagnosis");
        record.setSymptoms("Updated Symptoms");
        record.setReceipt("Updated Receipt");
        record.setLabStatus(LabStatuses.COMPLETED);
        record.setTestName("Blood Test");

        record.setRecordDate(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        return record;
    }

    @Test
    void createMedicalRecord_ShouldReturnCreated() throws Exception {

        MedicalRecordRequestDTO request =
                new MedicalRecordRequestDTO(
                        10L,
                        "Flu",
                        "Fever",
                        "Medicine",
                        LabStatuses.PENDING,
                        "Blood Test"
                );

        when(medicalRecordService.CreateMedicalRecord(any()))
                .thenReturn(sampleResponse());

        mockMvc.perform(
                        post("/api/records")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "doctor",
                                                null,
                                                List.of(
                                                        new SimpleGrantedAuthority("ROLE_DOCTOR")
                                                )
                                        )
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diagnosis").value("Flu"));

        verify(medicalRecordService)
                .CreateMedicalRecord(any());
    }

    @Test
    void createMedicalRecord_ShouldReturnForbidden_WhenPatient() throws Exception {

        MedicalRecordRequestDTO request =
                new MedicalRecordRequestDTO(
                        10L,
                        "Flu",
                        "Fever",
                        "Medicine",
                        LabStatuses.PENDING,
                        "Blood Test"
                );

        mockMvc.perform(
                        post("/api/records")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "patient",
                                                null,
                                                List.of(
                                                        new SimpleGrantedAuthority("ROLE_PATIENT")
                                                )
                                        )
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());

        verify(medicalRecordService, never())
                .CreateMedicalRecord(any());
    }

    @Test
    void getMedicalRecord_ShouldReturnOk() throws Exception {

        when(medicalRecordService.returnMedicalRecord(1L))
                .thenReturn(sampleResponse());

        mockMvc.perform(
                        get("/api/records/{id}", 1L)
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "doctor",
                                                null,
                                                List.of(
                                                        new SimpleGrantedAuthority("ROLE_DOCTOR")
                                                )
                                        )
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diagnosis").value("Flu"));

        verify(medicalRecordService)
                .returnMedicalRecord(1L);
    }

    @Test
    void updateStatus_ShouldReturnOk_WhenLabTechnician() throws Exception {

        MedicalRecordStatusRequest request =
                new MedicalRecordStatusRequest(
                        LabStatuses.COMPLETED,
                        "Blood Test"
                );

        MedicalRecordResponseDTO response = sampleResponse();
        response.setLabStatus(LabStatuses.COMPLETED);

        when(medicalRecordService.setStatus(
                eq(1L),
                any(MedicalRecordStatusRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        patch("/api/records/{id}/status", 1L)
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "lab",
                                                null,
                                                List.of(
                                                        new SimpleGrantedAuthority("ROLE_LAB_TECHNICIAN")
                                                )
                                        )
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labStatus").value("COMPLETED"));

        verify(medicalRecordService)
                .setStatus(eq(1L), any(MedicalRecordStatusRequest.class));
    }

    @Test
    void updateStatus_ShouldReturnForbidden_WhenPatient() throws Exception {

        MedicalRecordStatusRequest request =
                new MedicalRecordStatusRequest(
                        LabStatuses.COMPLETED,
                        "Blood Test"
                );

        mockMvc.perform(
                        patch("/api/records/{id}/status", 1L)
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "patient",
                                                null,
                                                List.of(
                                                        new SimpleGrantedAuthority("ROLE_PATIENT")
                                                )
                                        )
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());

        verify(medicalRecordService, never())
                .setStatus(any(), any());
    }

    @Test
    void getPatientRecords_ShouldReturnOk_WhenDoctor() throws Exception {

        MedicalRecordSummaryDto summary =
                new MedicalRecordSummaryDto(
                        1L,
                        "Flu",
                        LabStatuses.PENDING,
                        LocalDateTime.now()
                );

        when(medicalRecordService.getPatientMedicalRecords(10L))
                .thenReturn(List.of(summary));

        mockMvc.perform(
                        get("/api/records/patient/{patientId}", 10L)
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "doctor",
                                                null,
                                                List.of(
                                                        new SimpleGrantedAuthority("ROLE_DOCTOR")
                                                )
                                        )
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].diagnosis").value("Flu"));

        verify(medicalRecordService)
                .getPatientMedicalRecords(10L);
    }

    @Test
    void getPatientRecords_ShouldReturnForbidden_WhenPatient() throws Exception {

        mockMvc.perform(
                        get("/api/records/patient/{patientId}", 10L)
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "patient",
                                                null,
                                                List.of(
                                                        new SimpleGrantedAuthority("ROLE_PATIENT")
                                                )
                                        )
                                ))
                )
                .andExpect(status().isForbidden());

        verify(medicalRecordService, never())
                .getPatientMedicalRecords(any());
    }
}