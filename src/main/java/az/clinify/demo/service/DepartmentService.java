package az.clinify.demo.service;

import az.clinify.demo.dto.request.CreateDepartmentRequest;
import az.clinify.demo.dto.response.DepartmentResponse;
import az.clinify.demo.entity.Department;
import az.clinify.demo.exceptions.DepartmentAlreadyExistsException;
import az.clinify.demo.mapper.DepartmentMapper;
import az.clinify.demo.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service

public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        String departmentName = request.getName().trim();

        if (departmentRepository.existsByNameIgnoreCase(departmentName)) {
            throw new DepartmentAlreadyExistsException(departmentName);
        }

        Department department = new Department();
        department.setName(departmentName);
        department.setDescription(request.getDescription());
        department.setActive(request.getActive() != null ? request.getActive() : true);

        Department savedDepartment = departmentRepository.save(department);

        return departmentMapper.toResponse(savedDepartment);
    }

}
