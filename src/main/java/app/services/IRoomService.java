package app.services;

import app.dtos.attendee.CreateAttendeeDTO;
import app.dtos.management.RoomStatusDTO;
import app.dtos.room.CreateRoomDTO;
import app.dtos.room.RoomDTO;
import app.dtos.system.ResultDTO;
import app.enums.RoomStatus;
import app.models.Room;
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
