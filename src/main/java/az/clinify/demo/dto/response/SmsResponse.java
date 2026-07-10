package az.clinify.demo.dto.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor

public class SmsResponse {

    private boolean success;
    private String message;
    private String id;

}
