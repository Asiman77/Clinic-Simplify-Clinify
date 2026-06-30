package az.clinify.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class LoginRequestDTO {
    @NotBlank(message = "FIN cannot be blank")
    @Size(min = 7,max = 7,message = "FIN must be exactly 7 characters")
    private String fin;

    private String password;
//passwd null gede bilsin
}
