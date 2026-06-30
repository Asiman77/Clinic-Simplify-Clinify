package az.clinify.demo.mapper;

import az.clinify.demo.dto.response.DoctorAvailabilityResponse;
import az.clinify.demo.entity.DoctorAvailability;
import org.springframework.stereotype.Component;

@Component
public class DoctorAvailabilityMapper {

    public DoctorAvailabilityResponse toResponse(DoctorAvailability availability) {

        if (availability == null) {
            return null;
        }

        DoctorAvailabilityResponse response = new DoctorAvailabilityResponse();

        response.setId(availability.getId());

        response.setDoctorId(availability.getDoctor().getId());
        response.setDoctorFirstName(
                availability.getDoctor().getUser().getFirstName()
        );
        response.setDoctorLastName(
                availability.getDoctor().getUser().getLastName()
        );

        response.setDayOfWeek(availability.getDayOfWeek());
        response.setStartTime(availability.getStartTime());
        response.setEndTime(availability.getEndTime());
        response.setSlotDurationMinutes(availability.getSlotDurationMinutes());
        response.setAvailabilityType(availability.getAvailabilityType());
        response.setActive(availability.getActive());

        return response;
    }

}