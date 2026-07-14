package az.clinify.demo.mapper;

import az.clinify.demo.dto.response.LabResponseDetailDTO;
import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.dto.response.LabResponseSummaryDTO;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.LabResponse;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class LabResponseMapper {
    public LabResponseResponseDTO toResponse(LabResponse labResponse) {
        if (labResponse == null) {
            return null;
        }

        LabResponseResponseDTO response = new LabResponseResponseDTO();

        response.setId(labResponse.getId());
        response.setMedicalRecordId(
                labResponse.getMedicalRecord().getId());

        if (labResponse.getLabTechnician() != null) {
            response.setLabTechnicianId(
                    labResponse.getLabTechnician().getId());
            response.setLabTechnicianFullName(
                    fullName(labResponse.getLabTechnician()));
        }

        response.setTestName(labResponse.getTestName());
        response.setStatus(labResponse.getStatus());
        response.setResultText(labResponse.getResultText());
        response.setNote(labResponse.getNote());
        response.setFiles(labResponse.getFiles());
        response.setCreatedAt(labResponse.getCreatedAt());
        response.setUpdatedAt(labResponse.getUpdatedAt());

        return response;
    }

    public LabResponseSummaryDTO toSummary(LabResponse labResponse) {
        if (labResponse == null) {
            return null;
        }

        MedicalRecord medicalRecord = labResponse.getMedicalRecord();
        User patient = medicalRecord.getPatient();
        DoctorProfile doctor = medicalRecord.getDoctor();

        LabResponseSummaryDTO response = new LabResponseSummaryDTO();

        response.setId(labResponse.getId());
        response.setMedicalRecordId(medicalRecord.getId());

        response.setPatientId(patient.getId());
        response.setPatientFullName(fullName(patient));

        response.setDoctorId(doctor.getId());
        response.setDoctorFullName(
                fullName(doctor.getUser()));

        response.setTestName(labResponse.getTestName());
        response.setStatus(labResponse.getStatus());
        response.setCreatedAt(labResponse.getCreatedAt());
        response.setUpdatedAt(labResponse.getUpdatedAt());

        return response;
    }

    public LabResponseDetailDTO toDetail(LabResponse labResponse) {
        if (labResponse == null) {
            return null;
        }

        MedicalRecord medicalRecord = labResponse.getMedicalRecord();
        User patient = medicalRecord.getPatient();
        DoctorProfile doctor = medicalRecord.getDoctor();

        LabResponseDetailDTO response = new LabResponseDetailDTO();

        response.setId(labResponse.getId());
        response.setMedicalRecordId(medicalRecord.getId());

        response.setPatientId(patient.getId());
        response.setPatientFullName(fullName(patient));

        response.setDoctorId(doctor.getId());
        response.setDoctorFullName(
                fullName(doctor.getUser()));

        if (labResponse.getLabTechnician() != null) {
            response.setLabTechnicianId(
                    labResponse.getLabTechnician().getId());
            response.setLabTechnicianFullName(
                    fullName(labResponse.getLabTechnician()));
        }

        response.setDiagnosis(medicalRecord.getDiagnosis());
        response.setSymptoms(medicalRecord.getSymptoms());

        response.setTestName(labResponse.getTestName());
        response.setStatus(labResponse.getStatus());
        response.setResultText(labResponse.getResultText());
        response.setNote(labResponse.getNote());
        response.setFiles(labResponse.getFiles());
        response.setCreatedAt(labResponse.getCreatedAt());
        response.setUpdatedAt(labResponse.getUpdatedAt());

        return response;
    }

    private String fullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}