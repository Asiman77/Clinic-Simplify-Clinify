package az.clinify.demo.mapper;

import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.entity.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponseDTO toResponse(Appointment appointment) {

        if (appointment == null) {
            return null;
        }

        AppointmentResponseDTO response = new AppointmentResponseDTO();

        response.setId(appointment.getId());


        response.setPatientId(appointment.getPatient().getId());
        response.setPatientFullName(
                appointment.getPatient().getFirstName() + " " +
                        appointment.getPatient().getLastName()
        );


        response.setDoctorId(appointment.getDoctor().getId());
        response.setDoctorFullName(
                appointment.getDoctor().getUser().getFirstName() + " " +
                        appointment.getDoctor().getUser().getLastName()
        );


        if (appointment.getCreatedBy() != null) {
            response.setCreatedById(appointment.getCreatedBy().getId());
            response.setCreatedByFullName(
                    appointment.getCreatedBy().getFirstName() + " " +
                            appointment.getCreatedBy().getLastName()
            );
        }

        response.setType(appointment.getType());
        response.setStatus(appointment.getStatus());
        response.setStartTime(appointment.getStartTime());
        response.setEndTime(appointment.getEndTime());
        response.setReason(appointment.getReason());

        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());

        return response;
    }
}