package az.clinify.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import az.clinify.demo.dto.request.CreateDoctorProfileRequest;
import az.clinify.demo.dto.request.UpdateDoctorProfileRequest;
import az.clinify.demo.dto.response.AvailableSlotResponse;
import az.clinify.demo.dto.response.DoctorProfileResponse;
import az.clinify.demo.enums.AppointmentType;
import az.clinify.demo.service.AvailableSlotService;
import az.clinify.demo.service.DoctorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorProfileController {
    private final DoctorProfileService doctorProfileService;
    private final AvailableSlotService availableSlotService;

    @GetMapping
    public ResponseEntity<Page<DoctorProfileResponse>> getAllDoctors(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(doctorProfileService.getAllDoctors(pageable));
    }

    @GetMapping("/{id}")
    public DoctorProfileResponse getDoctorById(@PathVariable Long id) {
        return doctorProfileService.getDoctorById(id);
    }

    @GetMapping("/{id}/available-slots")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlots(@PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam AppointmentType type) {
        return ResponseEntity.ok(availableSlotService.getAvailableSlots(id, date, type));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorProfileResponse createDoctor(
            @Valid @RequestBody CreateDoctorProfileRequest request) {
        return doctorProfileService.createDoctor(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public DoctorProfileResponse updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDoctorProfileRequest request) {
        return doctorProfileService.updateDoctor(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    public DoctorProfileResponse activateDoctor(@PathVariable Long id) {
        return doctorProfileService.activateDoctor(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public DoctorProfileResponse deactivateDoctor(@PathVariable Long id) {
        return doctorProfileService.deactivateDoctor(id);
    }
}
