package az.clinify.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceptionRegisterRequest {

    @NotBlank(message = "please insert the fin")
    private String fin;

    @NotBlank(message = "insert the firstname")
    private String firstName;

    @NotBlank(message = "insert the lastname")
    private String lastName;

    @NotBlank(message = "instert the birthdate correctly")
    private LocalDate birthDate;

    @NotBlank(message = "instert the gender")
    private String gender;
}