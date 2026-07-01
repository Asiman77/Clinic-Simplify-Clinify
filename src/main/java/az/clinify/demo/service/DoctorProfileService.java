package az.clinify.demo.service;

import org.springframework.stereotype.Service;

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
}
