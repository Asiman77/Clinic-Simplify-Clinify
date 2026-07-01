package az.clinify.demo.mockServer;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import az.clinify.demo.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MockDataService {

    private final MockDataRepository mockDataRepository;

    public void verifySignature(String fin, String incomingSignature) {
    if (fin == null || incomingSignature == null) {
        throw new IllegalArgumentException("FIN və ya imza boş ola bilməz!");
    }

    // 1. FIN tapılmayanda exception atırıq
    MockData mockData = mockDataRepository.findByFin(fin)
            .orElseThrow(() -> new UserNotFoundException("Bu FIN koduna sahib istifadəçi tapılmadı: " + fin));

    // 2. İmza (parol) uyğun gəlməyəndə exception atırıq
    if (!mockData.getPassword().equals(incomingSignature)) {
        throw new BadCredentialsException("Daxil edilən imza/parol yanlışdır!");
    }
}
}