package az.clinify.demo.mapper;

import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.LabResponse;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.repository.DoctorProfileRepository;
import az.clinify.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MedicalRecordMapper {

        private final UserRepository userRepository;
        private final DoctorProfileRepository doctorProfileRepository;
        private final LabResponseMapper labResponseMapper;

        public MedicalRecordResponseDTO toResponse(MedicalRecord medicalRecord) {

                if (medicalRecord == null) {
                        return null;
                }

                User patient = userRepository.findById(medicalRecord.getPatient().getId())
                                .orElseThrow(() -> new RuntimeException("Patient not found"));

                DoctorProfile doctor = doctorProfileRepository.findById(medicalRecord.getDoctor().getId())
                                .orElseThrow(() -> new RuntimeException("Doctor not found"));

                MedicalRecordResponseDTO response = new MedicalRecordResponseDTO();

                response.setId(medicalRecord.getId());

                response.setPatientId(patient.getId());
                response.setPatientFullName(
                                patient.getFirstName() + " " + patient.getLastName());

                response.setDoctorId(doctor.getId());
                response.setDoctorFullName(
                                doctor.getUser().getFirstName() + " " +
                                                doctor.getUser().getLastName());

                response.setDiagnosis(medicalRecord.getDiagnosis());
                response.setSymptoms(medicalRecord.getSymptoms());
                response.setReceipt(medicalRecord.getReceipt());
                response.setRecordDate(medicalRecord.getRecordDate());

                response.setLabResponses(
                                medicalRecord.getLabResponses()
                                                .stream()
                                                .map(labResponseMapper::toResponse)
                                                .collect(Collectors.toList()));

                response.setCreatedAt(medicalRecord.getCreatedAt());
                response.setUpdatedAt(medicalRecord.getUpdatedAt());

                return response;
        }

        public MedicalRecord toEntity(
                MedicalRecordRequestDTO request,
                User patient,
                DoctorProfile doctor
        ) {
                if (request == null) {
                        return null;
                }

                MedicalRecord medicalRecord = new MedicalRecord();

                medicalRecord.setPatient(patient);
                medicalRecord.setDoctor(doctor);
                medicalRecord.setDiagnosis(request.getDiagnosis());
                medicalRecord.setSymptoms(request.getSymptoms());
                medicalRecord.setReceipt(request.getReceipt());
                medicalRecord.setRecordDate(LocalDateTime.now());

                if (request.getLabTests() != null && !request.getLabTests().isEmpty()) {
                        request.getLabTests().forEach(labTestRequest -> {
                                LabResponse labResponse = new LabResponse();

                                labResponse.setMedicalRecord(medicalRecord);
                                labResponse.setTestName(labTestRequest.getTestName());
                                labResponse.setNote(labTestRequest.getNote());
                                labResponse.setStatus(LabStatuses.PENDING);

                                medicalRecord.getLabResponses().add(labResponse);
                        });
                }

                return medicalRecord;
        }
}