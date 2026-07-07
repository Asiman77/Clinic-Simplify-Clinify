package az.clinify.demo.repository;

import az.clinify.demo.entity.LabResponse;
import az.clinify.demo.enums.LabStatuses;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LabResponseRepository extends JpaRepository<LabResponse, Long> {
    List<LabResponse> findAllByMedicalRecordId(Long medicalRecordId);

    List<LabResponse> findAllByStatus(LabStatuses status);
}