package az.clinify.demo.controller;

import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import az.clinify.demo.dto.request.MedicalRecordStatusRequest;
import az.clinify.demo.dto.request.UpdateMedicalRecordRequest;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.dto.response.MedicalRecordSummaryDto;
import az.clinify.demo.entity.MedicalRecord;
import az.clinify.demo.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/records")
@RequiredArgsConstructor
public class MedicalrecordController {
    private final MedicalRecordService service;

    @PostMapping
    public ResponseEntity<MedicalRecordResponseDTO> createMedicalRecord(
            @Valid @RequestBody MedicalRecordRequestDTO request) {
        MedicalRecordResponseDTO response = service.CreateMedicalRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MedicalRecordResponseDTO> updateStatus(
            @PathVariable Long id, @Valid @RequestBody MedicalRecordStatusRequest request) {
        MedicalRecordResponseDTO response = service.setStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecord(@PathVariable Long id) {
        MedicalRecordResponseDTO response = service.returnMedicalRecord(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('DOCTOR')") 
    public ResponseEntity<List<MedicalRecordSummaryDto>> getPatientRecords(@PathVariable Long patientId) {
        List<MedicalRecordSummaryDto> records = service.getPatientMedicalRecords(patientId);
        return ResponseEntity.ok(records);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')") 
    public ResponseEntity<MedicalRecord> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMedicalRecordRequest dto,
            Principal principal) {
        

        String currentDoctorUsername = principal.getName();
        
        MedicalRecord updatedRecord = service.updateMedicalRecord(id, dto, currentDoctorUsername);
        return ResponseEntity.ok(updatedRecord);
    }

}
