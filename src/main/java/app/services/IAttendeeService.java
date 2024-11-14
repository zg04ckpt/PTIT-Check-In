package app.services;

import app.dtos.attendee.CheckInDTO;
import app.dtos.attendee.WaitingRoomDTO;
import app.enums.CheckInStatus;
import app.models.Attendee;
import jakarta.servlet.http.HttpServletRequest;

public interface IAttendeeService {
    Attendee getByCheckInCodeAndRoomId(String checkInCode, String roomId);
    boolean checkIfAttendeeIsInRoom(String attendeeId, String roomId);
    WaitingRoomDTO getWaitingData(String roomId, String attendeeId);
    CheckInDTO getCheckInData(String roomId);
    void checkIn(Attendee attendee, CheckInDTO data, HttpServletRequest request);

    void setAttendeeStatus(String attendeeId, String roomId, String ip, CheckInStatus status);
}
