package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.controller.AppointmentController;
import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.enums.AppointmentType;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.AppointmentBookingService;
import az.clinify.demo.service.AppointmentManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@WebMvcTest(AppointmentController.class)
@Import(SecurityConfig.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private AppointmentManagementService appointmentManagementService;

    @MockitoBean
    private AppointmentBookingService appointmentBookingService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private AppointmentResponseDTO sampleResponse() {

        AppointmentResponseDTO response = new AppointmentResponseDTO();

        response.setId(1L);
        response.setPatientId(10L);
        response.setPatientFullName("Ali Aliyev");

        response.setDoctorId(20L);
        response.setDoctorFullName("Dr. Vusal Mammadov");

        response.setCreatedById(10L);
        response.setCreatedByFullName("Ali Aliyev");

        response.setType(AppointmentType.ONLINE);
        response.setStatus(AppointmentStatus.REQUESTED);

        response.setStartTime(LocalDateTime.now().plusDays(1));
        response.setEndTime(LocalDateTime.now().plusDays(1).plusMinutes(30));

        response.setReason("Routine check-up");

        return response;
    }

}