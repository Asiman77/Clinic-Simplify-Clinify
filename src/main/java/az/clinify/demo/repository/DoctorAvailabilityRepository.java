package az.clinify.demo.repository;

import az.clinify.demo.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctorId(Long doctorId);

    List<DoctorAvailability> findByDoctorIdAndDayOfWeekAndActiveTrue(Long doctorId, DayOfWeek dayOfWeek);
}