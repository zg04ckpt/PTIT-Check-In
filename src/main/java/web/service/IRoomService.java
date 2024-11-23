package web.service;

import web.dto.attendee.CreateAttendeeDTO;
import web.dto.management.RoomStatusDTO;
import web.dto.room.CreateRoomDTO;
import web.dto.room.RoomDTO;
import web.dto.room.ResultDTO;
import web.model.enums.RoomStatus;
import web.model.Room;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface IRoomService {
    Room findByCode(String code);
    Room findById(String Id);
    boolean isRoomNameExisted(String name);
    boolean isRoomExisted(String roomId);
    Room createNewRoom(CreateRoomDTO data, HttpServletRequest request);
    RoomDTO getRoomData(String roomId);
    ResultDTO getResult(String roomId);
    void setAttendeeStatus(String jsonData, HttpSession session, String ip);
    boolean isAttendeeListValid(List<CreateAttendeeDTO> data);
    void closeRoom(String roomId);
    RoomStatus getStatus(String roomId);
    long getRemainingSecondsUntilRoomOpens(String roomId);
    void openRoom(String roomId);
//    String getGGMapUrl(String roomId, String attendeeId);

    // --------- Manage Thịnh + Hưng ----------------
    List<RoomStatusDTO> listRoomStatusDTO();
    void deleteById(String id);
    Room findByID(String id);
}
