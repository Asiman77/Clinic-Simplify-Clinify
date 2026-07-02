package az.clinify.demo.service;

import az.clinify.demo.mapper.AppointmentMapper;
import az.clinify.demo.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppointmentManagementService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

}