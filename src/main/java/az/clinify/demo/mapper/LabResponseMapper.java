package az.clinify.demo.mapper;

import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.entity.LabResponse;
import org.springframework.stereotype.Component;

@Component
public class LabResponseMapper {

    public LabResponseResponseDTO toResponse(LabResponse labResponse) {

        if (labResponse == null) {
            return null;
        }

        LabResponseResponseDTO response = new LabResponseResponseDTO();

        response.setId(labResponse.getId());

        response.setMedicalRecordId(labResponse.getMedicalRecord().getId());

        response.setLabTechnicianId(labResponse.getLabTechnician().getId());
        response.setLabTechnicianFullName(
                labResponse.getLabTechnician().getFirstName() + " " +
                        labResponse.getLabTechnician().getLastName()
        );

        response.setResultText(labResponse.getResultText());
        response.setNote(labResponse.getNote());

        response.setCreatedAt(labResponse.getCreatedAt());
        response.setUpdatedAt(labResponse.getUpdatedAt());

        return response;
    }
}