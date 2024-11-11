package app.controller;

import app.dtos.attendee.CheckInDTO;
import app.enums.CheckInStatus;
import app.models.Attendee;
import app.services.IAttendeeService;
import app.services.IRoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//checkInCode mau: B22DCCN001, B22DCCN002, B22DCCN003, B22DCCN004

// Bat su kien thoat / chuyen tab: sự kien đóng kết nối websocket, visibilitychange(window event)


@Controller
@RequestMapping("/attendees")
public class AttendeesController {
    private final IAttendeeService attendeeService;

    @Value("${spring.base-url}")
    private String baseUrl;

    public AttendeesController(IAttendeeService attendeeService) {
        this.attendeeService = attendeeService;
    }

    @GetMapping("/join-room")
    public String getJoinRoomForm(@RequestParam String roomId, Model model) {
        //trả về trang check-in.html kèm theo biến DTO
        model.addAttribute("data", attendeeService.getCheckInData(roomId));
        return "check-in.html";
    }

    @PostMapping("/join-room")
    public String joinRoom(@ModelAttribute("data") CheckInDTO data, Model model, HttpSession session, HttpServletRequest request) {

        Attendee attendee = attendeeService.getByCheckInCodeAndRoomId(data.checkInCode, data.roomId);
        // Kiểm tra attendee có trong danh sách điểm danh hay ko
        String errorMessage = null;
        if(attendee == null) {
            errorMessage = "Mã điểm danh không hợp lệ";
        } else if(attendee.getCheckInStatus() != CheckInStatus.OUT_OF_ROOM) {
            errorMessage = "Mã này đã được điểm danh bởi: " + attendee.getName();
        } else if(data.requireCheckLocation && (data.latitude == 0 || data.longitude == 0)) {
            errorMessage = "Vui lòng lấy vị trí";
        }

        if(errorMessage != null) {
            model.addAttribute("data", data);
            model.addAttribute("message", errorMessage);
            return "check-in.html";
        }

        // Điểm danh
        attendeeService.checkIn(attendee, data, request);

        // Lưu session
        session.setAttribute("attendeeId", attendee.getId());
        session.setAttribute("joinedRoomId", data.roomId);

        return "redirect:" + baseUrl + "/attendees/waiting";
    }

    @GetMapping("/waiting")
    public String getWaitingRoom(Model model, HttpSession session){
        String attendeeId = session.getAttribute("attendeeId").toString();
        String roomId = session.getAttribute("joinedRoomId").toString();
        model.addAttribute("data", attendeeService.getWaitingData(roomId, attendeeId));
        return "waiting-room.html";
    }

    @GetMapping("/clear-session")
    public String clearSession(HttpSession session) {
        session.removeAttribute("attendeeId");
        session.removeAttribute("joinedRoomId");
        return "redirect:" + baseUrl;
    }
}
