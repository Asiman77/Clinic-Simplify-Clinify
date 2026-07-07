package az.clinify.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class FinCheckRequest {
    @NotBlank(message = "FIN boş ola bilməz")
    @Size(min = 7, max = 7, message = "FIN kodu dəqiq 7 simvoldan ibarət olmalıdır")
    private String fin;

    public FinCheckRequest() {

    }
}