package az.clinify.demo.dto.request;

import az.clinify.demo.enums.LabStatuses;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordStatusRequest {
    @NotNull(message = "Lab status is required")
    private LabStatuses labStatus;

    @Size(max = 1000, message = "Test name must not exceed 1000 characters")
    private String testName;
}
