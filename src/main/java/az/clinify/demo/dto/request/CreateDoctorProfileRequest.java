package az.clinify.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorProfileRequest {
    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Department id is required")
    private Long departmentId;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @Size(max = 2000)
    private String bio;

    @PositiveOrZero(message = "Experience years cannot be negative")
    private Integer experienceYears;

    private Boolean active;

}