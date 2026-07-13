package az.clinify.demo.service;

import az.clinify.demo.dto.request.UpdateMedicalRecordRequest;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.dto.response.MedicalRecordSummaryDto;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.exceptions.MedicalRecordNotFoundException;
import az.clinify.demo.mapper.MedicalRecordMapper;
import az.clinify.demo.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalRecordResponseDTO returnMedicalRecord(Long id) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException("This medical record not found"));
        return medicalRecordMapper.toResponse(medicalRecord);
    }

    @Transactional
    public MedicalRecord updateMedicalRecord(Long recordId, UpdateMedicalRecordRequest dto, String currentDoctorFin) {
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new MedicalRecordNotFoundException("This medical record did not found"));

        String recordDoctorFin = record.getDoctor().getUser().getFin();

        if (!recordDoctorFin.equals(currentDoctorFin)) {
            throw new AccessDeniedException("You can not edit this medical record!");
        }

        if (dto.getDiagnosis() != null && !dto.getDiagnosis().trim().isEmpty()) {
            record.setDiagnosis(dto.getDiagnosis());
        }

        if (dto.getSymptoms() != null) {
            record.setSymptoms(dto.getSymptoms());
        }

        if (dto.getReceipt() != null) {
            record.setReceipt(dto.getReceipt());
        }

        return medicalRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public Page<MedicalRecordSummaryDto> getPatientMedicalRecords(Long patientId, Pageable pageable) {
        return medicalRecordRepository.findAllSummaryByPatientId(patientId, pageable);
    }

}
