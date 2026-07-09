package az.clinify.demo.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorProfileFilterRequest {

    private Long departmentId;
    private String specialization;
    private Integer experienceYears;
    private boolean isActive;
}