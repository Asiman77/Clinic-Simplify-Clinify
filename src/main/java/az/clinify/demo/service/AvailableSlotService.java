package az.clinify.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.clinify.demo.dto.response.AvailableSlotResponse;
import az.clinify.demo.enums.AppointmentType;
import az.clinify.demo.repository.AppointmentRepository;
import az.clinify.demo.repository.DoctorAvailabilityRepository;
import az.clinify.demo.repository.DoctorProfileRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailableSlotService {
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public List<AvailableSlotResponse> getAvailableSlots(
            Long doctorId,
            LocalDate date,
            AppointmentType type) {
        return List.of();
    }
}
