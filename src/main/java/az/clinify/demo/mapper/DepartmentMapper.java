package az.clinify.demo.mapper;

import az.clinify.demo.dto.request.CreateDepartmentRequest;
import az.clinify.demo.dto.response.DepartmentResponse;
import az.clinify.demo.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse(Department department) {

        if (department == null) {
            return null;
        }

        DepartmentResponse response = new DepartmentResponse();

        response.setId(department.getId());
        response.setName(department.getName());
        response.setDescription(department.getDescription());
        response.setActive(department.getActive());

        return response;
    }

    public Department toEntity(CreateDepartmentRequest request) {
        if (request == null) {
            return null;
        }

        Department department = new Department();

        department.setName(request.getName().trim());
        department.setDescription(request.getDescription());
        department.setActive(true);

        return department;
    }
}