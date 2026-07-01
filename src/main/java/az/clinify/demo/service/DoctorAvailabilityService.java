package az.clinify.demo.service;

import az.clinify.demo.dto.request.CreateDoctorAvailabilityRequest;
import az.clinify.demo.dto.response.DoctorAvailabilityResponse;
import az.clinify.demo.entity.DoctorAvailability;
import az.clinify.demo.entity.DoctorProfile;
import az.clinify.demo.exceptions.DoctorNotAvailableException;
import az.clinify.demo.exceptions.DoctorNotFoundException;
import az.clinify.demo.mapper.DoctorAvailabilityMapper;
import az.clinify.demo.mapper.DoctorProfileMapper;
import az.clinify.demo.repository.DoctorAvailabilityRepository;
import az.clinify.demo.repository.DoctorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorAvailabilityMapper doctorAvailabilityMapper;
    private final DoctorProfileRepository doctorRepository;

    public DoctorAvailabilityResponse createAvailability(CreateDoctorAvailabilityRequest request){
        DoctorProfile doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found"));
        DoctorAvailability availability = new DoctorAvailability();

        availability.setDoctor(doctor);
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setSlotDurationMinutes(request.getSlotDurationMinutes());
        availability.setAvailabilityType(request.getAvailabilityType());
        availability.setActive(request.getActive());

        availabilityRepository.save(availability);

        return doctorAvailabilityMapper.toResponse(availability);
    }

    public DoctorAvailabilityResponse getDoctorAvailabilityById(Long id) {
        DoctorAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new DoctorNotAvailableException("Availability not found"));
        return doctorAvailabilityMapper.toResponse(availability);
    }

    public List<DoctorAvailabilityResponse> getAllDoctorAvailabilities() {
        return availabilityRepository.findAll()
                .stream()
                .map(doctorAvailabilityMapper::toResponse)
                .toList();
    }

    public List<DoctorAvailabilityResponse> getDoctorAvailabilitiesByDoctorId(Long doctorId) {
        return availabilityRepository.findByDoctorId(doctorId)
                .stream()
                .map(doctorAvailabilityMapper::toResponse)
                .toList();
    }
    public void deleteDoctorAvailability(Long id) {
        DoctorAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new DoctorNotAvailableException("Availability not found"));
        availabilityRepository.delete(availability);
    }



}
