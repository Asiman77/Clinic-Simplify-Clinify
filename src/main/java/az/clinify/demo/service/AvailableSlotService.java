package az.clinify.demo.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.clinify.demo.dto.response.AvailableSlotResponse;
import az.clinify.demo.entity.Appointment;
import az.clinify.demo.entity.DoctorAvailability;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.enums.AppointmentType;
import az.clinify.demo.enums.AvailabilityType;
import az.clinify.demo.exceptions.DoctorNotFoundException;
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
    public List<AvailableSlotResponse> getAvailableSlots(Long doctorId, LocalDate date, AppointmentType type) {
        doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        List<DoctorAvailability> availabilities = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(doctorId, dayOfWeek);

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        List<AppointmentStatus> blockingStatuses = List.of(AppointmentStatus.REQUESTED, AppointmentStatus.APPROVED);

        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndStartTimeBetweenAndStatusIn(doctorId, dayStart, dayEnd, blockingStatuses);

        return List.of();
    }

    private boolean supportsRequestedType(AvailabilityType availabilityType, AppointmentType requestedType) {
        if (requestedType == AppointmentType.ONLINE) {
            return availabilityType == AvailabilityType.ONLINE_ONLY
                    || availabilityType == AvailabilityType.MIXED;
        }

        if (requestedType == AppointmentType.WALK_IN) {
            return availabilityType == AvailabilityType.WALK_IN_ONLY
                    || availabilityType == AvailabilityType.MIXED;
        }

        return false;
    }

    private boolean isOverlapping(LocalDateTime slotStart, LocalDateTime slotEnd, LocalDateTime appointmentStart,
            LocalDateTime appointmentEnd) {
        return slotStart.isBefore(appointmentEnd) && slotEnd.isAfter(appointmentStart);
    }
}
