package az.clinify.demo.dto.request;

import az.clinify.demo.enums.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {
    @NotNull(message = "Doctor id is required")
    private Long doctorId;

    private Long patientId;

    @NotNull(message = "Appointment type is required")
    private AppointmentType type;

    @NotNull(message = "Start time is required")
    @Future(message = "Appointment start time must be in the future")
    private LocalDateTime startTime;

    @Size(max = 1000, message = "Reason cannot be longer than 1000 characters")
    private String reason;
}
