package az.clinify.demo.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.entity.Appointment;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.exceptions.AppointmentNotFoundException;
import az.clinify.demo.exceptions.BaseBadRequestException;
import az.clinify.demo.exceptions.DoctorNotFoundException;
import az.clinify.demo.exceptions.InvalidStatusException;
import az.clinify.demo.mapper.AppointmentMapper;
import az.clinify.demo.repository.AppointmentRepository;
import az.clinify.demo.repository.DoctorProfileRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorAppointmentService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getCurrentDoctorAppointments(String authenticatedFin, Pageable pageable) {
        DoctorProfile doctor = getCurrentDoctor(authenticatedFin);
        return appointmentRepository
                .findByDoctorId(doctor.getId(), pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional
    public AppointmentResponseDTO approve(Long appointmentId, String authenticatedFin) {
        return transition(appointmentId, authenticatedFin, AppointmentStatus.REQUESTED, AppointmentStatus.APPROVED);
    }

    @Transactional
    public AppointmentResponseDTO reject(
            Long appointmentId,
            String authenticatedFin) {

        return transition(
                appointmentId,
                authenticatedFin,
                AppointmentStatus.REQUESTED,
                AppointmentStatus.REJECTED);
    }

    @Transactional
    public AppointmentResponseDTO complete(Long appointmentId, String authenticatedFin) {
        Appointment appointment = findOwnedAppointment(appointmentId, authenticatedFin);
        requireStatus(appointment, AppointmentStatus.APPROVED);
        if (appointment.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BaseBadRequestException("A future appointment cannot be completed");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(savedAppointment);
    }

    private AppointmentResponseDTO transition(Long appointmentId, String authenticatedFin,
            AppointmentStatus expectedStatus, AppointmentStatus targetStatus) {
        Appointment appointment = findOwnedAppointment(appointmentId, authenticatedFin);
        requireStatus(appointment, expectedStatus);
        appointment.setStatus(targetStatus);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(savedAppointment);
    }

    private Appointment findOwnedAppointment(Long appointmentId, String authenticatedFin) {
        DoctorProfile doctor = getCurrentDoctor(authenticatedFin);
        return appointmentRepository
                .findByIdAndDoctorId(appointmentId, doctor.getId())
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Appointment could not be found"));
    }

    private DoctorProfile getCurrentDoctor(String authenticatedFin) {
        return doctorProfileRepository
                .findByUserFin(authenticatedFin)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor profile could not be found"));
    }

    private void requireStatus(Appointment appointment, AppointmentStatus expectedStatus) {
        if (appointment.getStatus() != expectedStatus) {
            throw new InvalidStatusException("Appointment must be " + expectedStatus + " for this action");
        }
    }
}