package az.clinify.demo.service;

import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import az.clinify.demo.dto.request.UpdateMedicalRecordRequest;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.dto.response.MedicalRecordSummaryDto;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.entity.User;
import az.clinify.demo.exceptions.MedicalRecordNotFoundException;
import az.clinify.demo.exceptions.UserNotFoundException;
import az.clinify.demo.mapper.MedicalRecordMapper;
import az.clinify.demo.repository.DoctorProfileRepository;
import az.clinify.demo.repository.MedicalRecordRepository;
import az.clinify.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Transactional
    public MedicalRecordResponseDTO CreateMedicalRecord(MedicalRecordRequestDTO request) {
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new UserNotFoundException("Patient not found"));
        DoctorProfile doctor = doctorProfileRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        MedicalRecord medicalRecord =
                medicalRecordMapper.toEntity(request, patient, doctor);

        MedicalRecord savedMedicalRecord =
                medicalRecordRepository.save(medicalRecord);

        return medicalRecordMapper.toResponse(savedMedicalRecord);
    }

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
