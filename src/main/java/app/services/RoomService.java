package app.services;

import app.dtos.*;
import app.enums.RoomStatus;
import app.models.Room;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface RoomService {
    Room findByCode(String code);
    Room findById(String Id);
    boolean isRoomNameExisted(String name);
    boolean isRoomExisted(String roomId);
    Room createNewRoom(CreateRoomDTO data, HttpServletRequest request);
    RoomDTO getRoomData(String roomId);
    ResultDTO getResult(String roomId);
    void setAttendeeStatus(String jsonData, HttpSession session);
    boolean isAttendeeListValid(List<CreateAttendeeDTO> data);
    void closeRoom(String roomId);
    RoomStatus getStatus(String roomId);
    long getRemainingSecondsUntilRoomOpens(String roomId);
    void openRoom(String roomId);
}
