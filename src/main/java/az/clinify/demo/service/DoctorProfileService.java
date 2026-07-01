package az.clinify.demo.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.clinify.demo.dto.request.CreateDoctorProfileRequest;
import az.clinify.demo.dto.response.DoctorProfileResponse;
import az.clinify.demo.entity.Department;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.entity.Role;
import az.clinify.demo.entity.User;
import az.clinify.demo.enums.RoleType;
import az.clinify.demo.exceptions.BaseConflictException;
import az.clinify.demo.exceptions.BaseNotFoundException;
import az.clinify.demo.exceptions.DepartmentNotFoundException;
import az.clinify.demo.exceptions.DoctorNotFoundException;
import az.clinify.demo.exceptions.UserNotFoundException;
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

    @Transactional(readOnly = true)
    public DoctorProfileResponse getDoctorById(Long id) {
        DoctorProfile doctorProfile = findDoctorProfileById(id);
        return doctorProfileMapper.toResponse(doctorProfile);
    }

    @Transactional
    public DoctorProfileResponse createDoctor(CreateDoctorProfileRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));

        Department department = findDepartmentById(request.getDepartmentId());

        if (doctorProfileRepository.existsByUserId(user.getId())) {
            throw new BaseConflictException("Doctor profile already exists for user id: " + user.getId());
        }

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        boolean hasDoctorRole = user.getRoles()
                .stream()
                .anyMatch(role -> role.getName() == RoleType.DOCTOR);

        if (!hasDoctorRole) {
            Role doctorRole = roleRepository.findByName(RoleType.DOCTOR)
                    .orElseThrow(() -> new BaseNotFoundException("DOCTOR role not found"));
            user.getRoles().add(doctorRole);
            userRepository.save(user);
        }

        DoctorProfile doctorProfile = doctorProfileMapper.toEntity(request, user, department);
        DoctorProfile savedDoctorProfile = doctorProfileRepository.save(doctorProfile);
        return doctorProfileMapper.toResponse(savedDoctorProfile);
    }

    private DoctorProfile findDoctorProfileById(Long id) {
        return doctorProfileRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor profile not found with id: " + id));
    }

    private Department findDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));
    }

}
