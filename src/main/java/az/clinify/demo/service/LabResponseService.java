package az.clinify.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.entity.LabResponse;
import az.clinify.demo.enums.LabStatuses;
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
}
