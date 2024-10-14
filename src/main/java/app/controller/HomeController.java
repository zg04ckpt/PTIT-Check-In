package app.controller;

import app.dtos.SearchRoomDTO;
import app.models.Room;
import app.repositories.RoomRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParsePosition;

@Controller
public class HomeController {
    private RoomRepository roomRepository;

    public HomeController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping("")
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("data", new SearchRoomDTO());
//        model.addAttribute("message", request.getScheme() + "//:" + request.getServerName());
        return "index.html";
    }

    @PostMapping("")
    public String findRoom(@ModelAttribute("data") SearchRoomDTO data, Model model) {
        if(data.roomCode.length() != 6) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Mã phải gồm 6 chữ số");
            return "index.html";
        }

        Room room = roomRepository.findByCode(data.roomCode);
        if(room == null) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Không tìm thấy phòng");
            return "index.html";
        }

        return "redirect:/attendees/join-room?roomId=" + room.getId();
    }
}
