package app.controller;

import app.services.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// id phong mau: 6f7ffd60-959a-4953-9444-6bf0919dbe4a
// ma phong mau: HS12JK
@Controller
@RequestMapping("/rooms")
public class RoomsController {
    private RoomService roomService;

    public RoomsController(RoomService roomService) {
        this.roomService = roomService;
    }

    //code ở đây
}
