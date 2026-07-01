package az.clinify.demo.otp;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import az.clinify.demo.exceptions.OtpNotValidException;
import jakarta.transaction.Transactional;

@Service
public class OtpService {

    private final OtpCodeRepository otpRepository;

    public OtpService(OtpCodeRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Transactional
    public boolean verifyOtp(String phoneNumber, String userInputCode) {
        OtpCode otp = otpRepository.findFirstByPhoneNumberOrderByIdDesc(phoneNumber)
                .orElseThrow(() -> new OtpNotValidException("Bu nömrəyə aid OTP tapılmadı!"));

        // 1) is expired
        if (otp.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new OtpNotValidException("OTP kodun vaxtı bitib! Yenidən cəhd edin.");
        }

        //3) is used
        if(otp.isUsed()){
            throw new OtpNotValidException("This otp is used. Please try new.");
        }

        // 2) is code correct
        if (!otp.getOtpCode().equals(userInputCode)) {
            throw new OtpNotValidException("Daxil edilən OTP kod yanlışdır!");
        }

        return true;
    }
}
