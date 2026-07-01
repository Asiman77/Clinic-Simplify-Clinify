package az.clinify.demo.controller;

import az.clinify.demo.service.DoctorAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
public class DoctorAvailabilityController {
    private final DoctorAvailabilityService availabilityService;


}
