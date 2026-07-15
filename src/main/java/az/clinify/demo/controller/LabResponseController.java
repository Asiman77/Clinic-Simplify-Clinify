package az.clinify.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;

import az.clinify.demo.dto.request.DeleteLabResponseFileRequest;
import az.clinify.demo.dto.response.LabResponseSummaryDTO;
import az.clinify.demo.dto.response.PatientLabResultSummaryDTO;
import az.clinify.demo.dto.request.LabResponseStatusRequest;
import az.clinify.demo.dto.request.UpdateLabResponseRequest;
import az.clinify.demo.dto.response.LabResponseDetailDTO;
import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.service.LabResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lab-responses")
@RequiredArgsConstructor
public class LabResponseController {
        private final LabResponseService labResponseService;

        @GetMapping("/patient/mine")
        @PreAuthorize("hasRole('PATIENT')")
        public ResponseEntity<Page<PatientLabResultSummaryDTO>> getCurrentPatientLabResults(Principal principal,
                        @PageableDefault(page = 0, size = 10, sort = "medicalRecord.recordDate", direction = Sort.Direction.DESC) Pageable pageable) {
                return ResponseEntity.ok(labResponseService.getCurrentPatientLabResults(principal.getName(), pageable));
        }

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
        public ResponseEntity<Page<LabResponseSummaryDTO>> getAllLabResponses(
                        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
                return ResponseEntity.ok(labResponseService.getAllLabResponses(pageable));
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
        public ResponseEntity<LabResponseDetailDTO> getLabResponseById(@PathVariable Long id) {
                return ResponseEntity.ok(labResponseService.getLabResponseDetail(id));
        }

        @GetMapping("/medical-record/{medicalRecordId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
        public ResponseEntity<List<LabResponseResponseDTO>> getLabResponsesByMedicalRecordId(
                        @PathVariable Long medicalRecordId) {
                return ResponseEntity.ok(
                                labResponseService.getLabResponsesByMedicalRecordId(medicalRecordId));
        }

        @GetMapping("/pending")
        @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
        public ResponseEntity<Page<LabResponseSummaryDTO>> getOpenLabResponses(
                        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
                return ResponseEntity.ok(labResponseService.getOpenLabResponses(pageable));
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('LAB_TECHNICIAN')")
        public ResponseEntity<LabResponseResponseDTO> updateLabResponse(
                        @PathVariable Long id,
                        @Valid @RequestBody UpdateLabResponseRequest request, Principal principal) {
                return ResponseEntity.ok(labResponseService.updateLabResponse(id, request, principal.getName()));
        }

        @PatchMapping("/{id}/status")
        @PreAuthorize("hasRole('LAB_TECHNICIAN')")
        public ResponseEntity<LabResponseResponseDTO> updateLabResponseStatus(
                        @PathVariable Long id,
                        @Valid @RequestBody LabResponseStatusRequest request,
                        Principal principal) {

                return ResponseEntity.ok(labResponseService.updateLabResponseStatus(id, request, principal.getName()));
        }

        @PostMapping("/{id}/files")
        @PreAuthorize("hasRole('LAB_TECHNICIAN')")
        public ResponseEntity<LabResponseResponseDTO> uploadLabResponseFile(
                        @PathVariable Long id,
                        @RequestParam("file") MultipartFile file,
                        Principal principal) {
                return ResponseEntity.ok(labResponseService.uploadLabResponseFile(id, file, principal.getName()));
        }

        @DeleteMapping("/{id}/files")
        @PreAuthorize("hasRole('LAB_TECHNICIAN')")
        public ResponseEntity<LabResponseResponseDTO> deleteLabResponseFile(
                        @PathVariable Long id,
                        @Valid @RequestBody DeleteLabResponseFileRequest request,
                        Principal principal) {
                return ResponseEntity.ok(labResponseService.deleteLabResponseFile(id, request.getPublicId(),
                                principal.getName()));
        }
}
