package app.services;

import app.models.Room;

public interface RoomService {
    Room findByCode(String code);
    Room findById(String Id);

    //code ở đaay

    Boolean isRoomCodeExists(String code);

}
