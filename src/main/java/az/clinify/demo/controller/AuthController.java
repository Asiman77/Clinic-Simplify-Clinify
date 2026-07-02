package az.clinify.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import az.clinify.demo.dto.request.AuthRequestDTO;
import az.clinify.demo.dto.request.FinCheckRequest;
import az.clinify.demo.dto.request.PasswordSetupRequest;
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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<RegisterVerifyResponse> verifyAndRegister(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.registerFromMock(request));
    }

    @PostMapping("/register/setup-password")
    public ResponseEntity<String> setupPassword(@Valid @RequestBody PasswordSetupRequest request) {
        return ResponseEntity.ok(authService.setupPassword(request));
    }
}