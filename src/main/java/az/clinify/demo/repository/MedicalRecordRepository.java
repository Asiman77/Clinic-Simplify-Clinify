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
            SELECT
                mr.id,
                mr.diagnosis,
                mr.record_date,
                CONCAT(u.first_name, ' ', u.last_name) AS doctor_name,
                COUNT(lr.id) AS lab_response_count
            FROM medical_records mr
            LEFT JOIN doctor_profiles dp
                ON mr.doctor_id = dp.id
            LEFT JOIN users u
                ON dp.user_id = u.id
            LEFT JOIN lab_responses lr
                ON lr.medical_record_id = mr.id
            WHERE mr.patient_id = :patientId
            GROUP BY
                mr.id,
                mr.diagnosis,
                mr.record_date,
                u.first_name,
                u.last_name
            ORDER BY mr.record_date DESC
            """, countQuery = """
                SELECT COUNT(*)
                FROM medical_records
                WHERE patient_id = :patientId
            """, nativeQuery = true)
    Page<MedicalRecordSummaryDto> findAllSummaryByPatientId(
            @Param("patientId") Long patientId,
            Pageable pageable);
}
