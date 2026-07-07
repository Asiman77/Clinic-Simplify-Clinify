package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.controller.AppointmentController;
import az.clinify.demo.dto.request.AppointmentRequestDTO;
import az.clinify.demo.dto.request.AppointmentStatusRequest;
import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.enums.AppointmentType;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.AppointmentBookingService;
import az.clinify.demo.service.AppointmentManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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

    @Test
    void getAllAppointments_ShouldReturnOk_WhenAdmin() throws Exception {

        when(appointmentManagementService.getAllAppointments())
                .thenReturn(List.of(sampleResponse()));


        mockMvc.perform(get("/api/appointments")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                "admin",
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("REQUESTED"));


        verify(appointmentManagementService)
                .getAllAppointments();
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getAllAppointments_ShouldReturnForbidden_WhenPatient() throws Exception {


        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isForbidden());


        verify(appointmentManagementService, never())
                .getAllAppointments();
    }


    @Test
    @WithMockUser(roles = "PATIENT")
    void getAppointmentById_ShouldReturnOk() throws Exception {

        when(appointmentManagementService.getAppointmentById(1L))
                .thenReturn(sampleResponse());


        mockMvc.perform(get("/api/appointments/{id}", 1L)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                "patient",
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));


        verify(appointmentManagementService)
                .getAppointmentById(1L);
    }


    @Test
    @WithMockUser(roles = "DOCTOR")
    void getByPatient_ShouldReturnForbidden_WhenDoctor() throws Exception {


        mockMvc.perform(get("/api/appointments/patient/{id}",10L))
                .andExpect(status().isForbidden());


        verify(appointmentManagementService, never())
                .getByPatient(any());
    }


    @Test
    void getByDoctor_ShouldReturnOk() throws Exception {

        when(appointmentManagementService.getByDoctor(20L))
                .thenReturn(List.of(sampleResponse()));


        mockMvc.perform(get("/api/appointments/doctor/{id}", 20L)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                "doctor",
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].doctorId").value(20));


        verify(appointmentManagementService)
                .getByDoctor(20L);
    }


    @Test
    @WithMockUser(roles = "PATIENT")
    void getByDoctor_ShouldReturnForbidden_WhenPatient() throws Exception {


        mockMvc.perform(get("/api/appointments/doctor/{id}",20L))
                .andExpect(status().isForbidden());


        verify(appointmentManagementService, never())
                .getByDoctor(any());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void createAppointment_ShouldReturnCreated() throws Exception {


        AppointmentRequestDTO request =
                new AppointmentRequestDTO(
                        20L,
                        10L,
                        AppointmentType.ONLINE,
                        LocalDateTime.now().plusDays(1),
                        "Routine check-up"
                );


        when(appointmentBookingService.createAppointment(any()))
                .thenReturn(sampleResponse());


        mockMvc.perform(post("/api/appointments")
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                "patient",
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
                        )))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));


        verify(appointmentBookingService)
                .createAppointment(any());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void createAppointment_ShouldReturnForbidden_WhenDoctor() throws Exception {


        AppointmentRequestDTO request =
                new AppointmentRequestDTO(
                        20L,
                        10L,
                        AppointmentType.ONLINE,
                        LocalDateTime.now().plusDays(1),
                        "Routine check-up"
                );



        mockMvc.perform(post("/api/appointments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());



        verify(appointmentBookingService, never())
                .createAppointment(any());
    }

}


