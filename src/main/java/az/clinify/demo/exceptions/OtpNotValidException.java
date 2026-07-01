package az.clinify.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Bu xəta atılanda Spring avtomatik olaraq HTTP 403 statusu qaytaracaq
@ResponseStatus(HttpStatus.FORBIDDEN)
public class OtpNotValidException extends RuntimeException {
    public OtpNotValidException(String message) {
        super(message);
    }
}
