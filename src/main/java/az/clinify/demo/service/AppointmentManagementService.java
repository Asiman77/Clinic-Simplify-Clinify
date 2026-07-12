package az.clinify.demo.service;

import az.clinify.demo.dto.request.AppointmentStatusRequest;
import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.entity.Appointment;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.exceptions.AppointmentNotFoundException;
import az.clinify.demo.exceptions.BaseBadRequestException;
import az.clinify.demo.exceptions.InvalidStatusException;
import az.clinify.demo.exceptions.UserNotFoundException;
import az.clinify.demo.mapper.AppointmentMapper;
import az.clinify.demo.repository.AppointmentRepository;
import az.clinify.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AppointmentManagementService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getCurrentPatientAppointments(
            String authenticatedFin,
            Pageable pageable) {

        User patient = userRepository.findByFin(authenticatedFin)
                .orElseThrow(() -> new UserNotFoundException(
                        "Authenticated patient could not be found"));

        return appointmentRepository
                .findByPatientId(patient.getId(), pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found, id: " + id));

        return appointmentMapper.toResponse(appointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getByPatient(Long patientId, Pageable pageable) {
        return appointmentRepository.findByPatientId(patientId, pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getByDoctor(Long doctorId, Pageable pageable) {
        return appointmentRepository.findByDoctorId(doctorId, pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional
    public AppointmentResponseDTO cancelCurrentPatientAppointment(
            Long appointmentId,
            String authenticatedFin) {
        User patient = userRepository.findByFin(authenticatedFin)
                .orElseThrow(() -> new UserNotFoundException(
                        "Authenticated patient could not be found"));

        Appointment appointment = appointmentRepository
                .findByIdAndPatientId(
                        appointmentId,
                        patient.getId())
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Appointment could not be found"));

        AppointmentStatus currentStatus = appointment.getStatus();

        boolean canBeCancelled = currentStatus == AppointmentStatus.REQUESTED
                || currentStatus == AppointmentStatus.APPROVED;

        if (!canBeCancelled) {
            throw new InvalidStatusException(
                    "Only requested or approved appointments can be cancelled");
        }

        if (!appointment.getStartTime()
                .isAfter(LocalDateTime.now())) {
            throw new BaseBadRequestException(
                    "Started or past appointments cannot be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(savedAppointment);
    }

    @Transactional
    public AppointmentResponseDTO updateStatus(Long id, AppointmentStatusRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found, id: " + id));

        appointment.setStatus(request.getStatus());

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(updatedAppointment);
    }

}