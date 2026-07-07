package az.clinify.demo.service;

import org.springframework.stereotype.Service;

import az.clinify.demo.entity.LabResponse;
import az.clinify.demo.mapper.LabResponseMapper;
import az.clinify.demo.repository.LabResponseRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabResponseService {

    private final LabResponseRepository labResponseRepository;
    private final LabResponseMapper labResponseMapper;

    private LabResponse getLabResponseEntityById(Long id) {
        return labResponseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Lab response not found with id: " + id));
    }
}
