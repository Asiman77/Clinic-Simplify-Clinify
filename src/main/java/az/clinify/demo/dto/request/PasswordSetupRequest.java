package az.clinify.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordSetupRequest {
    @NotBlank(message = "FIN cannot be blank")
    @Size(min = 7, max = 7, message = "FIN must be exactly 7 characters")
    private String fin;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public PasswordSetupRequest() {

    }
}