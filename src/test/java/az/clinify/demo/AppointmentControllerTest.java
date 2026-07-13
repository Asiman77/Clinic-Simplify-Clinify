package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.controller.AppointmentController;
import az.clinify.demo.dto.request.AppointmentStatusRequest;
import az.clinify.demo.dto.request.PatientAppointmentRequestDTO;
import az.clinify.demo.dto.response.AppointmentResponseDTO;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.enums.AppointmentType;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.AppointmentBookingService;
import az.clinify.demo.service.AppointmentManagementService;
import az.clinify.demo.service.DoctorAppointmentService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

        @MockitoBean
        private DoctorAppointmentService doctorAppointmentService;

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
        void getAppointmentById_ShouldReturnOk_WhenAdmin()
                        throws Exception {

                when(appointmentManagementService.getAppointmentById(1L))
                                .thenReturn(sampleResponse());

                mockMvc.perform(get("/api/appointments/{id}", 1L)
                                .with(authentication(new UsernamePasswordAuthenticationToken(
                                                "admin",
                                                null,
                                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));

                verify(appointmentManagementService).getAppointmentById(1L);
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
        @WithMockUser(roles = "DOCTOR")
        void getByPatient_ShouldReturnForbidden_WhenDoctor() throws Exception {

                mockMvc.perform(get("/api/appointments/patient/{id}", 10L))
                                .andExpect(status().isForbidden());

                verify(appointmentManagementService, never())
                                .getByPatient(anyLong(), any(Pageable.class));
        }

        @Test
        void getCurrentPatientAppointments_ShouldReturnOk()
                        throws Exception {
                Page<AppointmentResponseDTO> page = new PageImpl<>(
                                List.of(sampleResponse()),
                                PageRequest.of(0, 10),
                                1);

                when(appointmentManagementService.getCurrentPatientAppointments(
                                eq("patient"),
                                any(Pageable.class)))
                                .thenReturn(page);

                mockMvc.perform(get("/api/appointments/mine")
                                .param("page", "0")
                                .param("size", "10")
                                .with(authentication(new UsernamePasswordAuthenticationToken(
                                                "patient",
                                                null,
                                                List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalElements").value(1));

                verify(appointmentManagementService)
                                .getCurrentPatientAppointments(
                                                eq("patient"),
                                                any(Pageable.class));
        }

        @Test
        void getByDoctor_ShouldReturnOk_WhenReception()
                        throws Exception {

                Page<AppointmentResponseDTO> page = new PageImpl<>(
                                List.of(sampleResponse()),
                                PageRequest.of(0, 10),
                                1);

                when(appointmentManagementService.getByDoctor(
                                eq(20L),
                                any(Pageable.class)))
                                .thenReturn(page);

                mockMvc.perform(get("/api/appointments/doctor/{id}", 20L)
                                .param("page", "0")
                                .param("size", "10")
                                .with(authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                                "reception",
                                                                null,
                                                                List.of(new SimpleGrantedAuthority(
                                                                                "ROLE_RECEPTION"))))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].doctorId").value(20))
                                .andExpect(jsonPath("$.totalElements").value(1));

                verify(appointmentManagementService)
                                .getByDoctor(eq(20L), any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "PATIENT")
        void getByDoctor_ShouldReturnForbidden_WhenPatient() throws Exception {

                mockMvc.perform(get("/api/appointments/doctor/{id}", 20L))
                                .andExpect(status().isForbidden());

                verify(appointmentManagementService, never())
                                .getByDoctor(anyLong(), any(Pageable.class));
        }

        @Test
        void getByPatient_ShouldReturnOk_WhenReception()
                        throws Exception {

                Page<AppointmentResponseDTO> page = new PageImpl<>(
                                List.of(sampleResponse()),
                                PageRequest.of(0, 10),
                                1);

                when(appointmentManagementService.getByPatient(
                                eq(10L),
                                any(Pageable.class)))
                                .thenReturn(page);

                mockMvc.perform(get("/api/appointments/patient/{id}",
                                10L)
                                .param("page", "0")
                                .param("size", "10")
                                .with(authentication(new UsernamePasswordAuthenticationToken(
                                                "reception",
                                                null,
                                                List.of(new SimpleGrantedAuthority("ROLE_RECEPTION"))))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].patientId")
                                                .value(10))
                                .andExpect(jsonPath("$.totalElements").value(1));

                verify(appointmentManagementService)
                                .getByPatient(eq(10L),
                                                any(Pageable.class));
        }

        @Test
        void createPatientAppointment_ShouldReturnCreated()
                        throws Exception {
                PatientAppointmentRequestDTO request = new PatientAppointmentRequestDTO(
                                20L,
                                LocalDateTime.now().plusDays(1),
                                "Routine check-up");

                when(appointmentBookingService.createPatientAppointment(
                                any(PatientAppointmentRequestDTO.class),
                                eq("patient")))
                                .thenReturn(sampleResponse());

                UsernamePasswordAuthenticationToken patientAuthentication = new UsernamePasswordAuthenticationToken(
                                "patient", null, List.of(new SimpleGrantedAuthority(
                                                "ROLE_PATIENT")));

                mockMvc.perform(post("/api/appointments")
                                .with(authentication(patientAuthentication))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1));

                verify(appointmentBookingService).createPatientAppointment(any(PatientAppointmentRequestDTO.class),
                                eq("patient"));
        }

        @Test
        @WithMockUser(username = "doctor", roles = "DOCTOR")
        void createPatientAppointment_ShouldReturnForbidden_WhenDoctor()
                        throws Exception {
                PatientAppointmentRequestDTO request = new PatientAppointmentRequestDTO(
                                20L,
                                LocalDateTime.now().plusDays(1),
                                "Routine check-up");

                mockMvc.perform(post("/api/appointments")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());

                verify(appointmentBookingService, never()).createPatientAppointment(
                                any(PatientAppointmentRequestDTO.class),
                                anyString());
        }

        @Test
        void updateStatus_ShouldReturnOk_WhenAdmin() throws Exception {
                AppointmentStatusRequest request = new AppointmentStatusRequest(
                                AppointmentStatus.APPROVED);

                AppointmentResponseDTO response = sampleResponse();
                response.setStatus(AppointmentStatus.APPROVED);

                when(appointmentManagementService.updateStatus(
                                eq(1L),
                                any(AppointmentStatusRequest.class))).thenReturn(response);

                mockMvc.perform(
                                patch("/api/appointments/{id}/status", 1L)
                                                .with(csrf())
                                                .with(authentication(
                                                                new UsernamePasswordAuthenticationToken(
                                                                                "admin",
                                                                                null,
                                                                                List.of(new SimpleGrantedAuthority(
                                                                                                "ROLE_ADMIN")))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("APPROVED"));

                verify(appointmentManagementService)
                                .updateStatus(eq(1L), any(AppointmentStatusRequest.class));
        }

        @Test
        void updateStatus_ShouldReturnForbidden_WhenPatient() throws Exception {

                AppointmentStatusRequest request = new AppointmentStatusRequest(AppointmentStatus.APPROVED);

                mockMvc.perform(
                                patch("/api/appointments/{id}/status", 1L)
                                                .with(csrf())
                                                .with(authentication(
                                                                new UsernamePasswordAuthenticationToken(
                                                                                "patient",
                                                                                null,
                                                                                List.of(new SimpleGrantedAuthority(
                                                                                                "ROLE_PATIENT")))))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());

                verify(appointmentManagementService, never())
                                .updateStatus(any(), any());
        }

        @Test
        void getCurrentDoctorAppointments_ShouldReturnOk_WhenDoctor()
                        throws Exception {

                Page<AppointmentResponseDTO> page = new PageImpl<>(
                                List.of(sampleResponse()),
                                PageRequest.of(0, 10),
                                1);

                when(doctorAppointmentService.getCurrentDoctorAppointments(
                                eq("doctor"),
                                any(Pageable.class)))
                                .thenReturn(page);

                mockMvc.perform(get("/api/appointments/doctor/mine")
                                .param("page", "0")
                                .param("size", "10")
                                .with(authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                                "doctor",
                                                                null,
                                                                List.of(new SimpleGrantedAuthority(
                                                                                "ROLE_DOCTOR"))))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalElements").value(1))
                                .andExpect(jsonPath("$.content[0].doctorId").value(20));

                verify(doctorAppointmentService)
                                .getCurrentDoctorAppointments(
                                                eq("doctor"),
                                                any(Pageable.class));
        }

        @Test
        void approveAppointment_ShouldReturnOk_WhenDoctor()
                        throws Exception {

                AppointmentResponseDTO response = sampleResponse();
                response.setStatus(AppointmentStatus.APPROVED);

                when(doctorAppointmentService.approve(1L, "doctor"))
                                .thenReturn(response);

                mockMvc.perform(patch("/api/appointments/{id}/approve", 1L)
                                .with(csrf())
                                .with(authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                                "doctor",
                                                                null,
                                                                List.of(new SimpleGrantedAuthority(
                                                                                "ROLE_DOCTOR"))))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("APPROVED"));

                verify(doctorAppointmentService).approve(1L, "doctor");
        }

}
