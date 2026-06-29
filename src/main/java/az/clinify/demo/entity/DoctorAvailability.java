package az.clinify.demo.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

import az.clinify.demo.enums.AvailabilityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// Bu entity indi bizə bunu verir:

// Doctor hansı gün işləyir?
// Hansı saat aralığında işləyir?
// Neçə dəqiqəlik qəbullar edir?
// Bu aralıq online üçündür, walk-in üçündür, yoxsa ikisi üçün?
// Schedule aktivdir ya yox?
@Entity
@Table(name = "doctor_availabilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailability extends BaseEntity {


    // Bu schedule hansı həkimə aiddir?
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    // Həftənin hansı günü? MONDAY, TUESDAY və s.
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    // Həkimin həmin gün qəbul etməyə başladığı saat
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // Həkimin həmin gün qəbul etməyi bitirdiyi saat
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Bir qəbulun standart müddəti
    @Column(name = "slot_duration_minutes", nullable = false)
    private Integer slotDurationMinutes = 30;

    // ONLINE_ONLY, WALK_IN_ONLY, MIXED
    @Enumerated(EnumType.STRING)
    @Column(name = "availability_type", nullable = false, length = 30)
    private AvailabilityType availabilityType = AvailabilityType.MIXED;

    // Bu schedule aktivdir ya yox
    @Column(nullable = false)
    private Boolean active = true;

}
