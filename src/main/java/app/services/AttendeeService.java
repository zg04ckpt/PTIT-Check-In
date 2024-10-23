package app.services;

import app.dtos.CheckInDTO;
import app.models.Attendee;

public interface AttendeeService {
    Attendee getByCheckInCodeAndRoomId(String checkInCode, String roomId);
    Attendee getById(String id);
    void checkIn(Attendee attendee, CheckInDTO data);
}
