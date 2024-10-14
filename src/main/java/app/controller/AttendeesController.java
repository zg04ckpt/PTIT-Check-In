package app.controller;

import app.dtos.CheckInDTO;
import app.dtos.RoomInfoDTO;
import app.dtos.WaitingRoomDTO;
import app.models.Attendee;
import app.models.Room;
import app.repositories.AttendeeRepository;
import app.repositories.RoomRepository;
import app.services.AttendeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//checkInCode mau: B22DCCN001, B22DCCN002, B22DCCN003, B22DCCN004

@Controller
@RequestMapping("/attendees")
public class AttendeesController {
    private AttendeeService attendeeService;
    private RoomRepository roomRepository;
    private final AttendeeRepository attendeeRepository;

    public AttendeesController(AttendeeService attendeeService, RoomRepository roomRepository,
                               AttendeeRepository attendeeRepository) {
        this.attendeeService = attendeeService;
        this.roomRepository = roomRepository;
        this.attendeeRepository = attendeeRepository;
    }

    //endpoints
    @GetMapping("/join-room")
    public String getJoinRoomForm(@RequestParam("roomId") String roomId, Model model) {
        // tìm room theo roomId để lay cau hinh phong
        Room room = roomRepository.getReferenceById(roomId);
        CheckInDTO data = new CheckInDTO();
        data.requireCheckLocation = false; // tùy theo cấu hình phòng
        data.roomId = roomId;
        data.roomName = room.getName();
        data.roomCode = room.getCode();
        model.addAttribute("data", data);
        return "check-in.html";
    }

    @PostMapping("/join-room")
    public String joinRoom(@ModelAttribute("data") CheckInDTO data , Model model) {

//        model.addAttribute("message", "Mã phòng không hợp lệ");

        Attendee attendee = attendeeRepository.findByRoomIdAndCheckInCode(data.roomId, data.checkInCode);
        if(attendee == null) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Mã điểm danh không hợp lệ");
            return "check-in.html";
        }

        return "redirect:/attendees/waiting?roomId=" + data.roomId + "&checkInCode=" + data.checkInCode;
    }

    @GetMapping("/waiting")
    public String getWaitingRoom(@RequestParam("roomId") String roomId, @RequestParam("checkInCode") String checkInCode,  Model model) {
        Room room = roomRepository.getReferenceById(roomId);

        //lay thong tin phong
        WaitingRoomDTO data = new WaitingRoomDTO();
        data.roomName = room.getName();
        data.roomCode = room.getCode();
        data.roomOwner = room.getCreateBy();
        data.checkInCode = checkInCode;
        model.addAttribute("data", data);
        return "waiting-room.html";
    }
}
