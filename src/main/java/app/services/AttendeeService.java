package app.services;

import app.models.Attendee;

public interface AttendeeService {
    boolean existsByRoomId(String roomId);
    boolean existsByCheckInCode(String checkInCode);
}
