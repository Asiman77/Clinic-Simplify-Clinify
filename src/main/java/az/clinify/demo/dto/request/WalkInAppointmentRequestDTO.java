package az.clinify.demo.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
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
public class WalkInAppointmentRequestDTO {

    @NotNull(message = "Patient id is required")
    private Long patientId;

    @NotNull(message = "Doctor id is required")
    private Long doctorId;

    @NotNull(message = "Start time is required")
    @Future(message = "Appointment start time must be in the future")
    private LocalDateTime startTime;

    @Size(max = 1000, message = "Reason cannot be longer than 1000 characters")
    private String reason;
}