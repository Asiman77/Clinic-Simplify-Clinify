package az.clinify.demo.service;

import org.springframework.stereotype.Service;

import az.clinify.demo.dto.request.AuthRequestDTO;
import az.clinify.demo.dto.request.FinCheckRequest;
import az.clinify.demo.dto.request.PasswordSetupRequest;
import az.clinify.demo.dto.request.ReceptionRegisterRequest;
import az.clinify.demo.dto.response.AuthResponse;
import az.clinify.demo.dto.response.FinCheckResponse;
import az.clinify.demo.dto.response.RegisterVerifyResponse;
import az.clinify.demo.entity.Role;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.RoleType;
import az.clinify.demo.exceptions.BaseBadRequestException;
import az.clinify.demo.exceptions.UserNotFoundException;
import az.clinify.demo.mockServer.MockData;
import az.clinify.demo.mockServer.MockDataService;
import az.clinify.demo.repository.UserRepository;
import az.clinify.demo.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MockDataService mockDataService;
    private final BCryptPasswordEncoder passwordEncoder;

    public FinCheckResponse checkFin(FinCheckRequest request) {
        String fin = request.getFin();
        boolean present = userRepository.findByFin(fin).get().isHasAccount();

        if (present) {
            return new FinCheckResponse(fin, "LOGIN_REQUIRED", "Please insert password");
        }

        if (!present) {
            return new FinCheckResponse(fin, "REGISTER_REQUIRED", "You are not registered. Please insert signature.");
        }

        throw new EntityNotFoundException("this fin does not exist.");
    }

    public AuthResponse login(AuthRequestDTO request) {
        User user = userRepository.findByFin(request.getFin())
                .orElseThrow(() -> new UserNotFoundException("This fin does not exist"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseBadRequestException("Password is wrong.");
        }
        Set<String> rolesList = user.getRoles().stream()
                .map(Role::getName)
                .map(RoleType::name)
                .collect(Collectors.toSet());

        String token = jwtTokenProvider.generateToken(user.getFin(), rolesList);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setFin(user.getFin());
        response.setRoles(rolesList);

        return response;
    }

    public RegisterVerifyResponse registerFromMock(AuthRequestDTO request) {

        mockDataService.verifySignature(request.getFin(), request.getPassword());

        return new RegisterVerifyResponse(
                request.getFin(),
                true,
                "Signature verified successfully. Please proceed to setup your password.",
                "SETUP_PASSWORD_REQUIRED");

    }

    public String registerFromReception(ReceptionRegisterRequest request) {

        if (userRepository.findByFin(request.getFin()).isPresent()) {
            throw new BaseBadRequestException("User is already registered.");
        }

        boolean isValid = mockDataService.verifyReceptionData(request);

        if (isValid) {
            User newUser = new User();

            newUser.setFin(request.getFin());
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setBirthDate(request.getBirthDate());
            newUser.setGender(request.getGender());

            userRepository.save(newUser);
        }

        return "Account created successfully.";
    }

    public String setupPassword(PasswordSetupRequest request) {
        if (userRepository.findByFin(request.getFin()).isPresent()) {
            User user = userRepository.findByFin(request.getFin()).get();

            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setHasAccount(true);
            userRepository.save(user);

        }

        return "Account created successfully! Please proceed to the login page.";
    }

}