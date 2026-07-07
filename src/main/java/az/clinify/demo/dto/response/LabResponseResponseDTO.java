package az.clinify.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.valueobject.LabResponseFileMetadata;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabResponseResponseDTO {
    private Long id;

    private Long medicalRecordId;

    private Long labTechnicianId;
    private String labTechnicianFullName;

    private String testName;
    private LabStatuses status;

    private String resultText;

    private String note;

    private List<LabResponseFileMetadata> files;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
