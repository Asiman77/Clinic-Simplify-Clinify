package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.controller.AuthController;
import az.clinify.demo.dto.request.AuthRequestDTO;
import az.clinify.demo.dto.request.FinCheckRequest;
import az.clinify.demo.dto.request.PasswordSetupRequest;
import az.clinify.demo.dto.response.AuthResponse;
import az.clinify.demo.dto.response.FinCheckResponse;
import az.clinify.demo.dto.response.RegisterVerifyResponse;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void checkFin_ShouldReturnOk_WhenUserExists() throws Exception {
        FinCheckRequest request = new FinCheckRequest();
        request.setFin("1234567");

        FinCheckResponse response = new FinCheckResponse("1234567", "LOGIN_REQUIRED", "Please insert password");
        when(authService.checkFin(any(FinCheckRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/check-fin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fin").value("1234567"))
                .andExpect(jsonPath("$.status").value("LOGIN_REQUIRED"))
                .andExpect(jsonPath("$.message").value("Please insert password"));

        verify(authService).checkFin(any(FinCheckRequest.class));
    }

    @Test
    void login_ShouldReturnOk_WhenCredentialsAreValid() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO();
        request.setFin("7654321");
        request.setPassword("correctPassword");

        AuthResponse response = new AuthResponse();
        response.setToken("mocked-jwt-token");
        response.setFin("7654321");
        response.setRoles(Set.of("USER"));

        when(authService.login(any(AuthRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.fin").value("7654321"))
                .andExpect(jsonPath("$.roles[0]").value("USER"));

        verify(authService).login(any(AuthRequestDTO.class));
    }
    @Test
    void verifyAndRegister_ShouldReturnOk_WhenSignatureIsValid() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO();
        request.setFin("9999999");
        request.setPassword("signature_data");

        RegisterVerifyResponse response = new RegisterVerifyResponse(
                "9999999",
                true,
                "Signature verified successfully. Please proceed to setup your password.",
                "SETUP_PASSWORD_REQUIRED"
        );

        when(authService.registerFromMock(any(AuthRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fin").value("9999999"))
                .andExpect(jsonPath("$.verified").value(true))
                .andExpect(jsonPath("$.message").value("Signature verified successfully. Please proceed to setup your password."))
                .andExpect(jsonPath("$.status").value("SETUP_PASSWORD_REQUIRED"));

        verify(authService).registerFromMock(any(AuthRequestDTO.class));
    }


}