package az.clinify.demo.entity;

import az.clinify.demo.entity.BaseEntity;
import az.clinify.demo.enums.AppointmentStatus;
import az.clinify.demo.enums.AppointmentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;


//This Entity class are added for booking APppointment which will happen between doctors and patient
//It extends BaseEntity for id , createdAt , updatedAt fields
@Entity
@Table(name = "appointments")
public class Appointment extends BaseEntity {

//User can book an appointmnet for next days
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    //As we know doctors can accept , reject this appointment before this time
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentStatus status = AppointmentStatus.REQUESTED;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(columnDefinition = "TEXT")
    private String reason;

}