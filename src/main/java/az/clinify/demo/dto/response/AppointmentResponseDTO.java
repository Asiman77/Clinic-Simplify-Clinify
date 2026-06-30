package az.clinify.demo.dto.response;

import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.enums.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    private Long id;

    private Long patientId;
    private String patientFullName;

    private Long doctorId;
    private String doctorFullName;

    private Long createdById;
    private String createdByFullName;

    private AppointmentType type;
    private AppointmentStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String reason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
