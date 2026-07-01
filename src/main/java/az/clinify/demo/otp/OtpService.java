package az.clinify.demo.otp;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import az.clinify.demo.dto.request.SmsRequest;
import az.clinify.demo.dto.response.SmsResponse;
import az.clinify.demo.exceptions.OtpNotValidException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpCodeRepository otpRepository;


    @Transactional
    public boolean verifyOtp(String phoneNumber, String userInputCode) {
        OtpCode otp = otpRepository.findFirstByPhoneNumberOrderByIdDesc(phoneNumber)
                .orElseThrow(() -> new OtpNotValidException("Bu nömrəyə aid OTP tapılmadı!"));

        // 1) is expired
        if (otp.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new OtpNotValidException("OTP kodun vaxtı bitib! Yenidən cəhd edin.");
        }

        // 3) is used
        if (otp.isUsed()) {
            throw new OtpNotValidException("This otp is used. Please try new.");
        }

        // 2) is code correct
        if (!otp.getOtpCode().equals(userInputCode)) {
            throw new OtpNotValidException("Daxil edilən OTP kod yanlışdır!");
        }

        return true;
    }
}
