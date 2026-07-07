package az.clinify.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

public class CreateDepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(max = 150, message = "Department name cannot exceed 150 characters")
    private String name;

    @Size(max = 1000, message = "Description is too long")
    private String description;

    private Boolean active;

    public CreateDepartmentRequest(String name, String description, Boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }
}