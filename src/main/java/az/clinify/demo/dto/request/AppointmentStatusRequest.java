package az.clinify.demo.dto.request;

import az.clinify.demo.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusRequest {
    @NotNull(message = "Appointment status is required")
    private AppointmentStatus status;
}
