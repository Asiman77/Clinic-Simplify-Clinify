package az.clinify.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.entity.LabResponse;
import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.exceptions.BaseBadRequestException;
import az.clinify.demo.exceptions.InvalidStatusException;
import az.clinify.demo.mapper.LabResponseMapper;
import az.clinify.demo.repository.LabResponseRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabResponseService {

    private final LabResponseRepository labResponseRepository;
    private final LabResponseMapper labResponseMapper;

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

    private LabResponse getLabResponseEntityById(Long id) {
        return labResponseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Lab response not found with id: " + id));
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
