package az.clinify.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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
import az.clinify.demo.dto.response.DoctorProfileResponse;
import az.clinify.demo.service.DoctorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorProfileController {
    private final DoctorProfileService doctorProfileService;

    @GetMapping
    public List<DoctorProfileResponse> getAllDoctors() {
        return doctorProfileService.getAllDoctors();
    }

    @GetMapping("/{id}")
    public DoctorProfileResponse getDoctorById(@PathVariable Long id) {
        return doctorProfileService.getDoctorById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorProfileResponse createDoctor(
            @Valid @RequestBody CreateDoctorProfileRequest request) {
        return doctorProfileService.createDoctor(request);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public DoctorProfileResponse updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDoctorProfileRequest request) {
        return doctorProfileService.updateDoctor(id, request);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/activate")
    public DoctorProfileResponse activateDoctor(@PathVariable Long id) {
        return doctorProfileService.activateDoctor(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public DoctorProfileResponse deactivateDoctor(@PathVariable Long id) {
        return doctorProfileService.deactivateDoctor(id);
    }
}
