package az.clinify.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.clinify.demo.dto.response.DoctorProfileResponse;
import az.clinify.demo.service.DoctorProfileService;
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
}
