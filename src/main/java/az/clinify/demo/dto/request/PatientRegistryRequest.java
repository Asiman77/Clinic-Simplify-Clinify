package az.clinify.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegistryRequest {
    @NotBlank(message = "FIN can not be empty")
    @Size(min = 7,max =7,message ="FIN must be exactly 7 characters")
    private String fin;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message ="Gender must be selected")
    private String gender;

    @NotNull(message ="Birth date is required")
    private LocalDate birthDate;

    @NotBlank(message ="Phone number cannot be blank")
    private String phoneNumber;

}
