package az.clinify.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabResponseResponseDTO {
    private Long id;

    private Long medicalRecordId;

    private Long labTechnicianId;
    private String labTechnicianFullName;

    private String resultText;

    private String note;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
