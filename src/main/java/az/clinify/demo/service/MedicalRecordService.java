package az.clinify.demo.service;

import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import az.clinify.demo.dto.request.MedicalRecordStatusRequest;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

}
