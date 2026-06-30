package az.clinify.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String fin;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate birthDate;
    private String phoneNumber;
    private String email;
    private boolean hasAccount;
    //admin teyin edir
    private Set<String> roles;

}
