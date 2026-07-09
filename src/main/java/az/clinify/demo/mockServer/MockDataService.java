package az.clinify.demo.mockServer;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import az.clinify.demo.dto.request.ReceptionRegisterRequest;
import az.clinify.demo.exceptions.BaseBadRequestException;
import az.clinify.demo.exceptions.BaseNotFoundException;
import az.clinify.demo.exceptions.UnauthorizedException;
import az.clinify.demo.exceptions.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MockDataService {

    private final MockDataRepository mockDataRepository;

    public boolean verifySignature(String fin, String incomingSignature) {
        if (fin == null || incomingSignature == null) {
            throw new IllegalArgumentException("FIN və ya imza boş ola bilməz!");
        }

        // 1. FIN tapılmayanda exception atırıq
        MockData mockData = mockDataRepository.findByFin(fin)
                .orElseThrow(() -> new UserNotFoundException("user with given fin not found"));

        // 2. İmza (parol) uyğun gəlməyəndə exception atırıq
        if (!mockData.getPassword().equals(incomingSignature)) {
            throw new UnauthorizedException ("signature is false");
        }
        return true;
    }

    public MockData getNewUserData(String fin) {
        MockData mockData = mockDataRepository.findByFin(fin)
                .orElseThrow(() -> new EntityNotFoundException("Mock data not found for this FIN."));

        return mockData;

    }

    public boolean verifyReceptionData(ReceptionRegisterRequest request) {

        MockData mockData = mockDataRepository.findByFin(request.getFin())
                .orElseThrow(() -> new UserNotFoundException("FIN not found."));

        if (!mockData.getFirstName().equals(request.getFirstName())
                || !mockData.getLastName().equals(request.getLastName())
                || !mockData.getBirthDate().equals(request.getBirthDate())
                || !mockData.getGender().equals(request.getGender())) {

            throw new BaseBadRequestException("Entered information does not match official records.");
        }

        return true;
    }
}