package app.controller;

import app.dtos.attendee.SearchRoomDTO;
import app.enums.RoomStatus;
import app.models.Room;
import app.services.ILogService;
import app.services.IRoomService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final IRoomService roomService;
    private final ILogService logService;

    @Value("${spring.base-url}")
    private String baseUrl;

    public HomeController(IRoomService roomService, ILogService logService) {
        this.roomService = roomService;
        this.logService = logService;
    }

    //code ở đây
    @GetMapping("/")
    public String getHome(Model model, HttpServletRequest request) {
        logService.writeLog("Truy cập trang chủ", request);
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
        return "redirect:" + baseUrl + "/attendees/join-room?" + "roomId=" + room.getId();
    }
}
