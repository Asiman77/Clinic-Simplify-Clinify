package az.clinify.demo.dto.response;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ErrorResponse {
    private int status;
    private String error;
    private String message;



    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
