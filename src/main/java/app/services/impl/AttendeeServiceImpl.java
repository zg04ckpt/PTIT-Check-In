package app.services.impl;

import app.repositories.AttendeeRepository;
import app.repositories.RoomRepository;
import app.services.AttendeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendeeServiceImpl implements AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public AttendeeServiceImpl(AttendeeRepository attendeeRepository, RoomRepository roomRepository) {
        this.attendeeRepository = attendeeRepository;
        this.roomRepository = roomRepository;
    }
}
