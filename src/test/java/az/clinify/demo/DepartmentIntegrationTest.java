package az.clinify.demo;

import az.clinify.demo.entity.Department;
import az.clinify.demo.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DepartmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartmentRepository departmentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllDepartments_returnsDataFromDatabase_withoutAuthentication() throws Exception {
        Department department = saveDepartment("Cardiology", "Heart care", true);

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(department.getId()))
                .andExpect(jsonPath("$[0].name").value("Cardiology"))
                .andExpect(jsonPath("$[0].description").value("Heart care"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void createDepartment_persistsTrimmedName_andReturnsCreated() throws Exception {
        Map<String, Object> request = Map.of(
                "name", "  Neurology  ",
                "description", "Brain and nervous system",
                "active", true);

        mockMvc.perform(post("/api/departments")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Neurology"))
                .andExpect(jsonPath("$.active").value(true));

        assertThat(departmentRepository.findAll())
                .singleElement()
                .satisfies(saved -> {
                    assertThat(saved.getName()).isEqualTo("Neurology");
                    assertThat(saved.getDescription()).isEqualTo("Brain and nervous system");
                });
    }

    @Test
    void updateThenDeleteDepartment_changesPersistedState() throws Exception {
        Department department = saveDepartment("Neurology", "Old description", true);

        mockMvc.perform(put("/api/departments/{id}", department.getId())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Neuroscience",
                                "description", "Updated description",
                                "active", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Neuroscience"))
                .andExpect(jsonPath("$.description").value("Updated description"));

        mockMvc.perform(delete("/api/departments/{id}", department.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        Department persisted = departmentRepository.findById(department.getId()).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("Neuroscience");
        assertThat(persisted.getActive()).isFalse();
    }

    private Department saveDepartment(String name, String description, boolean active) {
        Department department = new Department();
        department.setName(name);
        department.setDescription(description);
        department.setActive(active);
        return departmentRepository.saveAndFlush(department);
    }
}
