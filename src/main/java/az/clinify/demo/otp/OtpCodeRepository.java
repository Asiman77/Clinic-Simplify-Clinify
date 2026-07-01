package az.clinify.demo.otp;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    // Last Added otp of given number
    Optional<OtpCode> findFirstByPhoneNumberOrderByIdDesc(String phoneNumber);
}
