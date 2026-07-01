package az.clinify.demo.dto.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SmsRequest {
    private String to;
    private String text;
    private String senderName;

}
