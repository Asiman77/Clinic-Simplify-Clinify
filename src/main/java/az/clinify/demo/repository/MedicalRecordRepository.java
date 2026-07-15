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
                mr.id,
                mr.diagnosis,
                mr.recordDate,
                CONCAT(
                    CONCAT(doctorUser.firstName, ' '),
                    doctorUser.lastName
                ),
                COUNT(labResponse.id)
            )
            FROM MedicalRecord mr
            JOIN mr.doctor doctor
            JOIN doctor.user doctorUser
            LEFT JOIN mr.labResponses labResponse
            WHERE mr.patient.id = :patientId
            GROUP BY
                mr.id,
                mr.diagnosis,
                mr.recordDate,
                doctorUser.firstName,
                doctorUser.lastName
            """, countQuery = """
            SELECT COUNT(mr)
            FROM MedicalRecord mr
            WHERE mr.patient.id = :patientId
            """)
    Page<MedicalRecordSummaryDto> findAllSummaryByPatientId(
            @Param("patientId") Long patientId,
            Pageable pageable);
}
