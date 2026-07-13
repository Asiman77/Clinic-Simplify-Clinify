package az.clinify.demo.repository;

import az.clinify.demo.entity.Appointment;
import az.clinify.demo.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
        List<Appointment> findByDoctorIdAndStartTimeBetweenAndStatusIn(
                        Long doctorId,
                        LocalDateTime dayStart,
                        LocalDateTime dayEnd,
                        List<AppointmentStatus> statuses);

        Optional<Appointment> findByIdAndPatientId(
                        Long appointmentId,
                        Long patientId);

        Optional<Appointment> findByIdAndDoctorId(
                        Long appointmentId,
                        Long doctorId);

        @Query("""
                        SELECT COUNT(a) > 0
                        FROM Appointment a
                        WHERE a.doctor.id = :doctorId
                          AND a.startTime < :requestedEndTime
                          AND a.endTime > :requestedStartTime
                          AND a.status IN :statuses
                        """)
        boolean existsConflictingAppointment(
                        @Param("doctorId") Long doctorId,
                        @Param("requestedStartTime") LocalDateTime requestedStartTime,
                        @Param("requestedEndTime") LocalDateTime requestedEndTime,
                        @Param("statuses") List<AppointmentStatus> statuses);

        Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

        Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);
}