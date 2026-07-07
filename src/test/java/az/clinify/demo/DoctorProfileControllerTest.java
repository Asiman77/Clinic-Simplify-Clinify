package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.controller.DoctorProfileController;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.AvailableSlotService;
import az.clinify.demo.service.DoctorProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import az.clinify.demo.dto.response.AvailableSlotResponse;
import az.clinify.demo.dto.response.DoctorProfileResponse;
import az.clinify.demo.enums.AppointmentType;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import az.clinify.demo.dto.request.CreateDoctorProfileRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(DoctorProfileController.class)
@Import(SecurityConfig.class)
class DoctorProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DoctorProfileService doctorProfileService;

    @MockitoBean
    private AvailableSlotService availableSlotService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void getAllDoctors_ShouldReturnOk() throws Exception {
        DoctorProfileResponse response = new DoctorProfileResponse(
                1L,
                10L,
                "Aygun",
                "Aliyeva",
                "aygun@example.com",
                2L,
                "Cardiology",
                "Cardiologist",
                "Experienced cardiologist",
                5,
                true
        );

        when(doctorProfileService.getAllDoctors())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].doctorFirstName").value("Aygun"))
                .andExpect(jsonPath("$[0].doctorLastName").value("Aliyeva"));

        verify(doctorProfileService).getAllDoctors();
    }

    @Test
    @WithMockUser
    void getDoctorById_ShouldReturnOk() throws Exception {
        DoctorProfileResponse response = new DoctorProfileResponse(
                1L,
                10L,
                "Aygun",
                "Aliyeva",
                "aygun@example.com",
                2L,
                "Cardiology",
                "Cardiologist",
                "Experienced cardiologist",
                5,
                true
        );

        when(doctorProfileService.getDoctorById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/doctors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.specialization").value("Cardiologist"));

        verify(doctorProfileService).getDoctorById(1L);
    }

    @Test
    @WithMockUser
    void getAvailableSlots_ShouldReturnOk() throws Exception {
        LocalDate date = LocalDate.of(2026, 7, 10);

        AvailableSlotResponse slot = new AvailableSlotResponse(
                LocalDateTime.of(2026, 7, 10, 10, 0),
                LocalDateTime.of(2026, 7, 10, 10, 30),
                true
        );

        when(availableSlotService.getAvailableSlots(
                eq(1L),
                eq(date),
                eq(AppointmentType.WALK_IN)))
                .thenReturn(List.of(slot));

        mockMvc.perform(get("/api/doctors/{id}/available-slots", 1L)
                        .param("date", "2026-07-10")
                        .param("type", "WALK_IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));

        verify(availableSlotService)
                .getAvailableSlots(1L, date, AppointmentType.WALK_IN);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDoctor_ShouldReturnCreated_WhenUserIsAdmin() throws Exception {

        CreateDoctorProfileRequest request = new CreateDoctorProfileRequest(
                10L,
                2L,
                "Cardiologist",
                "Experienced cardiologist",
                5,
                true
        );

        DoctorProfileResponse response = new DoctorProfileResponse(
                1L,
                10L,
                "Aygun",
                "Aliyeva",
                "aygun@example.com",
                2L,
                "Cardiology",
                "Cardiologist",
                "Experienced cardiologist",
                5,
                true
        );

        when(doctorProfileService.createDoctor(any(CreateDoctorProfileRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/doctors")
                                .with(csrf())
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                "admin",
                                                null,
                                                List.of(
                                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                                )
                                        )
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        verify(doctorProfileService)
                .createDoctor(any(CreateDoctorProfileRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createDoctor_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {

        CreateDoctorProfileRequest request = new CreateDoctorProfileRequest(
                10L,
                2L,
                "Cardiologist",
                "Experienced cardiologist",
                5,
                true
        );

        mockMvc.perform(
                        post("/api/doctors")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());

        verify(doctorProfileService, never())
                .createDoctor(any());
    }
}