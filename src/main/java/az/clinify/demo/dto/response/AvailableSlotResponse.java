package az.clinify.demo.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean available;
    
}
