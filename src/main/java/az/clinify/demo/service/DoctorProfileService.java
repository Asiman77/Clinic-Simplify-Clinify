package az.clinify.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.clinify.demo.dto.response.DoctorProfileResponse;
import az.clinify.demo.entity.Department;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.Role;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.RoleType;
import az.clinify.demo.mapper.DoctorProfileMapper;
import az.clinify.demo.repository.DepartmentRepository;
import az.clinify.demo.repository.DoctorProfileRepository;
import az.clinify.demo.repository.RoleRepository;
import az.clinify.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DoctorProfileService {
    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final DoctorProfileMapper doctorProfileMapper;

    @Transactional(readOnly = true)
    public List<DoctorProfileResponse> getAllDoctors() {
        return doctorProfileRepository.findByActiveTrue()
                .stream()
                .map(doctorProfileMapper::toResponse)
                .toList();
    }

    

    private DoctorProfile findDoctorProfileById(Long id) {
        return doctorProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
    }

    private Department findDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }



}
