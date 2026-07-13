package az.clinify.demo.service;

import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.dto.response.MedicalRecordSummaryDto;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.exceptions.MedicalRecordNotFoundException;
import az.clinify.demo.mapper.MedicalRecordMapper;
import az.clinify.demo.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO returnMedicalRecord(Long id) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException("This medical record not found"));
        return medicalRecordMapper.toResponse(medicalRecord);
    }

    @Transactional(readOnly = true)
    public Page<MedicalRecordSummaryDto> getPatientMedicalRecords(Long patientId, Pageable pageable) {
        return medicalRecordRepository.findAllSummaryByPatientId(patientId, pageable);
    }

}
