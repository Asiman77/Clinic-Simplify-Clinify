package az.clinify.demo.controller;

import az.clinify.demo.dto.request.CreateDoctorAvailabilityRequest;
import az.clinify.demo.dto.response.DoctorAvailabilityResponse;
import az.clinify.demo.service.DoctorAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
public class DoctorAvailabilityController {
    private final DoctorAvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<DoctorAvailabilityResponse>createAvailability(
            @Valid @RequestBody CreateDoctorAvailabilityRequest request){
        DoctorAvailabilityResponse response = availabilityService.createAvailability(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<DoctorAvailabilityResponse>getAvailabilityById(@PathVariable Long id){
        return ResponseEntity.ok(availabilityService.getDoctorAvailabilityById(id));
    }
    @GetMapping
    public ResponseEntity<List<DoctorAvailabilityResponse>>getAllAvailabilities(){
        return ResponseEntity.ok(availabilityService.getAllDoctorAvailabilities());
    }
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorAvailabilityResponse>>getAvailabilitiesByDoctorId(
            @PathVariable Long doctorId){
        return ResponseEntity.ok(availabilityService.getDoctorAvailabilitiesByDoctorId(doctorId));
    }



}
