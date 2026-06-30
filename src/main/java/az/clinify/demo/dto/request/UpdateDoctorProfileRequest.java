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
public class UpdateDoctorProfileRequest {

    @NotNull(message = "Department id is required")
    private Long departmentId;

    @NotBlank(message = "Specialization is required")
    @Size(max = 100, message = "Specialization cannot exceed 100 characters")
    private String specialization;

    @Size(max = 2000, message = "Bio cannot exceed 2000 characters")
    private String bio;

    @NotNull(message = "Experience years is required")
    @PositiveOrZero(message = "Experience years cannot be negative")
    private Integer experienceYears;

    @NotNull(message = "Active status is required")
    private Boolean active;
}