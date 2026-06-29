package az.clinify.demo.enums;

public enum AppointmentStatus {

    REQUESTED,
    APPROVED, // Həkim qəbul edib
    REJECTED, // Həkim qəbul etməyib
//    CHECKED_IN, // Xəstə xəstəxanaya gəlib
    COMPLETED, // Qəbul bitib
    CANCELLED, // Ləğv olunub
    NO_SHOW // Xəstə gəlməyib
}