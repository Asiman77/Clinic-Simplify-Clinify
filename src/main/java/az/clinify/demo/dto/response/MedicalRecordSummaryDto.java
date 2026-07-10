package az.clinify.demo.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MedicalRecordSummaryDto {
    private Long id;
    private String diagnosis;
    private LocalDateTime recordDate;
}