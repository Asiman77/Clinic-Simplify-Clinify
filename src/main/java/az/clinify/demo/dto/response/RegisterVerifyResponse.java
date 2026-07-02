package az.clinify.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterVerifyResponse {
    private String fin;
    private boolean verified;
    private String message;
    private String status; // Məsələn: "SETUP_PASSWORD_REQUIRED"
}