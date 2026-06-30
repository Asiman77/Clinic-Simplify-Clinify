package az.clinify.demo.mapper;

import az.clinify.demo.dto.response.UserResponse;
import az.clinify.demo.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {

        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFin(user.getFin());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setGender(user.getGender());
        response.setBirthDate(user.getBirthDate());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setHasAccount(user.isHasAccount());

        response.setRoles(
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );

        return response;
    }
}