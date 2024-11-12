package app.controller;

import app.dtos.attendee.CheckInDTO;
import app.dtos.attendee.WaitingRoomDTO;
import app.enums.CheckInStatus;
import app.models.Attendee;
import app.services.IAttendeeService;
import app.services.ILogService;
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
    private final ILogService logService;

    @Value("${spring.base-url}")
    private String baseUrl;

    public AttendeesController(IAttendeeService attendeeService, ILogService logService) {
        this.attendeeService = attendeeService;
        this.logService = logService;
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
        } else if(data.requireCheckLocation && (data.latitude == 0 && data.longitude == 0)) {
            errorMessage = "Vui lòng lấy vị trí";
        }

        if(errorMessage != null) {
            //log
            logService.writeLog("Yêu cầu điểm danh thất bại với mã điểm danh: " + data.checkInCode +
                    ". Lỗi [" + errorMessage + "]", data.roomId, null,  request);

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
    public String getWaitingRoom(Model model, HttpSession session, HttpServletRequest request){
        String attendeeId = session.getAttribute("attendeeId").toString();
        String roomId = session.getAttribute("joinedRoomId").toString();
        WaitingRoomDTO data = attendeeService.getWaitingData(roomId, attendeeId);
        model.addAttribute("data", data);

        logService.writeLog("Vào phòng chờ điểm danh.", null, attendeeId, request);
        logService.writeLog(data.attendeeName + " đã vào phòng chờ.", roomId, null, request);

        return "waiting-room.html";
    }

    @GetMapping("/clear-session")
    public String clearSession(HttpSession session , HttpServletRequest request) {

        WaitingRoomDTO data = attendeeService.getWaitingData(
                session.getAttribute("joinedRoomId").toString(),
                session.getAttribute("attendeeId").toString()
        );

        // Ghi log
        logService.writeLog("Thoát phòng điểm danh", null, data.attendeeId, request);
        logService.writeLog(data.attendeeName + " rời phòng điểm danh.", data.roomId, null, request);

        session.removeAttribute("attendeeId");
        session.removeAttribute("joinedRoomId");

        return "redirect:" + baseUrl;
    }
}
