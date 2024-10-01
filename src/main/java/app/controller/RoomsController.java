package app.controller;

import app.services.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rooms")
public class RoomsController {
    private RoomService roomService;

    public RoomsController(RoomService roomService) {
        this.roomService = roomService;
    }

    //endpoints
}
