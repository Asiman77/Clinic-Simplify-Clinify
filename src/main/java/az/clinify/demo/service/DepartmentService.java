package az.clinify.demo.service;

import az.clinify.demo.dto.request.CreateDepartmentRequest;
import az.clinify.demo.dto.request.UpdateDepartmentRequest;
import az.clinify.demo.dto.response.DepartmentResponse;
import az.clinify.demo.entity.Department;
import az.clinify.demo.exceptions.DepartmentAlreadyExistsException;
import az.clinify.demo.exceptions.DepartmentNotFoundException;
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

    @Transactional
    public DepartmentResponse updateDepartment(Long id , UpdateDepartmentRequest request){
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));

        String departmentName = request.getName().trim();

        if(departmentRepository.existsByNameIgnoreCaseAndIdNot(departmentName , id)){
            throw new DepartmentAlreadyExistsException(departmentName);
        }
        department.setName(departmentName);
        department.setDescription(request.getDescription());

        if(request.getActive() != null ){
            department.setActive(request.getActive());
        }

        Department updatedDepartment = departmentRepository.save(department);

        return departmentMapper.toResponse(updatedDepartment);


    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));

        department.setActive(false);

        departmentRepository.save(department);
    }

}
