package app.services.impl;

import app.repositories.AttendeeRepository;
import app.repositories.RoomRepository;
import app.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final AttendeeRepository attendeeRepository;
    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, AttendeeRepository attendeeRepository) {
        this.roomRepository = roomRepository;
        this.attendeeRepository = attendeeRepository;
    }

    //code ở đây
}
