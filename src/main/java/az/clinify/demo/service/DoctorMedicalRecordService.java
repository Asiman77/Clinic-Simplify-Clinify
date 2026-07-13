package az.clinify.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import az.clinify.demo.dto.response.DoctorPatientResponse;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.exceptions.DoctorNotFoundException;
import az.clinify.demo.exceptions.MedicalRecordNotFoundException;
import az.clinify.demo.exceptions.UserNotFoundException;
import az.clinify.demo.mapper.MedicalRecordMapper;
import az.clinify.demo.repository.AppointmentRepository;
import az.clinify.demo.repository.DoctorProfileRepository;
import az.clinify.demo.repository.MedicalRecordRepository;
import az.clinify.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorMedicalRecordService {

    private static final List<AppointmentStatus> ELIGIBLE_APPOINTMENT_STATUSES = List.of(AppointmentStatus.APPROVED,
            AppointmentStatus.COMPLETED);
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Transactional
    public MedicalRecordResponseDTO create(MedicalRecordRequestDTO request, String authenticatedFin) {
        DoctorProfile doctor = getCurrentDoctor(authenticatedFin);

        User patient = userRepository
                .findById(request.getPatientId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Patient could not be found"));

        boolean isEligiblePatient = appointmentRepository
                .existsByDoctorIdAndPatientIdAndStatusIn(
                        doctor.getId(),
                        patient.getId(),
                        ELIGIBLE_APPOINTMENT_STATUSES);

        if (!isEligiblePatient) {
            throw new AccessDeniedException(
                    "Medical records can only be created for eligible patients");
        }

        MedicalRecord record = medicalRecordMapper.toEntity(request, patient, doctor);
        MedicalRecord savedRecord = medicalRecordRepository.save(record);
        return medicalRecordMapper.toResponse(savedRecord);
    }

    private DoctorProfile getCurrentDoctor(String authenticatedFin) {
        return doctorProfileRepository
                .findByUserFin(authenticatedFin)
                .orElseThrow(() -> new DoctorNotFoundException(
                        "Doctor profile could not be found"));
    }

    @Transactional(readOnly = true)
    public Page<MedicalRecordResponseDTO> getCurrentDoctorRecords(
            String authenticatedFin,
            Pageable pageable) {

        DoctorProfile doctor = getCurrentDoctor(authenticatedFin);

        return medicalRecordRepository
                .findByDoctorId(doctor.getId(), pageable)
                .map(medicalRecordMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO getCurrentDoctorRecord(
            Long recordId,
            String authenticatedFin) {

        DoctorProfile doctor = getCurrentDoctor(authenticatedFin);

        MedicalRecord record = medicalRecordRepository
                .findByIdAndDoctorId(
                        recordId,
                        doctor.getId())
                .orElseThrow(() -> new MedicalRecordNotFoundException(
                        "Medical record could not be found"));

        return medicalRecordMapper.toResponse(record);
    }

    @Transactional(readOnly = true)
    public List<DoctorPatientResponse> getCurrentDoctorPatients(
            String authenticatedFin) {

        DoctorProfile doctor = getCurrentDoctor(authenticatedFin);

        return appointmentRepository
                .findDistinctPatientsByDoctorIdAndStatusIn(
                        doctor.getId(),
                        ELIGIBLE_APPOINTMENT_STATUSES)
                .stream()
                .map(patient -> new DoctorPatientResponse(
                        patient.getId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getEmail()))
                .toList();
    }
}