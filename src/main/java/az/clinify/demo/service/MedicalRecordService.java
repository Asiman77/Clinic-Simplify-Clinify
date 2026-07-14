package az.clinify.demo.service;

import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.dto.response.MedicalRecordSummaryDto;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.entity.User;
import az.clinify.demo.exceptions.MedicalRecordNotFoundException;
import az.clinify.demo.exceptions.UserNotFoundException;
import az.clinify.demo.mapper.MedicalRecordMapper;
import az.clinify.demo.repository.MedicalRecordRepository;
import az.clinify.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
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

    @Transactional(readOnly = true)
    public Page<MedicalRecordSummaryDto> getCurrentPatientMedicalRecords(
            String authenticatedFin,
            Pageable pageable) {
        User patient = getCurrentPatient(authenticatedFin);

        return medicalRecordRepository.findAllSummaryByPatientId(
                patient.getId(),
                pageable);
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO getCurrentPatientMedicalRecord(
            Long recordId,
            String authenticatedFin) {
        User patient = getCurrentPatient(authenticatedFin);

        MedicalRecord medicalRecord = medicalRecordRepository
                .findByIdAndPatientId(recordId, patient.getId())
                .orElseThrow(() -> new MedicalRecordNotFoundException(
                        "Medical record could not be found"));

        return medicalRecordMapper.toResponse(medicalRecord);
    }

    private User getCurrentPatient(String authenticatedFin) {
        return userRepository.findByFin(authenticatedFin)
                .orElseThrow(() -> new UserNotFoundException(
                        "Patient could not be found"));
    }

}
