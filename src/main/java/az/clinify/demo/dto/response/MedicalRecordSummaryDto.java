package az.clinify.demo.dto.response;

import java.time.LocalDateTime;

import az.clinify.demo.enums.LabStatuses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MedicalRecordSummaryDto {
    private Long id;
    private String diagnosis;
    private LabStatuses labStatus;
    private LocalDateTime statusUpdatedAt;
}