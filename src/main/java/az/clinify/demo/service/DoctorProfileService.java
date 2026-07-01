package az.clinify.demo.service;

import org.springframework.stereotype.Service;

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

    private DoctorProfile findDoctorProfileById(Long id) {
        return doctorProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
    }

    private Department findDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

}
