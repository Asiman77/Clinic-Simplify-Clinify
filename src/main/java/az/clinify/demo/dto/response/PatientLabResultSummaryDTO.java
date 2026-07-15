package az.clinify.demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.valueobject.LabResponseFileMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientLabResultSummaryDTO {

    private Long id;
    private Long medicalRecordId;
    private String diagnosis;
    private LocalDateTime recordDate;
    private String testName;
    private LabStatuses status;
    private String resultText;
    private String note;
    private List<LabResponseFileMetadata> files;
}