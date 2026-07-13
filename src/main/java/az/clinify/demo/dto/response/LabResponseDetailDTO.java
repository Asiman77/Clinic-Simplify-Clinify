package az.clinify.demo.dto.response;

import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.valueobject.LabResponseFileMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabResponseDetailDTO {
    private Long id;
    private Long medicalRecordId;
    private Long patientId;
    private String patientFullName;
    private Long doctorId;
    private String doctorFullName;
    private Long labTechnicianId;
    private String labTechnicianFullName;
    private String diagnosis;
    private String symptoms;
    private String testName;
    private LabStatuses status;
    private String resultText;
    private String note;
    private List<LabResponseFileMetadata> files;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}