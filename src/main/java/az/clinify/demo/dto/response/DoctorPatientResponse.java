package az.clinify.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DoctorPatientResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}