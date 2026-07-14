package az.clinify.demo.repository;

import az.clinify.demo.dto.response.MedicalRecordSummaryDto;
import az.clinify.demo.entity.MedicalRecord;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Page<MedicalRecord> findByDoctorId(
            Long doctorId,
            Pageable pageable);

    Optional<MedicalRecord> findByIdAndDoctorId(
            Long recordId,
            Long doctorId);

    Optional<MedicalRecord> findByIdAndPatientId(
            Long recordId,
            Long patientId);

    @Query(value = """
                SELECT new az.clinify.demo.dto.response.MedicalRecordSummaryDto(
                    m.id,
                    m.diagnosis,
                    m.recordDate,
                    CONCAT(m.doctor.user.firstName, ' ', m.doctor.user.lastName),
                    COUNT(labResponse.id)
                )
                FROM MedicalRecord m
                LEFT JOIN m.labResponses labResponse
                WHERE m.patient.id = :patientId
                GROUP BY
                    m.id,
                    m.diagnosis,
                    m.recordDate,
                    m.doctor.user.firstName,
                    m.doctor.user.lastName
                ORDER BY m.recordDate DESC
            """, countQuery = """
                SELECT COUNT(m)
                FROM MedicalRecord m
                WHERE m.patient.id = :patientId
            """)
    Page<MedicalRecordSummaryDto> findAllSummaryByPatientId(
            @Param("patientId") Long patientId,
            Pageable pageable);
}
