package app.services;

import app.dtos.attendee.CheckInDTO;
import app.dtos.attendee.WaitingRoomDTO;
import app.models.Attendee;
import jakarta.servlet.http.HttpServletRequest;

public interface IAttendeeService {
    Attendee getByCheckInCodeAndRoomId(String checkInCode, String roomId);
    WaitingRoomDTO getWaitingData(String roomId, String attendeeId);
    CheckInDTO getCheckInData(String roomId);
    void checkIn(Attendee attendee, CheckInDTO data, HttpServletRequest request);
}
