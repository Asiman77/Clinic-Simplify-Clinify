package az.clinify.demo.repository;

import az.clinify.demo.entity.Role;
import az.clinify.demo.enums.RoleType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);

}