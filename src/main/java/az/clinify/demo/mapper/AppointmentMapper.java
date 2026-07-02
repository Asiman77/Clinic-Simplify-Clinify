package az.clinify.demo.mapper;

import az.clinify.demo.dto.request.AppointmentRequestDTO;
import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.entity.Appointment;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.AppointmentStatus;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequestDTO request, User patient, DoctorProfile doctor, User createdBy,
            LocalDateTime endTime) {
        if (request == null) {
            return null;
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setCreatedBy(createdBy);
        appointment.setType(request.getType());
        appointment.setStatus(AppointmentStatus.REQUESTED);
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(endTime);
        appointment.setReason(request.getReason());
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