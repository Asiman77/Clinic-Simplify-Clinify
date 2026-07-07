package az.clinify.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.clinify.demo.dto.request.UpdateLabResponseRequest;
import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.service.LabResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lab-responses")
@RequiredArgsConstructor
public class LabResponseController {
    private final LabResponseService labResponseService;

    @GetMapping("/{id}")
    public ResponseEntity<LabResponseResponseDTO> getLabResponseById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                labResponseService.getLabResponseById(id));
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<List<LabResponseResponseDTO>> getLabResponsesByMedicalRecordId(
            @PathVariable Long medicalRecordId) {
        return ResponseEntity.ok(
                labResponseService.getLabResponsesByMedicalRecordId(medicalRecordId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LabResponseResponseDTO>> getPendingLabResponses() {
        return ResponseEntity.ok(
                labResponseService.getPendingLabResponses());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabResponseResponseDTO> updateLabResponse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLabResponseRequest request) {
        return ResponseEntity.ok(
                labResponseService.updateLabResponse(id, request));
    }

}
