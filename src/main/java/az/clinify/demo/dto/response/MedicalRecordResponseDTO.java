package az.clinify.demo.dto.response;

import az.clinify.demo.enums.LabStatuses;
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
public class MedicalRecordResponseDTO {
    private Long id;

    private Long patientId;
    private String patientFullName;

    private Long doctorId;
    private String doctorFullName;

    private String diagnosis;
    private String symptoms;
    private String receipt;

    private LocalDateTime recordDate;

    private List<LabResponseResponseDTO> labResponses;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
