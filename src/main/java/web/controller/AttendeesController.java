package web.controller;

import web.dto.attendee.CheckInDTO;
import web.dto.attendee.WaitingRoomDTO;
import web.model.enums.CheckInStatus;
import web.model.enums.RoomStatus;
import web.model.Attendee;
import web.service.IAttendeeService;
import web.service.ILogService;
import web.service.IRoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//checkInCode mau: B22DCCN001, B22DCCN002, B22DCCN003, B22DCCN004

// Bat su kien thoat / chuyen tab: sự kien đóng kết nối websocket, visibilitychange(window event)


@Controller
@RequestMapping("/attendees")
public class AttendeesController {
    private final IAttendeeService attendeeService;
    private final IRoomService roomService;
    private final ILogService logService;

    @Value("${spring.base-url}")
    private String baseUrl;

    public AttendeesController(IAttendeeService attendeeService, IRoomService roomService, ILogService logService) {
        this.attendeeService = attendeeService;
        this.roomService = roomService;
        this.logService = logService;
    }

    @GetMapping("/join-room")
    public String getJoinRoomForm(@RequestParam String roomId, Model model, HttpServletRequest request) {
        if(roomService.getStatus(roomId) == RoomStatus.CLOSED) {
            return "redirect:" + baseUrl + "/error";
        }
        //trả về trang check-in.html kèm theo DTO
        model.addAttribute("data", attendeeService.getCheckInData(roomId));
        // System log
        logService.writeLog("Truy cập trang check-in", null, null, request);
        return "check-in.html";
    }

    @PostMapping("/join-room")
    public String joinRoom(@ModelAttribute("data") CheckInDTO data, Model model, HttpSession session, HttpServletRequest request) {
        // Kiểm tra nếu phòng đã đóng
        if(roomService.getStatus(data.roomId) == RoomStatus.CLOSED) {
            return "redirect:" + baseUrl + "/error";
        }

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
                    " - Lỗi [" + errorMessage + "]", data.roomId, null,  request);
            logService.writeLog("Check-in thất bại", null, null, request);

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

        // Kiểm tra nếu phòng đã đóng
        if(roomService.getStatus(roomId) == RoomStatus.CLOSED) {
            return "redirect:" + baseUrl + "/attendees/clear-session";
        }

        WaitingRoomDTO data = attendeeService.getWaitingData(roomId, attendeeId);

        // System log
        logService.writeLog("Vào phòng chờ", null, null, request);

        model.addAttribute("data", data);
        return "waiting-room.html";
    }

    @GetMapping("/clear-session")
    public String clearSession(HttpSession session , HttpServletRequest request) {

        WaitingRoomDTO data = attendeeService.getWaitingData(
                session.getAttribute("joinedRoomId").toString(),
                session.getAttribute("attendeeId").toString()
        );

        session.removeAttribute("attendeeId");
        session.removeAttribute("joinedRoomId");

        return "redirect:" + baseUrl;
    }

    @GetMapping("/get-ip")
    public ResponseEntity<String> getIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return ResponseEntity.ok().body(clientIp);
    }
}
