package az.clinify.demo.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.print.Doc;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.clinify.demo.dto.request.AppointmentRequestDTO;
import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.entity.Appointment;
import az.clinify.demo.entity.DoctorAvailability;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.enums.AppointmentType;
import az.clinify.demo.enums.AvailabilityType;
import az.clinify.demo.exceptions.AppointmentConflictException;
import az.clinify.demo.exceptions.DoctorNotAvailableException;
import az.clinify.demo.exceptions.DoctorNotFoundException;
import az.clinify.demo.exceptions.UserNotFoundException;
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
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new UserNotFoundException("Patient not found with id: " + request.getPatientId()));

        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        User createdBy = patient;
        DayOfWeek dayOfWeek = request.getStartTime().getDayOfWeek();

        List<DoctorAvailability> availabilities = doctorAvailabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(doctor.getId(), dayOfWeek);

        DoctorAvailability matchedAvailability = availabilities.stream()
                .filter(availability -> supportsRequestedType(availability.getAvailabilityType(), request.getType()))
                .filter(availability -> isStartTimeInsideAvailability(request.getStartTime(), availability))
                .filter(availability -> isStartTimeAlignedWithSlotDuration(request.getStartTime(), availability))
                .findFirst()
                .orElseThrow(() -> new DoctorNotAvailableException(
                        "Doctor is not available for the selected time and appointment type"));

        LocalDateTime endTime = request.getStartTime().plusMinutes(matchedAvailability.getSlotDurationMinutes());

        List<AppointmentStatus> blockingStatuses = List.of(AppointmentStatus.REQUESTED, AppointmentStatus.APPROVED);

        boolean hasConflict = appointmentRepository.existsConflictingAppointment(
                doctor.getId(),
                request.getStartTime(),
                endTime,
                blockingStatuses);

        if (hasConflict) {
            throw new AppointmentConflictException("Selected appointment slot is already booked");
        }

        Appointment appointment = appointmentMapper.toEntity(request, patient, doctor, createdBy, endTime);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(savedAppointment);
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

    private boolean isStartTimeInsideAvailability(LocalDateTime startTime, DoctorAvailability availability) {
        LocalTime requestedTime = startTime.toLocalTime();

        return !requestedTime.isBefore(availability.getStartTime())
                && requestedTime.isBefore(availability.getEndTime());
    }

    private boolean isStartTimeAlignedWithSlotDuration(LocalDateTime startTime, DoctorAvailability availability) {
        LocalTime requestedTime = startTime.toLocalTime();

        long minutesFromAvailabilityStart = Duration.between(
                availability.getStartTime(),
                requestedTime).toMinutes();

        return minutesFromAvailabilityStart % availability.getSlotDurationMinutes() == 0;
    }
}
