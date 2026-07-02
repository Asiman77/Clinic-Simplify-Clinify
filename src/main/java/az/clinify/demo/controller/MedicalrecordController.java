package az.clinify.demo.controller;

import az.clinify.demo.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/records")
@RequiredArgsConstructor
public class MedicalrecordController {
private final MedicalRecordService service;







}
