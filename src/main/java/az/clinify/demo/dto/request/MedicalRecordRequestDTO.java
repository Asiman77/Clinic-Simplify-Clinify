package az.clinify.demo.dto.request;

import az.clinify.demo.enums.LabStatuses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordRequestDTO {
    @NotNull(message = "Patient id is required")
    @Positive(message = "Patient id must be a positive number")
    private Long patientId;

    @NotBlank(message = "Diagnosis is required")
    @Size(max = 255, message = "Diagnosis must not exceed 255 characters")
    private String diagnosis;

    @Size(max = 2000, message = "Symptoms must not exceed 2000 characters")
    private String symptoms;

    @Size(max = 2000, message = "Receipt must not exceed 2000 characters")
    private String receipt;

    private LabStatuses labStatus;

    @Size(max = 1000, message = "Test name must not exceed 1000 characters")
    private String testName;
}
