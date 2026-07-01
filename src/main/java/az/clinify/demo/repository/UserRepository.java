package az.clinify.demo.repository;

import az.clinify.demo.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByFin(String fin);

}