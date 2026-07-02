package az.clinify.demo.repository;

import az.clinify.demo.entity.Appointment;
import az.clinify.demo.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorIdAndStartTimeBetweenAndStatusIn(
            Long doctorId,
            LocalDateTime dayStart,
            LocalDateTime dayEnd,
            List<AppointmentStatus> statuses);

    List<Appointment> findByPatientId(Long patientId);
}