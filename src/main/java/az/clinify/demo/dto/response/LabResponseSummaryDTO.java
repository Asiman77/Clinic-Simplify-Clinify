package az.clinify.demo.dto.response;

import az.clinify.demo.enums.LabStatuses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabResponseSummaryDTO {
    private Long id;
    private Long medicalRecordId;
    private Long patientId;
    private String patientFullName;
    private Long doctorId;
    private String doctorFullName;
    private String testName;
    private LabStatuses status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}