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


}