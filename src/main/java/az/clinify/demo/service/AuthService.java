package az.clinify.demo.service;

import org.springframework.stereotype.Service;

import az.clinify.demo.dto.request.AuthRequestDTO;
import az.clinify.demo.dto.request.FinCheckRequest;
import az.clinify.demo.dto.response.AuthResponse;
import az.clinify.demo.dto.response.FinCheckResponse;
import az.clinify.demo.entity.Role;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.RoleType;
import az.clinify.demo.exceptions.UserNotFoundException;
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
        boolean present = userRepository.findByFin(fin).isPresent();

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

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadCredentialsException("Password is wrong.");
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

    public String registerFromMock(AuthRequestDTO request) {
        MockData mockData = mockDataRepository.findByFin(request.getFin())
                .orElseThrow(() -> new EntityNotFoundException("Bu FIN mock serverdə tapılmadı."));

        if (!mockData.getPassword().equals(request.getPassword())) {
            throw new BadCredentialsException("Daxil edilən mock imza yanlışdır!");
        }

        User newUser = new User();
        newUser.setFin(mockData.getFin());
        newUser.setFirstName(mockData.getFirstName());
        newUser.setLastName(mockData.getLastName());
        newUser.setGender(mockData.getGender());
        newUser.setBirthDate(mockData.getBirthDate());


        //idk if 
        newUser.setPassword(request.getPassword());

        userRepository.save(newUser);

        return "Qeydiyyat tamamlandı. Zəhmət olmasa yenidən login endpointinə müraciət edin.";
    }

}
