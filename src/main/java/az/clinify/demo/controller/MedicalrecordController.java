package az.clinify.demo.controller;

import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/records")
@RequiredArgsConstructor
public class MedicalrecordController {
private final MedicalRecordService service;
    @PostMapping
    public ResponseEntity<MedicalRecordResponseDTO>createMedicalRecord(
            @Valid @RequestBody MedicalRecordRequestDTO request){
        MedicalRecordResponseDTO response = service.CreateMedicalRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }







}
