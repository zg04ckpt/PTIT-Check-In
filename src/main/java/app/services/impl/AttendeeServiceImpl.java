package app.services.impl;

import app.models.Attendee;
import app.repositories.AttendeeRepository;
import app.repositories.RoomRepository;
import app.services.AttendeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendeeServiceImpl implements AttendeeService {
    private final AttendeeRepository attendeeRepository;

    @Autowired
    public AttendeeServiceImpl(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }
    public boolean existsByRoomId(String roomId){
        try {
            return attendeeRepository.existsByRoomId(roomId);
        } catch (Exception exception) {
            return false;
        }

    }
    public boolean existsByCheckInCode(String checkInCode){

        try {
            return attendeeRepository.existsByCheckInCode(checkInCode);
        } catch (Exception exception) {
            return false;
        }
    }
}
