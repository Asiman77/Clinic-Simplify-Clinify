package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.controller.LabResponseController;
import az.clinify.demo.dto.request.LabResponseStatusRequest;
import az.clinify.demo.dto.request.UpdateLabResponseRequest;
import az.clinify.demo.dto.response.LabResponseResponseDTO;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.LabResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LabResponseController.class)
@Import({
        SecurityConfig.class
})
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

    @Test
    @WithMockUser
    void getLabResponseById_ShouldReturnOk() throws Exception {
        LabResponseResponseDTO response = new LabResponseResponseDTO(); // DTO daxilində sahələr varsa set edə bilərsən örn: response.setId(1L);
        when(labResponseService.getLabResponseById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/lab-responses/{id}", 1L))
                .andExpect(status().isOk());

        verify(labResponseService).getLabResponseById(1L);
    }

    @Test
    @WithMockUser
    void getLabResponsesByMedicalRecordId_ShouldReturnOk() throws Exception {
        LabResponseResponseDTO response = new LabResponseResponseDTO();
        when(labResponseService.getLabResponsesByMedicalRecordId(100L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/lab-responses/medical-record/{medicalRecordId}", 100L))
                .andExpect(status().isOk());

        verify(labResponseService).getLabResponsesByMedicalRecordId(100L);
    }
    @Test
    @WithMockUser
    void getPendingLabResponses_ShouldReturnOk() throws Exception {
        LabResponseResponseDTO response = new LabResponseResponseDTO();
        when(labResponseService.getPendingLabResponses()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/lab-responses/pending"))
                .andExpect(status().isOk());

        verify(labResponseService).getPendingLabResponses();
    }

    @Test
    @WithMockUser
    void updateLabResponse_ShouldReturnOk() throws Exception {
        UpdateLabResponseRequest request = new UpdateLabResponseRequest(); // request daxilində @Valid üçün lazımi sahələri doldur
        LabResponseResponseDTO response = new LabResponseResponseDTO();

        when(labResponseService.updateLabResponse(eq(1L), any(UpdateLabResponseRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/lab-responses/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(labResponseService).updateLabResponse(eq(1L), any(UpdateLabResponseRequest.class));
    }

    @Test
    @WithMockUser
    void updateLabResponseStatus_ShouldReturnOk() throws Exception {
        LabResponseStatusRequest request = new LabResponseStatusRequest();
        request.setStatus(az.clinify.demo.enums.LabStatuses.COMPLETED); // Səndə hansı enum-lar varsa birini yaz (örn: PENDING, APPROVED və s.)

        LabResponseResponseDTO response = new LabResponseResponseDTO();

        when(labResponseService.updateLabResponseStatus(eq(1L), any(LabResponseStatusRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/lab-responses/{id}/status", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(labResponseService).updateLabResponseStatus(eq(1L), any(LabResponseStatusRequest.class));
    }
    @Test
    @WithMockUser
    void uploadLabResponseFile_ShouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "test data".getBytes()
        );
        LabResponseResponseDTO response = new LabResponseResponseDTO();

        when(labResponseService.uploadLabResponseFile(eq(1L), any(MultipartFile.class))).thenReturn(response);

        mockMvc.perform(multipart("/api/lab-responses/{id}/files", 1L)
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(labResponseService).uploadLabResponseFile(eq(1L), any(MultipartFile.class));
    }


}