package app.controller;

import app.services.AttendeeService;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {
    private final AttendeeService attendeeService;

    public HomeController(AttendeeService attendeeService) {
        this.attendeeService = attendeeService;
    }

    //code ở đây
}
