package az.clinify.demo.service;

import az.clinify.demo.dto.request.AppointmentStatusRequest;
import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.entity.Appointment;
import az.clinify.demo.exceptions.AppointmentNotFoundException;
import az.clinify.demo.mapper.AppointmentMapper;
import az.clinify.demo.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AppointmentManagementService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

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
    public AppointmentResponseDTO updateStatus(Long id, AppointmentStatusRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found, id: " + id));

        appointment.setStatus(request.getStatus());

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(updatedAppointment);
    }

}