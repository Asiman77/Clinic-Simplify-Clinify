package az.clinify.demo.mapper;

import az.clinify.demo.dto.response.DoctorProfileResponse;
import az.clinify.demo.entity.DoctorProfile;
import org.springframework.stereotype.Component;

@Component
public class DoctorProfileMapper {

    public DoctorProfileResponse toResponse(DoctorProfile doctorProfile) {

        if (doctorProfile == null) {
            return null;
        }

        DoctorProfileResponse response = new DoctorProfileResponse();

        response.setId(doctorProfile.getId());

        response.setUserId(doctorProfile.getUser().getId());
        response.setDoctorFirstName(doctorProfile.getUser().getFirstName());
        response.setDoctorLastName(doctorProfile.getUser().getLastName());
        response.setEmail(doctorProfile.getUser().getEmail());

        response.setDepartmentId(doctorProfile.getDepartment().getId());
        response.setDepartmentName(doctorProfile.getDepartment().getName());

        response.setSpecialization(doctorProfile.getSpecialization());
        response.setBio(doctorProfile.getBio());
        response.setExperienceYears(doctorProfile.getExperienceYears());
        response.setActive(doctorProfile.getActive());

        return response;
    }
}