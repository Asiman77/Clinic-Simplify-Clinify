package az.clinify.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;

import az.clinify.demo.entity.User;
import az.clinify.demo.exceptions.UserNotFoundException;
import az.clinify.demo.repository.UserRepository;
import az.clinify.demo.dto.request.LabResponseStatusRequest;
import az.clinify.demo.dto.request.UpdateLabResponseRequest;
import az.clinify.demo.dto.response.LabResponseDetailDTO;
import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.dto.response.LabResponseSummaryDTO;
import az.clinify.demo.entity.LabResponse;
import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.exceptions.BaseBadRequestException;
import az.clinify.demo.exceptions.BaseNotFoundException;
import az.clinify.demo.exceptions.InvalidStatusException;
import az.clinify.demo.exceptions.LabResponseNotFoundException;
import az.clinify.demo.mapper.LabResponseMapper;
import az.clinify.demo.repository.LabResponseRepository;
import az.clinify.demo.valueobject.LabResponseFileMetadata;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabResponseService {
    private final LabResponseRepository labResponseRepository;
    private final LabResponseMapper labResponseMapper;
    private final CloudinaryUploadService cloudinaryUploadService;
    private final UserRepository userRepository;

    private static final List<LabStatuses> OPEN_STATUSES = List.of(
            LabStatuses.PENDING,
            LabStatuses.IN_PROGRESS);

    @Transactional(readOnly = true)
    public Page<LabResponseSummaryDTO> getAllLabResponses(
            Pageable pageable) {

        return labResponseRepository
                .findAll(pageable)
                .map(labResponseMapper::toSummary);
    }

    @Transactional(readOnly = true)
    public Page<LabResponseSummaryDTO> getOpenLabResponses(
            Pageable pageable) {

        return labResponseRepository
                .findAllByStatusIn(OPEN_STATUSES, pageable)
                .map(labResponseMapper::toSummary);
    }

    @Transactional(readOnly = true)
    public LabResponseDetailDTO getLabResponseDetail(Long id) {
        LabResponse labResponse = getLabResponseEntityById(id);
        return labResponseMapper.toDetail(labResponse);
    }

    @Transactional(readOnly = true)
    public LabResponseResponseDTO getLabResponseById(Long id) {
        return labResponseMapper.toResponse(getLabResponseEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<LabResponseResponseDTO> getLabResponsesByMedicalRecordId(Long medicalRecordId) {
        return labResponseRepository.findAllByMedicalRecordId(medicalRecordId)
                .stream()
                .map(labResponseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LabResponseResponseDTO> getPendingLabResponses() {
        return labResponseRepository.findAllByStatus(LabStatuses.PENDING)
                .stream()
                .map(labResponseMapper::toResponse)
                .toList();
    }

    @Transactional
    public LabResponseResponseDTO updateLabResponse(Long id, UpdateLabResponseRequest request,
            String authenticatedFin) {
        LabResponse labResponse = getLabResponseEntityById(id);
        validateEditable(labResponse);
        User technician = getCurrentLabTechnician(authenticatedFin);
        assignOrValidateTechnician(labResponse, technician);
        if (request.getResultText() != null) {
            labResponse.setResultText(request.getResultText());
        }
        if (request.getNote() != null) {
            labResponse.setNote(request.getNote());
        }
        if (request.getStatus() != null) {
            validateStatusTransition(labResponse, request.getStatus());
            validateCompletedStatus(labResponse, request.getStatus());
            labResponse.setStatus(request.getStatus());
        }
        LabResponse savedResponse = labResponseRepository.save(labResponse);
        return labResponseMapper.toResponse(savedResponse);
    }

    @Transactional
    public LabResponseResponseDTO updateLabResponseStatus(Long id, LabResponseStatusRequest request,
            String authenticatedFin) {
        LabResponse labResponse = getLabResponseEntityById(id);
        validateEditable(labResponse);
        User technician = getCurrentLabTechnician(authenticatedFin);
        assignOrValidateTechnician(labResponse, technician);
        validateStatusTransition(labResponse, request.getStatus());
        validateCompletedStatus(labResponse, request.getStatus());
        labResponse.setStatus(request.getStatus());
        LabResponse savedResponse = labResponseRepository.save(labResponse);
        return labResponseMapper.toResponse(savedResponse);
    }

    @Transactional
    public LabResponseResponseDTO uploadLabResponseFile(
            Long id,
            MultipartFile file,
            String authenticatedFin) {
        LabResponse labResponse = getLabResponseEntityById(id);
        validateEditable(labResponse);
        User technician = getCurrentLabTechnician(authenticatedFin);

        assignOrValidateTechnician(labResponse, technician);

        LabResponseFileMetadata fileMetadata = cloudinaryUploadService
                .uploadLabResponseFile(id, file);
        if (labResponse.getFiles() == null) {
            labResponse.setFiles(new ArrayList<>());
        }
        labResponse.getFiles().add(fileMetadata);
        LabResponse savedResponse = labResponseRepository.save(labResponse);
        return labResponseMapper.toResponse(savedResponse);
    }

    @Transactional
    public LabResponseResponseDTO deleteLabResponseFile(
            Long id,
            String publicId,
            String authenticatedFin) {
        LabResponse labResponse = getLabResponseEntityById(id);
        validateEditable(labResponse);
        User technician = getCurrentLabTechnician(authenticatedFin);
        assignOrValidateTechnician(labResponse, technician);
        if (labResponse.getFiles() == null) {
            throw new BaseNotFoundException(
                    "Lab response file not found");
        }

        LabResponseFileMetadata fileMetadata = labResponse.getFiles()
                .stream()
                .filter(file -> publicId.equals(file.getPublicId()))
                .findFirst()
                .orElseThrow(() -> new BaseNotFoundException(
                        "Lab response file not found"));

        cloudinaryUploadService.deleteLabResponseFile(fileMetadata);
        labResponse.getFiles().remove(fileMetadata);
        LabResponse savedResponse = labResponseRepository.save(labResponse);
        return labResponseMapper.toResponse(savedResponse);
    }

    private LabResponse getLabResponseEntityById(Long id) {
        return labResponseRepository.findById(id)
                .orElseThrow(() -> new LabResponseNotFoundException("Lab response not found with id: " + id));
    }

    private void validateCompletedStatus(LabResponse labResponse, LabStatuses requestedStatus) {
        if (requestedStatus != LabStatuses.COMPLETED) {
            return;
        }

        boolean hasResultText = labResponse.getResultText() != null && !labResponse.getResultText().isBlank();
        boolean hasFiles = labResponse.getFiles() != null && !labResponse.getFiles().isEmpty();

        if (!hasResultText && !hasFiles) {
            throw new BaseBadRequestException(
                    "Lab response must contain result text or at least one file before completion");
        }
    }

    private void validateEditable(LabResponse labResponse) {
        if (isFinalStatus(labResponse.getStatus())) {
            throw new InvalidStatusException(
                    "Completed or cancelled lab responses cannot be modified");
        }
    }

    private void validateStatusTransition(
            LabResponse labResponse,
            LabStatuses requestedStatus) {

        LabStatuses currentStatus = labResponse.getStatus();

        if (currentStatus == requestedStatus) {
            return;
        }

        boolean validTransition = switch (currentStatus) {
            case PENDING ->
                requestedStatus == LabStatuses.IN_PROGRESS
                        || requestedStatus == LabStatuses.CANCELLED;

            case IN_PROGRESS ->
                requestedStatus == LabStatuses.COMPLETED
                        || requestedStatus == LabStatuses.CANCELLED;

            default -> false;
        };

        if (!validTransition) {
            throw new InvalidStatusException(
                    "Invalid lab response status transition: "
                            + currentStatus
                            + " -> "
                            + requestedStatus);
        }
    }

    private boolean isFinalStatus(LabStatuses status) {
        return status == LabStatuses.COMPLETED
                || status == LabStatuses.CANCELLED;
    }

    private User getCurrentLabTechnician(String authenticatedFin) {
        return userRepository.findByFin(authenticatedFin)

                .orElseThrow(() -> new UserNotFoundException("Lab technician could not be found"));
    }

    private void assignOrValidateTechnician(LabResponse labResponse, User technician) {
        User assignedTechnician = labResponse.getLabTechnician();
        if (assignedTechnician == null) {
            labResponse.setLabTechnician(technician);
            return;
        }

        if (!assignedTechnician.getId().equals(technician.getId())) {
            throw new AccessDeniedException(
                    "This lab response is assigned to another lab technician");
        }
    }
}
