package az.clinify.demo.repository;

import az.clinify.demo.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    Optional<DoctorAvailability>findByDoctorId(Long aLong);

}