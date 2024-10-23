package app.services;

import app.dtos.*;
import app.models.Room;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RoomService {
    Room findByCode(String code);
    Room findById(String Id);
    boolean isRoomNameExisted(String name);
    boolean isRoomExisted(String roomId);
    Room createNewRoom(CreateRoomDTO data, HttpServletRequest request);
    RoomDTO getRoomData(String roomId);
    ResultDTO getResult(String roomId);
    void setAttendeeStatus(String jsonData);
    boolean isAttendeeListValid(List<CreateAttendeeDTO> data);
}
