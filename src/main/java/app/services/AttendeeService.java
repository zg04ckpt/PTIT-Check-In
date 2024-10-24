package app.services;

import app.dtos.CheckInDTO;
import app.dtos.WaitingRoomDTO;
import app.models.Attendee;

public interface AttendeeService {
    Attendee getByCheckInCodeAndRoomId(String checkInCode, String roomId);
    WaitingRoomDTO getWaitingData(String roomId, String attendeeId);
    CheckInDTO getCheckInData(String roomId);
    void checkIn(Attendee attendee, CheckInDTO data);
}
