package az.clinify.demo.service;

import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import az.clinify.demo.dto.request.MedicalRecordStatusRequest;
import az.clinify.demo.dto.request.UpdateMedicalRecordRequest;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.dto.response.MedicalRecordSummaryDto;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.entity.User;
import az.clinify.demo.exceptions.DoctorNotFoundException;
import az.clinify.demo.exceptions.MedicalRecordNotFoundException;
import az.clinify.demo.exceptions.UserNotFoundException;
import az.clinify.demo.mapper.MedicalRecordMapper;
import az.clinify.demo.repository.DoctorProfileRepository;
import az.clinify.demo.repository.MedicalRecordRepository;
import az.clinify.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

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


    public MedicalRecordResponseDTO CreateMedicalRecord(MedicalRecordRequestDTO request){
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new UserNotFoundException("Patient not found"));
        DoctorProfile doctor = doctorProfileRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient);
        medicalRecord.setDoctor(doctor);
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setReceipt(request.getReceipt());
        medicalRecord.setRecordDate(LocalDateTime.now());
        medicalRecord.setLabStatus(request.getLabStatus());
        medicalRecord.setStatusUpdatedAt(LocalDateTime.now());
        medicalRecord.setTestName(request.getTestName());

        return medicalRecordMapper.toResponse(medicalRecordRepository.save(medicalRecord));
    }
    public MedicalRecordResponseDTO setStatus(Long id,MedicalRecordStatusRequest request){
            MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                    .orElseThrow(() -> new MedicalRecordNotFoundException("not found"));

            medicalRecord.setLabStatus(request.getLabStatus());
            medicalRecord.setStatusUpdatedAt(LocalDateTime.now());
            if (request.getTestName()!= null) {
                medicalRecord.setTestName(request.getTestName());
            }
            return medicalRecordMapper.toResponse( medicalRecordRepository.save(medicalRecord));
        }
    public MedicalRecordResponseDTO returnMedicalRecord(Long id){
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException("not found"));
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

        if (dto.getTestName() != null) {
            record.setTestName(dto.getTestName());
        }

        if (dto.getLabStatus() != null && record.getLabStatus() != dto.getLabStatus()) {
            record.setLabStatus(dto.getLabStatus());
            record.setStatusUpdatedAt(LocalDateTime.now());
        }
        return medicalRecordRepository.save(record);
    }


    @Transactional(readOnly = true)
    public List<MedicalRecordSummaryDto> getPatientMedicalRecords(Long patientId) {

        return medicalRecordRepository.findAllSummaryByPatientId(patientId);
    }


}
