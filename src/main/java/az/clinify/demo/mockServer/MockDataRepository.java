package az.clinify.demo.mockServer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MockDataRepository extends JpaRepository<MockData, Long> {
    
    Optional<MockData> findByFin(String fin);
}
