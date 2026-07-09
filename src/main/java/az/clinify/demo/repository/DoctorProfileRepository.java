package az.clinify.demo.repository;

import az.clinify.demo.entity.DoctorProfile;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    Optional<DoctorProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<DoctorProfile> findByActiveTrue();

    List<DoctorProfile> findByDepartmentIdAndActiveTrue(Long departmentId);

    Page<DoctorProfile> findByActiveTrue(Pageable pageable);

    @Query("""
                SELECT d
                FROM DoctorProfile d
                WHERE (:departmentId IS NULL OR d.department.id = :departmentId)
                  AND (:specialization IS NULL OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%')))
                  AND (:experienceYears IS NULL OR d.experienceYears = :experienceYears)
            """)
    Page<DoctorProfile> findByFilters(
            @Param("departmentId") Long departmentId,
            @Param("specialization") String specialization,
            @Param("experienceYears") Integer experienceYears,
            Pageable pageable);

}