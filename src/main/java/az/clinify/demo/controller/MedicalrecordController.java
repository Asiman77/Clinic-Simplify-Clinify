package az.clinify.demo.controller;

import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import az.clinify.demo.dto.request.UpdateMedicalRecordRequest;
import az.clinify.demo.dto.response.DoctorPatientResponse;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.dto.response.MedicalRecordSummaryDto;
import az.clinify.demo.service.DoctorMedicalRecordService;
import az.clinify.demo.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/records")
@RequiredArgsConstructor
public class MedicalrecordController {
    private final MedicalRecordService service;
    private final DoctorMedicalRecordService doctorMedicalRecordService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordResponseDTO> createMedicalRecord(
            @Valid @RequestBody MedicalRecordRequestDTO request, Principal principal) {
        MedicalRecordResponseDTO response = doctorMedicalRecordService.create(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecord(@PathVariable Long id) {
        MedicalRecordResponseDTO response = service.returnMedicalRecord(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<MedicalRecordSummaryDto>> getPatientRecords(
            @PathVariable Long patientId,
            @PageableDefault(page = 0, size = 10, sort = "recordDate", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(service.getPatientMedicalRecords(patientId, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordResponseDTO> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMedicalRecordRequest request,
            Principal principal) {

        MedicalRecordResponseDTO response = doctorMedicalRecordService.update(id, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor/mine")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Page<MedicalRecordResponseDTO>> getCurrentDoctorRecords(Principal principal,
            @PageableDefault(page = 0, size = 10, sort = "recordDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(doctorMedicalRecordService.getCurrentDoctorRecords(principal.getName(), pageable));
    }

    @GetMapping("/doctor/mine/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordResponseDTO> getCurrentDoctorRecord(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(doctorMedicalRecordService.getCurrentDoctorRecord(id, principal.getName()));
    }

    @GetMapping("/doctor/patients")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<DoctorPatientResponse>> getCurrentDoctorPatients(Principal principal) {
        return ResponseEntity.ok(doctorMedicalRecordService.getCurrentDoctorPatients(principal.getName()));
    }

}
