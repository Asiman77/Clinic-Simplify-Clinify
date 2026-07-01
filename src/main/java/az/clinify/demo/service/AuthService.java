package az.clinify.demo.service;

import org.springframework.stereotype.Service;

import az.clinify.demo.dto.request.AuthRequestDTO;
import az.clinify.demo.dto.request.FinCheckRequest;
import az.clinify.demo.dto.response.AuthResponse;
import az.clinify.demo.dto.response.FinCheckResponse;
import az.clinify.demo.entity.Role;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.RoleType;
import az.clinify.demo.mockServer.MockData;
import az.clinify.demo.mockServer.MockDataRepository;
import az.clinify.demo.repository.UserRepository;
import az.clinify.demo.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final MockDataRepository mockDataRepository;
    private final JwtTokenProvider jwtTokenProvider;



    public FinCheckResponse checkFin(FinCheckRequest request) {
        String fin = request.getFin();

        if (userRepository.findByFin(fin).isPresent()) {
            return new FinCheckResponse(fin, "LOGIN_REQUIRED", "İstifadəçi mövcuddur. Sistem parolunu daxil edin.");
        }

        if (mockDataRepository.findByFin(fin).isPresent()) {
            return new FinCheckResponse(fin, "REGISTER_REQUIRED", "İstifadəçi tapılmadı. Dövlət imzasını daxil edin.");
        }

        throw new EntityNotFoundException("Daxil edilən FIN kodu sistemdə tapılmadı!");
    }
}
