package az.clinify.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import az.clinify.demo.dto.request.AuthRequestDTO;
import az.clinify.demo.dto.request.FinCheckRequest;
import az.clinify.demo.dto.request.PasswordSetupRequest;
import az.clinify.demo.dto.request.ReceptionRegisterRequest;
import az.clinify.demo.dto.response.AuthResponse;
import az.clinify.demo.dto.response.FinCheckResponse;
import az.clinify.demo.dto.response.RegisterVerifyResponse;
import az.clinify.demo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/check-fin")
    public ResponseEntity<FinCheckResponse> checkFin(@Valid @RequestBody FinCheckRequest request) {
        return ResponseEntity.ok(authService.checkFin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequestDTO request,
            HttpServletResponse servletResponse) {
        AuthResponse authResponse = authService.login(request);

        ResponseCookie cookie = ResponseCookie
                .from("token", authResponse.getToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        servletResponse.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse servletResponse) {
        ResponseCookie expiredCookie = ResponseCookie
                .from("token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        servletResponse.addHeader(
                HttpHeaders.SET_COOKIE,
                expiredCookie.toString());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register/verify")
    public ResponseEntity<RegisterVerifyResponse> verifyAndRegister(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.registerFromMock(request));
    }

    @PostMapping("/register/setup-password")
    public ResponseEntity<String> setupPassword(@Valid @RequestBody PasswordSetupRequest request) {
        return ResponseEntity.ok(authService.setupPassword(request));
    }

    @PreAuthorize("hasRole('RECEPTION')")
    @PostMapping("/register-new-user")
    public ResponseEntity<String> registerFromReception(
            @RequestBody ReceptionRegisterRequest request) {

        return ResponseEntity.ok(authService.registerFromReception(request));
    }
}