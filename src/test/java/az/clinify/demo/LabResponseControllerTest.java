package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.dto.request.DeleteLabResponseFileRequest;
import az.clinify.demo.controller.LabResponseController;
import az.clinify.demo.dto.request.LabResponseStatusRequest;
import az.clinify.demo.dto.request.UpdateLabResponseRequest;
import az.clinify.demo.dto.response.LabResponseDetailDTO;
import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.dto.response.LabResponseSummaryDTO;
import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.LabResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(LabResponseController.class)
@Import(SecurityConfig.class)
class LabResponseControllerTest {

        @Autowired
        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @MockitoBean
        private LabResponseService labResponseService;

        @MockitoBean
        private JwtTokenProvider jwtTokenProvider;

        @MockitoBean
        private UserDetailsService userDetailsService;

        private LabResponseSummaryDTO sampleSummary() {
                return new LabResponseSummaryDTO(
                                1L,
                                100L,
                                10L,
                                "Ali Aliyev",
                                20L,
                                "Vusal Mammadov",
                                "Blood Test",
                                LabStatuses.PENDING,
                                LocalDateTime.now(),
                                LocalDateTime.now());
        }

        private UsernamePasswordAuthenticationToken authenticationFor(String username, String role) {
                return new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority(
                                                "ROLE_" + role)));
        }

        @Test
        void getLabResponseById_ShouldReturnOk() throws Exception {
                LabResponseDetailDTO response = new LabResponseDetailDTO();

                response.setId(1L);
                response.setTestName("Blood Test");
                response.setPatientFullName("Ali Aliyev");

                when(labResponseService.getLabResponseDetail(1L))
                                .thenReturn(response);

                mockMvc.perform(get("/api/lab-responses/{id}", 1L)
                                .with(authentication(
                                                authenticationFor(
                                                                "lab-technician",
                                                                "LAB_TECHNICIAN"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.testName")
                                                .value("Blood Test"));

                verify(labResponseService)
                                .getLabResponseDetail(1L);
        }

        @Test
        void getAllLabResponses_ShouldReturnOk() throws Exception {
                Page<LabResponseSummaryDTO> page = new PageImpl<>(
                                List.of(sampleSummary()),
                                PageRequest.of(0, 10),
                                1);

                when(labResponseService.getAllLabResponses(
                                any(Pageable.class)))
                                .thenReturn(page);

                mockMvc.perform(get("/api/lab-responses")
                                .with(authentication(
                                                authenticationFor(
                                                                "admin",
                                                                "ADMIN")))
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id")
                                                .value(1))
                                .andExpect(jsonPath("$.content[0].testName")
                                                .value("Blood Test"));

                verify(labResponseService)
                                .getAllLabResponses(any(Pageable.class));
        }

        @Test
        void getLabResponsesByMedicalRecordId_ShouldReturnOk()
                        throws Exception {

                LabResponseResponseDTO response = new LabResponseResponseDTO();

                when(labResponseService
                                .getLabResponsesByMedicalRecordId(100L))
                                .thenReturn(List.of(response));

                mockMvc.perform(get("/api/lab-responses/medical-record/{medicalRecordId}",
                                100L)
                                .with(authentication(authenticationFor(
                                                "lab-technician",
                                                "LAB_TECHNICIAN"))))
                                .andExpect(status().isOk());

                verify(labResponseService)
                                .getLabResponsesByMedicalRecordId(100L);
        }

        @Test
        void getOpenLabResponses_ShouldReturnOk() throws Exception {
                Page<LabResponseSummaryDTO> page = new PageImpl<>(
                                List.of(sampleSummary()),
                                PageRequest.of(0, 10),
                                1);

                when(labResponseService.getOpenLabResponses(
                                any(Pageable.class)))
                                .thenReturn(page);

                mockMvc.perform(get("/api/lab-responses/pending")
                                .with(authentication(
                                                authenticationFor(
                                                                "lab-technician",
                                                                "LAB_TECHNICIAN")))
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].status")
                                                .value("PENDING"));

                verify(labResponseService)
                                .getOpenLabResponses(any(Pageable.class));
        }

        @Test
        void updateLabResponse_ShouldReturnOk() throws Exception {
                UpdateLabResponseRequest request = new UpdateLabResponseRequest();

                LabResponseResponseDTO response = new LabResponseResponseDTO();

                when(labResponseService.updateLabResponse(
                                eq(1L),
                                any(UpdateLabResponseRequest.class),
                                eq("lab-technician")))
                                .thenReturn(response);

                mockMvc.perform(put("/api/lab-responses/{id}", 1L)
                                .with(authentication(authenticationFor(
                                                "lab-technician",
                                                "LAB_TECHNICIAN")))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());

                verify(labResponseService).updateLabResponse(
                                eq(1L),
                                any(UpdateLabResponseRequest.class),
                                eq("lab-technician"));
        }

        @Test
        void updateLabResponseStatus_ShouldReturnOk()
                        throws Exception {

                LabResponseStatusRequest request = new LabResponseStatusRequest();

                request.setStatus(LabStatuses.COMPLETED);

                LabResponseResponseDTO response = new LabResponseResponseDTO();

                when(labResponseService.updateLabResponseStatus(
                                eq(1L),
                                any(LabResponseStatusRequest.class),
                                eq("lab-technician")))
                                .thenReturn(response);

                mockMvc.perform(patch("/api/lab-responses/{id}/status", 1L)
                                .with(authentication(authenticationFor(
                                                "lab-technician",
                                                "LAB_TECHNICIAN")))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(request)))
                                .andExpect(status().isOk());
                verify(labResponseService).updateLabResponseStatus(
                                eq(1L),
                                any(LabResponseStatusRequest.class),
                                eq("lab-technician"));
        }

        @Test
        void uploadLabResponseFile_ShouldReturnOk()
                        throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "test.pdf",
                                MediaType.APPLICATION_PDF_VALUE,
                                "test data".getBytes());

                LabResponseResponseDTO response = new LabResponseResponseDTO();

                when(labResponseService.uploadLabResponseFile(
                                eq(1L),
                                any(MultipartFile.class),
                                eq("lab-technician")))
                                .thenReturn(response);

                mockMvc.perform(multipart("/api/lab-responses/{id}/files", 1L)
                                .file(file)
                                .with(authentication(
                                                authenticationFor("lab-technician", "LAB_TECHNICIAN")))
                                .with(csrf()))
                                .andExpect(status().isOk());

                verify(labResponseService).uploadLabResponseFile(
                                eq(1L),
                                any(MultipartFile.class),
                                eq("lab-technician"));
        }

        @Test
        void getLabResponseById_ShouldReturnForbidden_WhenDoctor()
                        throws Exception {

                mockMvc.perform(get("/api/lab-responses/{id}", 1L)
                                .with(authentication(authenticationFor(
                                                "doctor",
                                                "DOCTOR"))))
                                .andExpect(status().isForbidden());
                verify(labResponseService, never()).getLabResponseDetail(1L);
        }

        @Test
        void updateLabResponse_ShouldReturnForbidden_WhenAdmin()
                        throws Exception {
                UpdateLabResponseRequest request = new UpdateLabResponseRequest();
                mockMvc.perform(put("/api/lab-responses/{id}", 1L)
                                .with(authentication(authenticationFor("admin", "ADMIN")))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(request)))
                                .andExpect(status().isForbidden());

                verify(labResponseService, never()).updateLabResponse(
                                eq(1L),
                                any(UpdateLabResponseRequest.class),
                                eq("admin"));
        }

        @Test
        void deleteLabResponseFile_ShouldReturnOk()
                        throws Exception {

                String publicId = "clinify/lab-responses/1/result";

                DeleteLabResponseFileRequest request = new DeleteLabResponseFileRequest(publicId);

                LabResponseResponseDTO response = new LabResponseResponseDTO();

                when(labResponseService.deleteLabResponseFile(
                                1L,
                                publicId,
                                "lab-technician"))
                                .thenReturn(response);

                mockMvc.perform(delete("/api/lab-responses/{id}/files", 1L)
                                .with(authentication(authenticationFor(
                                                "lab-technician",
                                                "LAB_TECHNICIAN")))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(request)))
                                .andExpect(status().isOk());

                verify(labResponseService).deleteLabResponseFile(
                                1L,
                                publicId,
                                "lab-technician");
        }

        @Test
        void deleteLabResponseFile_ShouldReturnForbidden_WhenAdmin()
                        throws Exception {
                String publicId = "clinify/lab-responses/1/result";
                DeleteLabResponseFileRequest request = new DeleteLabResponseFileRequest(publicId);
                mockMvc.perform(delete("/api/lab-responses/{id}/files", 1L)
                                .with(authentication(authenticationFor(
                                                "admin",
                                                "ADMIN")))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());

                verify(labResponseService, never()).deleteLabResponseFile(
                                1L,
                                publicId,
                                "admin");
        }
}