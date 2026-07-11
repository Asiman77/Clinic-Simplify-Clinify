package az.clinify.demo.controller;

import az.clinify.demo.dto.request.AppointmentStatusRequest;
import az.clinify.demo.dto.request.PatientAppointmentRequestDTO;
import az.clinify.demo.dto.request.WalkInAppointmentRequestDTO;
import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.service.AppointmentBookingService;
import az.clinify.demo.service.AppointmentManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentManagementService appointmentManagementService;
    private final AppointmentBookingService appointmentBookingService;

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentManagementService.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentManagementService.getAppointmentById(id));
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createPatientAppointment(
            @Valid @RequestBody PatientAppointmentRequestDTO request,
            Authentication authentication) {
        AppointmentResponseDTO response = appointmentBookingService
                .createPatientAppointment(
                        request,
                        authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("hasRole('RECEPTION')")
    @PostMapping("/walk-in")
    public ResponseEntity<AppointmentResponseDTO> createWalkInAppointment(
            @Valid @RequestBody WalkInAppointmentRequestDTO request,
            Authentication authentication) {
        AppointmentResponseDTO response = appointmentBookingService.createWalkInAppointment(request,
                authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Returns all appointments belonging to the given patient.
     *
     * @param patientId id of the patient
     * @return list of appointments for the patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<AppointmentResponseDTO>> getByPatient(
            @PathVariable Long patientId,
            @PageableDefault(page = 0, size = 10, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(appointmentManagementService.getByPatient(patientId, pageable));
    }

    /**
     * Returns all appointments belonging to the given doctor.
     *
     * @param doctorId id of the doctor
     * @return list of appointments for the doctor
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Page<AppointmentResponseDTO>> getByDoctor(
            @PathVariable Long doctorId,
            @PageableDefault(page = 0, size = 10, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(
                appointmentManagementService.getByDoctor(doctorId, pageable));
    }

    /**
     * Updates the status of an appointment (e.g. APPROVED, REJECTED, CANCELLED).
     *
     * @param id      id of the appointment to update
     * @param request new status to apply
     * @return updated appointment
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody AppointmentStatusRequest request) {
        return ResponseEntity.ok(appointmentManagementService.updateStatus(id, request));
    }
}
