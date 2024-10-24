package app.controller;

import app.dtos.CheckInDTO;
import app.dtos.WaitingRoomDTO;
import app.enums.CheckInStatus;
import app.models.Attendee;
import app.models.Room;
import app.services.AttendeeService;
import app.services.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//checkInCode mau: B22DCCN001, B22DCCN002, B22DCCN003, B22DCCN004

@Controller
@RequestMapping("/attendees")
public class AttendeesController {
    private final AttendeeService attendeeService;
    private final RoomService roomService;

    public AttendeesController(AttendeeService attendeeService, RoomService roomService) {
        this.attendeeService = attendeeService;
        this.roomService = roomService;
    }

    @GetMapping("/join-room")
    public String getJoinRoomForm(@RequestParam String roomId, Model model) {
        //trả về trang check-in.html kèm theo biến DTO
        model.addAttribute("data", attendeeService.getCheckInData(roomId));
        return "check-in.html";
    }

    @PostMapping("/join-room")
    public String joinRoom(@ModelAttribute("data") CheckInDTO data, Model model) {

        Attendee attendee = attendeeService.getByCheckInCodeAndRoomId(data.checkInCode, data.roomId);
        // Kiểm tra attendee có trong danh sách điểm danh hay ko
        if(attendee == null) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Mã điểm danh không hợp lệ");
            return "check-in.html";
        }

        // Kiểm tra attendee đã được điểm danh hay chưa
        if(attendee.getCheckInStatus() != CheckInStatus.OUT_OF_ROOM) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Mã này đã được điểm danh bởi: " + attendee.getName());
            return "check-in.html";
        }

        // Điểm danh
        attendeeService.checkIn(attendee, data);

        return "redirect:/attendees/waiting?roomId=" + data.roomId + "&attendeeId=" + attendee.getId();
    }

    @GetMapping("/waiting")
    public String getWaitingRoom(@RequestParam String roomId, @RequestParam String attendeeId, Model model){
        model.addAttribute("data", attendeeService.getWaitingData(roomId, attendeeId));
        return "waiting-room.html";
    }
}
