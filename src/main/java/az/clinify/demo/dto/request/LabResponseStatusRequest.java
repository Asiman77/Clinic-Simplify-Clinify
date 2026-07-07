package az.clinify.demo.dto.request;

import az.clinify.demo.enums.LabStatuses;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabResponseStatusRequest {
    @NotNull(message = "Status is required")
    private LabStatuses status;
}
