package az.clinify.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabResponseRequestDTO {
    @NotNull(message = "Medical record id is required")
    @Positive(message = "Medical record id must be a positive number")
    private Long medicalRecordId;

    @NotBlank(message = "Lab result text is required")
    @Size(max = 5000, message = "Lab result text must not exceed 5000 characters")
    private String resultText;

    @Size(max = 2000, message = "Note must not exceed 2000 characters")
    private String note;
}
