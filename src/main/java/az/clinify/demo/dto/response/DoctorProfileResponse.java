package az.clinify.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileResponse {

    private Long id;

    private Long userId;

    private String doctorFirstName;

    private String doctorLastName;

    private String email;

    private Long departmentId;

    private String departmentName;

    private String specialization;

    private String bio;

    private Integer experienceYears;

    private Boolean active;

}