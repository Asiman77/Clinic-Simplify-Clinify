package az.clinify.demo.otp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_codes")
@Getter
@Setter
@NoArgsConstructor
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    public OtpCodeEntity(String phoneNumber, String otpCode, LocalDateTime expireAt) {
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
        this.expireAt = expireAt;
        this.isVerified = false;
    }
}