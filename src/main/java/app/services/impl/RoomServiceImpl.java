package app.services.impl;

import app.models.Room;
import app.repositories.AttendeeRepository;
import app.repositories.RoomRepository;
import app.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    //code ở đây
    @Override
    public Room findByCode(String code) {
        try {
             Room byName = roomRepository.findByCode(code);
             return byName;
        } catch (Exception exception) {
            return null;
        }

    }

    @Override
    public Room findById(String Id) {
        try {
           return roomRepository.getReferenceById(Id);
        } catch (Exception exception) {
            return null;
        }

    }

    @Override
    public Boolean isRoomCodeExists(String code) {
        try {
            return roomRepository.existsByCode(code);
        } catch (Exception exception) {
            return false;
        }
    }

}
