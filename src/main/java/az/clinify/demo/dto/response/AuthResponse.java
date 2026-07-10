package az.clinify.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    @JsonIgnore
    private String token;

    private String fin;

    private Set<String> roles;

}
