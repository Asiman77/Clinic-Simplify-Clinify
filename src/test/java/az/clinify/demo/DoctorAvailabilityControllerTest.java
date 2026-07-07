package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.controller.DoctorAvailabilityController;
import az.clinify.demo.dto.request.CreateDoctorAvailabilityRequest;
import az.clinify.demo.dto.request.UpdateDoctorAvailabilityRequest;
import az.clinify.demo.dto.response.DoctorAvailabilityResponse;
import az.clinify.demo.enums.AvailabilityType;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.DoctorAvailabilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DoctorAvailabilityController.class)
@Import({
        SecurityConfig.class
})
class DoctorAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorAvailabilityService availabilityService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = buildObjectMapper();

    private static final AvailabilityType SAMPLE_TYPE = AvailabilityType.values()[0];

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    private DoctorAvailabilityResponse buildResponse(Long id) {
        DoctorAvailabilityResponse response = new DoctorAvailabilityResponse();
        response.setId(id);
        response.setDoctorId(1L);
        response.setDoctorFirstName("Elvin");
        response.setDoctorLastName("Mammadov");
        response.setDayOfWeek(DayOfWeek.MONDAY);
        response.setStartTime(LocalTime.of(9, 0));
        response.setEndTime(LocalTime.of(17, 0));
        response.setSlotDurationMinutes(30);
        response.setAvailabilityType(SAMPLE_TYPE);
        response.setActive(true);
        return response;
    }

    private CreateDoctorAvailabilityRequest buildCreateRequest() {
        CreateDoctorAvailabilityRequest request = new CreateDoctorAvailabilityRequest();
        request.setDoctorId(1L);
        request.setDayOfWeek(DayOfWeek.MONDAY);
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(17, 0));
        request.setSlotDurationMinutes(30);
        request.setAvailabilityType(SAMPLE_TYPE);
        request.setActive(true);
        return request;
    }

    private UpdateDoctorAvailabilityRequest buildUpdateRequest() {
        UpdateDoctorAvailabilityRequest request = new UpdateDoctorAvailabilityRequest();
        request.setDayOfWeek(DayOfWeek.TUESDAY);
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(18, 0));
        request.setSlotDurationMinutes(45);
        request.setAvailabilityType(SAMPLE_TYPE);
        request.setActive(false);
        return request;
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void createAvailability_validRequest_returnsCreated() throws Exception {
        CreateDoctorAvailabilityRequest request = buildCreateRequest();
        DoctorAvailabilityResponse response = buildResponse(1L);

        when(availabilityService.createAvailability(any(CreateDoctorAvailabilityRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/availabilities")
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.slotDurationMinutes").value(30));

        verify(availabilityService, times(1))
                .createAvailability(any(CreateDoctorAvailabilityRequest.class));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void createAvailability_missingRequiredFields_returnsBadRequest() throws Exception {
        CreateDoctorAvailabilityRequest request = new CreateDoctorAvailabilityRequest();
        mockMvc.perform(post("/api/availabilities")
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                "doctor", null, List.of(new SimpleGrantedAuthority("ROLE_DOCTOR")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(availabilityService, never()).createAvailability(any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void createAvailability_invalidSlotDuration_returnsBadRequest() throws Exception {
        CreateDoctorAvailabilityRequest request = buildCreateRequest();
        request.setSlotDurationMinutes(0);

        mockMvc.perform(post("/api/availabilities")
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(availabilityService, never()).createAvailability(any());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void createAvailability_wrongRole_returnsForbidden() throws Exception {
        CreateDoctorAvailabilityRequest request = buildCreateRequest();

        mockMvc.perform(post("/api/availabilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(availabilityService, never()).createAvailability(any());
    }
}