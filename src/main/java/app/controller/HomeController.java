package app.controller;

import app.dtos.CreateTestDTO;
import app.dtos.SearchRoomDTO;
import app.enums.RoomStatus;
import app.models.Room;
import app.services.AttendeeService;
import app.services.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final RoomService roomService;

    public HomeController( RoomService roomService) {
        this.roomService = roomService;
    }

    //code ở đây
    @GetMapping("/")
    public String getHome( Model model) {
        SearchRoomDTO data = new SearchRoomDTO();
        model.addAttribute("data", data);
        return "index.html";
    }

    @PostMapping("/")
    public String findRoom(@ModelAttribute("data") SearchRoomDTO data,  Model model) {

        // Kiểm tra độ dài
        if(data.roomCode.length() != 6) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Mã phòng phải có 6 kí tự!");
            return "index.html";
        }

        //kiểm tra phòng hợp lệ
        Room room = roomService.findByCode(data.roomCode);
        if (room == null) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Phòng không tồn tại!");
            return "index.html";
        }
        else if(room.getStatus() == RoomStatus.PENDING) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Phòng điểm danh chưa mở!");
            return "index.html";
        }
        else if(room.getStatus() == RoomStatus.CLOSED) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Phòng điểm danh đã đóng!");
            return "index.html";
        }


        /*Chuyển hướng đến join-room(AttendeesController)*/
        return "redirect:/attendees/join-room?"+"roomId="+room.getId();
    }
}
