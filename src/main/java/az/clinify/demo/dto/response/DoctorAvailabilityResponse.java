package az.clinify.demo.dto.response;

import az.clinify.demo.enums.AvailabilityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityResponse {

    private Long id;

    private Long doctorId;

    private String doctorFirstName;

    private String doctorLastName;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer slotDurationMinutes;

    private AvailabilityType availabilityType;

    private Boolean active;

}