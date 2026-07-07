package az.clinify.demo.dto.request;

import az.clinify.demo.enums.LabStatuses;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLabResponseRequest {

    @Size(max = 5000, message = "Result text must not exceed 5000 characters")
    private String resultText;

    @Size(max = 2000, message = "Note must not exceed 2000 characters")
    private String note;

    private LabStatuses status;
}