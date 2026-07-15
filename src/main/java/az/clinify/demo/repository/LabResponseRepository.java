package az.clinify.demo.repository;

import az.clinify.demo.entity.LabResponse;
import az.clinify.demo.enums.LabStatuses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LabResponseRepository extends JpaRepository<LabResponse, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "medicalRecord",
            "medicalRecord.patient",
            "medicalRecord.doctor",
            "medicalRecord.doctor.user",
            "labTechnician"
    })
    Page<LabResponse> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {
            "medicalRecord",
            "medicalRecord.patient",
            "medicalRecord.doctor",
            "medicalRecord.doctor.user",
            "labTechnician"
    })
    Optional<LabResponse> findById(Long id);

    @EntityGraph(attributePaths = {
            "medicalRecord",
            "medicalRecord.patient",
            "medicalRecord.doctor",
            "medicalRecord.doctor.user",
            "labTechnician"
    })
    Page<LabResponse> findAllByStatusIn(
            List<LabStatuses> statuses,
            Pageable pageable);

    @EntityGraph(attributePaths = {
            "medicalRecord",
            "labTechnician"
    })
    List<LabResponse> findAllByMedicalRecordId(
            Long medicalRecordId);

    @EntityGraph(attributePaths = { "medicalRecord" })
    Page<LabResponse> findAllByMedicalRecordPatientId(
            Long patientId,
            Pageable pageable);
}