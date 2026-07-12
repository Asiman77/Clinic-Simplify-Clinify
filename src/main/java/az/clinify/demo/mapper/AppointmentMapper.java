package az.clinify.demo.mapper;

import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.entity.Appointment;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.enums.AppointmentType;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(
            User patient,
            DoctorProfile doctor,
            User createdBy,
            AppointmentType type,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String reason) {

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setCreatedBy(createdBy);
        appointment.setType(type);
        appointment.setStatus(AppointmentStatus.REQUESTED);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setReason(reason);

        return appointment;
    }

    public AppointmentResponseDTO toResponse(Appointment appointment) {

        if (appointment == null) {
            return null;
        }

        AppointmentResponseDTO response = new AppointmentResponseDTO();

        response.setId(appointment.getId());

        response.setPatientId(appointment.getPatient().getId());
        response.setPatientFullName(
                appointment.getPatient().getFirstName() + " " +
                        appointment.getPatient().getLastName());

        response.setDoctorId(appointment.getDoctor().getId());
        response.setDoctorFullName(
                appointment.getDoctor().getUser().getFirstName() + " " +
                        appointment.getDoctor().getUser().getLastName());

        if (appointment.getCreatedBy() != null) {
            response.setCreatedById(appointment.getCreatedBy().getId());
            response.setCreatedByFullName(
                    appointment.getCreatedBy().getFirstName() + " " +
                            appointment.getCreatedBy().getLastName());
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