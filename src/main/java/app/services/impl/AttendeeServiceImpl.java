package app.services.impl;

import app.repositories.AttendeeRepository;
import app.services.AttendeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendeeServiceImpl implements AttendeeService {
    private AttendeeRepository attendeeRepository;

    @Autowired
    public AttendeeServiceImpl(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }
}
