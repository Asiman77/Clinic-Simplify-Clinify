package az.clinify.demo.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerificationRequest {

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String otpCode;
}
