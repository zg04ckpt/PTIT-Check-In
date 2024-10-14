package app.controller;

import app.services.AttendeeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//checkInCode mau: B22DCCN001, B22DCCN002, B22DCCN003, B22DCCN004

@Controller
@RequestMapping("/attendees")
public class AttendeesController {
    private final AttendeeService attendeeService;

    public AttendeesController(AttendeeService attendeeService) {
        this.attendeeService = attendeeService;
    }

    //code ở đây
}
