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
        dto.setRecordDate(LocalDateTime.now());
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());


        LabResponseResponseDTO labResponse = new LabResponseResponseDTO(
                1L,               // id
                1L,               // medicalRecordId
                30L,              // labTechnicianId
                "Lab User",       // labTechnicianFullName
                "Blood Test",     // testName
                LabStatuses.PENDING, // status
                "Negative",       // resultText
                "Everything normal", // note
                new ArrayList<>(), // files (metadata list)
                LocalDateTime.now(), // createdAt
                LocalDateTime.now()  // updatedAt
        );

        dto.setLabResponses(List.of(labResponse));
        return dto;
    }

    private MedicalRecord sampleMedicalRecord() {
        MedicalRecord record = new MedicalRecord();
        record.setId(1L);
        record.setDiagnosis("Updated Diagnosis");
        record.setSymptoms("Updated Symptoms");
        record.setReceipt("Updated Receipt");
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
                        new ArrayList<>() // labTests siyahısı
                );

        when(medicalRecordService.CreateMedicalRecord(any()))
                .thenReturn(sampleResponse());

        mockMvc.perform(
                        post("/api/records")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "doctor",
                                                null,
                                                List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                                        )
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diagnosis").value("Flu"));

        verify(medicalRecordService).CreateMedicalRecord(any());
    }

    @Test
    void createMedicalRecord_ShouldReturnForbidden_WhenPatient() throws Exception {
        MedicalRecordRequestDTO request =
                new MedicalRecordRequestDTO(
                        10L,
                        "Flu",
                        "Fever",
                        "Medicine",
                        new ArrayList<>()
                );

        mockMvc.perform(
                        post("/api/records")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "patient",
                                                null,
                                                List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
                                        )
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());

        verify(medicalRecordService, never()).CreateMedicalRecord(any());
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
                                                List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                                        )
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diagnosis").value("Flu"));

        verify(medicalRecordService).returnMedicalRecord(1L);
    }

@Test
void getPatientRecords_ShouldReturnOk_WhenDoctor() throws Exception {

    MedicalRecordSummaryDto summary =
            new MedicalRecordSummaryDto(
                    1L,
                    "Flu",
                    LocalDateTime.now()
            );

    Page<MedicalRecordSummaryDto> page = new PageImpl<>(
            List.of(summary),
            PageRequest.of(0, 10),
            1
    );

    when(medicalRecordService.getPatientMedicalRecords(eq(10L), any(Pageable.class)))
            .thenReturn(page);

    mockMvc.perform(
                    get("/api/records/patient/{patientId}", 10L)
                            .param("page", "0")
                            .param("size", "10")
                            .with(authentication(
                                    new UsernamePasswordAuthenticationToken(
                                            "doctor",
                                            null,
                                            List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                                    )
                            ))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].diagnosis").value("Flu"))
            .andExpect(jsonPath("$.totalElements").value(1));

    verify(medicalRecordService)
            .getPatientMedicalRecords(eq(10L), any(Pageable.class));
}

    @Test
    void updateRecord_ShouldReturnOk_WhenDoctor() throws Exception {

        UpdateMedicalRecordRequest request =
                new UpdateMedicalRecordRequest(
                        "Updated Diagnosis",
                        "Updated Symptoms",
                        "Updated Receipt"
                );

        when(medicalRecordService.updateMedicalRecord(
                eq(1L),
                any(UpdateMedicalRecordRequest.class),
                anyString()
        )).thenReturn(sampleMedicalRecord());

        mockMvc.perform(
                        put("/api/records/{id}", 1L)
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "doctor",
                                                null,
                                                List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                                        )
                                ))
                                .principal(() -> "doctor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(medicalRecordService)
                .updateMedicalRecord(
                        eq(1L),
                        any(UpdateMedicalRecordRequest.class),
                        eq("doctor")
                );
    }

    @Test
    void updateRecord_ShouldReturnForbidden_WhenPatient() throws Exception {
        UpdateMedicalRecordRequest request =
                new UpdateMedicalRecordRequest(
                        "Updated Diagnosis",
                        "Updated Symptoms",
                        "Updated Receipt"
                );

        mockMvc.perform(
                        put("/api/records/{id}", 1L)
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "patient",
                                                null,
                                                List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
                                        )
                                ))
                                .principal(() -> "patient")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());

        verify(medicalRecordService, never()).updateMedicalRecord(any(), any(), anyString());
    }
}