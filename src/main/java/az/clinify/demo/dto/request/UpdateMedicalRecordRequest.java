package az.clinify.demo.dto.request;

import az.clinify.demo.enums.LabStatuses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateMedicalRecordRequest {

    private String diagnosis;

    private String symptoms;

    private String receipt;

}
