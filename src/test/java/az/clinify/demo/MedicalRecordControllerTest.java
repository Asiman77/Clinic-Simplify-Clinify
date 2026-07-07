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

}