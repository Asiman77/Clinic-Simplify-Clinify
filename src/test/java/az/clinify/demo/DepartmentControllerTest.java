package az.clinify.demo;

import az.clinify.demo.configs.SecurityConfig;
import az.clinify.demo.controller.DepartmentController;
import az.clinify.demo.dto.request.CreateDepartmentRequest;
import az.clinify.demo.dto.request.UpdateDepartmentRequest;
import az.clinify.demo.dto.response.DepartmentResponse;
import az.clinify.demo.security.JwtAuthenticationFilter;
import az.clinify.demo.security.JwtTokenProvider;
import az.clinify.demo.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
@Import({
        SecurityConfig.class
})
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DepartmentService departmentService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    @WithMockUser
    void getAllDepartments_ShouldReturnOk() throws Exception {
        DepartmentResponse response = new DepartmentResponse(1L, "Cardiology", "Heart-related treatments",true);
        when(departmentService.getAllDepartments()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Cardiology"));

        verify(departmentService).getAllDepartments();
    }

    @Test
    @WithMockUser
    void getDepartmentById_ShouldReturnOk() throws Exception {
        DepartmentResponse response = new DepartmentResponse(1L, "Cardiology", "Heart-related treatments",true);
        when(departmentService.getDepartmentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/departments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Cardiology"));

        verify(departmentService).getDepartmentById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDepartment_ShouldReturnCreated_WhenUserIsAdmin() throws Exception {
        CreateDepartmentRequest request = new CreateDepartmentRequest("Cardiology", "Heart-related treatments",true);
        DepartmentResponse response = new DepartmentResponse(1L, "Cardiology", "Heart-related treatments",true);

        when(departmentService.createDepartment(any(CreateDepartmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/departments")
                        .with(csrf())
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        verify(departmentService).createDepartment(any(CreateDepartmentRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createDepartment_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        CreateDepartmentRequest request = new CreateDepartmentRequest("Cardiology", "Heart-related treatments",true);

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(departmentService, never()).createDepartment(any());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDepartment_ShouldReturnOk_WhenUserIsAdmin() throws Exception {
        UpdateDepartmentRequest request = new UpdateDepartmentRequest("Neurology", "Brain-related treatments",true);
        DepartmentResponse response = new DepartmentResponse(1L, "Neurology", "Brain-related treatments",true);

        when(departmentService.updateDepartment(eq(1L), any(UpdateDepartmentRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/departments/{id}", 1L)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Neurology"));

        verify(departmentService).updateDepartment(eq(1L), any(UpdateDepartmentRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateDepartment_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        UpdateDepartmentRequest request = new UpdateDepartmentRequest("Neurology", "Brain-related treatments",true);

        mockMvc.perform(put("/api/departments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(departmentService, never()).updateDepartment(any(), any());
    }

    @Test
    void deleteDepartment_ShouldReturnNoContent_WhenUserIsAdmin() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/{id}", 1L)
                        .with(csrf())
                        .with(user("username").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(departmentService).deleteDepartment(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteDepartment_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/departments/{id}", 1L))
                .andExpect(status().isForbidden());

        verify(departmentService, never()).deleteDepartment(any());
    }
}