package az.clinify.demo.service;

import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.mapper.AppointmentMapper;
import az.clinify.demo.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AppointmentManagementService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public List<AppointmentResponseDTO> getByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Transactional
    public List<AppointmentResponseDTO> getByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

}