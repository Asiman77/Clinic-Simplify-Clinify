package az.clinify.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.clinify.demo.dto.request.AppointmentRequestDTO;
import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.mapper.AppointmentMapper;
import az.clinify.demo.repository.AppointmentRepository;
import az.clinify.demo.repository.DoctorAvailabilityRepository;
import az.clinify.demo.repository.DoctorProfileRepository;
import az.clinify.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentBookingService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        return null;
    }
}
