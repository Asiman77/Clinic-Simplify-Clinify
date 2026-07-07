package az.clinify.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import az.clinify.demo.dto.request.LabResponseStatusRequest;
import az.clinify.demo.dto.request.UpdateLabResponseRequest;
import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.entity.LabResponse;
import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.exceptions.BaseBadRequestException;
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
    public LabResponseResponseDTO updateLabResponse(Long id, UpdateLabResponseRequest request) {
        LabResponse labResponse = getLabResponseEntityById(id);

        if (request.getResultText() != null) {
            labResponse.setResultText(request.getResultText());
        }
        if (request.getNote() != null) {
            labResponse.setNote(request.getNote());
        }
        if (request.getStatus() != null) {
            validateStatusUpdate(labResponse, request.getStatus());
            validateCompletedStatus(labResponse, request.getStatus());
            labResponse.setStatus(request.getStatus());
        }

        return labResponseMapper.toResponse(labResponseRepository.save(labResponse));
    }

    @Transactional
    public LabResponseResponseDTO updateLabResponseStatus(Long id, LabResponseStatusRequest request) {
        LabResponse labResponse = getLabResponseEntityById(id);

        validateStatusUpdate(labResponse, request.getStatus());
        validateCompletedStatus(labResponse, request.getStatus());

        labResponse.setStatus(request.getStatus());

        return labResponseMapper.toResponse(labResponseRepository.save(labResponse));
    }

    @Transactional
    public LabResponseResponseDTO uploadLabResponseFile(Long id, MultipartFile file) {
        LabResponse labResponse = getLabResponseEntityById(id);
        LabResponseFileMetadata fileMetadata = cloudinaryUploadService.uploadLabResponseFile(id, file);

        if (labResponse.getFiles() == null) {
            labResponse.setFiles(new ArrayList<>());
        }

        labResponse.getFiles().add(fileMetadata);

        return labResponseMapper.toResponse(labResponseRepository.save(labResponse));
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

    private void validateStatusUpdate(LabResponse labResponse, LabStatuses requestedStatus) {
        if (labResponse.getStatus() == LabStatuses.COMPLETED || labResponse.getStatus() == LabStatuses.CANCELLED) {
            throw new InvalidStatusException("Final lab response status cannot be changed");
        }

        if (requestedStatus == LabStatuses.NOT_REQUIRED || requestedStatus == LabStatuses.REQUESTED) {
            throw new InvalidStatusException("Invalid lab response status: " + requestedStatus);
        }
    }
}
