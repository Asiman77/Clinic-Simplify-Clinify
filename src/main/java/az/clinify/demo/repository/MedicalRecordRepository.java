package az.clinify.demo.repository;

import az.clinify.demo.dto.response.MedicalRecordSummaryDto;
import az.clinify.demo.entity.MedicalRecord;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    @Query("""
                SELECT new az.clinify.demo.dto.response.MedicalRecordSummaryDto(
                    m.id,
                    m.diagnosis,
                    m.recordDate
                )
                FROM MedicalRecord m
                WHERE m.patient.id = :patientId
                ORDER BY m.recordDate DESC
            """)
    Page<MedicalRecordSummaryDto> findAllSummaryByPatientId(
            @Param("patientId") Long patientId,
            Pageable pageable);
}