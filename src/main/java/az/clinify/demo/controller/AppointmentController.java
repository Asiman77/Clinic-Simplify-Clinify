package az.clinify.demo.controller;

import az.clinify.demo.dto.request.AppointmentRequestDTO;
import az.clinify.demo.dto.request.AppointmentStatusRequest;
import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.service.AppointmentBookingService;
import az.clinify.demo.service.AppointmentManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @Valid @RequestBody AppointmentRequestDTO request) {
        AppointmentResponseDTO response = appointmentBookingService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Returns all appointments belonging to the given patient.
     *
     * @param patientId id of the patient
     * @return list of appointments for the patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentManagementService.getByPatient(patientId));
    }

    /**
     * Returns all appointments belonging to the given doctor.
     *
     * @param doctorId id of the doctor
     * @return list of appointments for the doctor
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentManagementService.getByDoctor(doctorId));
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
