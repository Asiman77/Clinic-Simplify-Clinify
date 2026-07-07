package az.clinify.demo.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceptionRegisterRequest {

    private String fin;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
    private String password;

}