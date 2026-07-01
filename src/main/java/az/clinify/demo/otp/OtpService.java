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
    private final RestClient restClient;

    @Value("${sms.api.key}")
    private String apiKey;

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

    public void sendOtp(String number) {

        String otp = generateOtp(number);

        String smsText = "Sizin OTP kodunuz: " + otp;
        SmsRequest requestBody = new SmsRequest(number, smsText, "OTP 1SMS");

        try {
            SmsResponse response = restClient.post()
                    .uri("https://1sms.az/api/v1/sms/otp")
                    .header("X-API-Key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(SmsResponse.class); // Gələn json-ı SmsResponse class-ına çevirir

        } catch (Exception e) {
            // SMS getməsə belə sistemin çökməməsi üçün xətanı loglayırıq
            System.err.println("SMS göndərilərkən xəta baş verdi: " + e.getMessage());
            throw new RuntimeException("SMS provayder xətası: " + e.getMessage());
        }

    }

    private String generateOtp(String phoneNumber) {

        // create otp
        String otp = String.format("%04d", new Random().nextInt(10000));

        // save in db
        LocalDateTime expireAt = LocalDateTime.now().plusMinutes(3);
        OtpCode newOtp = new OtpCode(phoneNumber, otp, expireAt);

        otpRepository.save(newOtp);

        return otp;
    }
}
