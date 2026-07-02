package az.clinify.demo.configs;


import az.clinify.demo.entity.Role;
import az.clinify.demo.enums.RoleType;
import az.clinify.demo.repository.RoleRepository;
import az.clinify.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor

public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void run(String... args) {
        Role superAdminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(RoleType.ADMIN);
                    return roleRepository.save(role);
                });

    }
}
