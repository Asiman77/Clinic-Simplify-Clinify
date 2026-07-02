package az.clinify.demo.controller;

import az.clinify.demo.dto.request.MedicalRecordRequestDTO;
import az.clinify.demo.dto.request.MedicalRecordStatusRequest;
import az.clinify.demo.dto.response.MedicalRecordResponseDTO;
import az.clinify.demo.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PatchMapping("/{id}/status")
    public ResponseEntity<MedicalRecordResponseDTO>updateStatus(
            @PathVariable Long id,@Valid @RequestBody MedicalRecordStatusRequest request){
        MedicalRecordResponseDTO response = service.setStatus(id,request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO>getMedicalRecord(@PathVariable Long id){
        MedicalRecordResponseDTO response = service.returnMedicalRecord(id);
        return ResponseEntity.ok(response);
    }







}
