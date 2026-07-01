package az.clinify.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor

public class SmsResponse {

    private boolean success;
    private String message;
    private String id;

}
