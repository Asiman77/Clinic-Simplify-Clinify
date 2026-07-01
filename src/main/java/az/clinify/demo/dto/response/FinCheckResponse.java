package az.clinify.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FinCheckResponse {


    private String fin;

    private String status;
    private String message;

}
