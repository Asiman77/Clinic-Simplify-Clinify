package az.clinify.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLabTestRequest {
    @NotBlank(message = "Test name is required")
    @Size(max = 255, message = "Test name must not exceed 255 characters")
    private String testName;

    @Size(max = 2000, message = "Note must not exceed 2000 characters")
    private String note;
}
