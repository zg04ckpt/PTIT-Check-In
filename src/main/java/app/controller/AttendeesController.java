package app.controller;

import app.services.AttendeeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/attendees")
public class AttendeesController {
    private AttendeeService attendeeService;

    public AttendeesController(AttendeeService attendeeService) {
        this.attendeeService = attendeeService;
    }

    //endpoints
}
