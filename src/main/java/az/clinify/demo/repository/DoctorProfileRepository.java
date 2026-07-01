package az.clinify.demo.repository;

import az.clinify.demo.entity.DoctorProfile;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    Optional<DoctorProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<DoctorProfile> findByActiveTrue();

    List<DoctorProfile> findByDepartmentIdAndActiveTrue(Long departmentId);
}