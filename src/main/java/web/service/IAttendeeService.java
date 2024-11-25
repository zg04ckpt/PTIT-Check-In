package web.service;

import web.dto.attendee.CheckInDTO;
import web.dto.attendee.WaitingRoomDTO;
import web.model.enums.CheckInStatus;
import web.model.Attendee;
import jakarta.servlet.http.HttpServletRequest;

public interface IAttendeeService {
    Attendee getByCheckInCodeAndRoomId(String checkInCode, String roomId);
    boolean checkIfAttendeeIsInRoom(String attendeeId, String roomId);
    WaitingRoomDTO getWaitingData(String roomId, String attendeeId);
    CheckInDTO getCheckInData(String roomId);
    void checkIn(Attendee attendee, CheckInDTO data, HttpServletRequest request);

    void setAttendeeStatus(String attendeeId, String roomId, String ip, CheckInStatus status);
}
