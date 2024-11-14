package app.controller;

import app.dtos.attendee.AttendeeDTO;
import app.dtos.room.CreateRoomDTO;
import app.enums.RoomStatus;
import app.models.Room;
import app.services.IAttendeeService;
import app.services.IFileService;
import app.services.ILogService;
import app.services.IRoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;

// id phong mau: 6f7ffd60-959a-4953-9444-6bf0919dbe4a
// ma phong mau: HS12JK
@Controller
@RequestMapping("/rooms")
public class RoomsController {
    private final IRoomService roomService;
    private final IAttendeeService attendeeService;
    private final IFileService fileService;
    private final ILogService logService;

    @Value("${spring.base-url}")
    private String baseUrl;

    public RoomsController(IRoomService roomService, IAttendeeService attendeeService, IFileService fileService, ILogService logService) {
        this.roomService = roomService;
        this.attendeeService = attendeeService;
        this.fileService = fileService;
        this.logService = logService;
    }

    // Tạo phòng
    @GetMapping("/create-room")
    public String getCreateForm(Model model, HttpServletRequest request) {
        CreateRoomDTO data = new CreateRoomDTO();
        model.addAttribute("data", data);

        // System log
        logService.writeLog("Truy cập trang tạo phòng", null, null, request);

        return "create-room.html";
    }

    @PostMapping("/create-room")
    public String createRoom(@ModelAttribute("data") CreateRoomDTO data, Model model, HttpServletRequest request, HttpSession session) {
        String errorMessage = null;

        if(data.name.isEmpty() || roomService.isRoomNameExisted(data.name)) {
            errorMessage = "Tên phòng không hợp lệ";
        } else if(data.createBy.isEmpty()) {
            errorMessage = "Tên chủ phòng trống";
        } else if(data.attendees.isEmpty() || !roomService.isAttendeeListValid(data.attendees)) {
            errorMessage = "Danh sách người tham gia không hợp lệ";
        } else if(data.requireCheckLocation && (data.latitude == 0 || data.longitude == 0)) {
            errorMessage = "Vị trí không hợp lệ";
        } else {
            Room newRoom = roomService.createNewRoom(data, request);
            if(newRoom == null) {
                errorMessage = "Tạo phòng thất bại";
            } else {
                // System log
                logService.writeLog("Tạo phòng thành công", null, null, request);

                // Room log
                logService.writeLog("Tạo phòng thành công", newRoom.getId(), null, request);

                // Lưu session
                session.setAttribute("roomId", newRoom.getId());
                return "redirect:" + baseUrl + "/rooms/";
            }
        }

        model.addAttribute("data", data);
        model.addAttribute("message", errorMessage);

        // System log
        logService.writeLog("Tạo phòng thất bại", null, null, request);

        return "create-room.html";
    }

    // Quản lý phòng
    @GetMapping("/")
    public String getRoom(Model model, HttpSession session, HttpServletRequest request) {
        String roomId = session.getAttribute("roomId").toString();

        RoomStatus status = roomService.getStatus(roomId);
        if(status == RoomStatus.PENDING) {
            return "redirect:" + baseUrl + "/rooms/wait-open";
        } else if(status == RoomStatus.CLOSED) {
            return "redirect:" + baseUrl + "/error";
        }

        // System log
        logService.writeLog("Chủ phòng truy cập vào phòng", null, null, request);

        model.addAttribute("data", roomService.getRoomData(roomId));
        return "room.html";
    }

    @GetMapping("/wait-open")
    public String getWaitOpenPage(Model model, HttpSession session, HttpServletRequest request) {
        String roomId = session.getAttribute("roomId").toString();
        long remainingTime = roomService.getRemainingSecondsUntilRoomOpens(roomId);
        model.addAttribute("roomId", roomId);
        model.addAttribute("remaining", remainingTime);

        // System log
        logService.writeLog("Chủ phòng chờ mở phòng: " + remainingTime + "s", null, null, request);

        return "wait-open.html";
    }

    @PostMapping("/open-room")
    public String openRoom(HttpSession session) {
        String roomId = session.getAttribute("roomId").toString();
        roomService.openRoom(roomId);
        return "redirect:" + baseUrl + "/rooms/";
    }

    @GetMapping("/result")
    public String getResult(Model model, HttpSession session, HttpServletRequest request) {
        String roomId = session.getAttribute("roomId").toString();
        if(roomService.getStatus(roomId) == RoomStatus.OPENING) {
            model.addAttribute("data", roomService.getResult(roomId));

            // System log
            logService.writeLog("Chủ phòng yêu cầu kết quả điểm danh", null, null, request);

            return "result.html";
        }
        return "redirect:" + baseUrl + "/error";
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportResult(HttpSession session, HttpServletRequest request) {
        String roomId = session.getAttribute("roomId").toString();
        if(roomService.getStatus(roomId) == RoomStatus.OPENING) {
            ByteArrayInputStream stream = fileService.exportDataToExcelFile(roomId);

            // Đóng phòng
            roomService.closeRoom(roomId);
            // Xóa session
            session.removeAttribute("roomId");

            if(stream == null)  {
                return ResponseEntity.internalServerError().body(null);
            }

            // System log
            logService.writeLog("Phòng được đóng bởi chủ phòng", null, null, request);

            // Room log
            logService.writeLog("Xuất file kết quả, đóng phòng điểm danh", roomId, null, request);

            // Tải xuống log ? trước khi xóa
            logService.removeAllLogOfRoom(roomId);

            // Trả về luồng file
            return ResponseEntity.ok()
                    .body(new InputStreamResource(stream));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/attendees-info")
    public ResponseEntity<List<AttendeeDTO>> getAdditionalInfos(HttpSession session) {
        String roomId = session.getAttribute("roomId").toString();
        List<AttendeeDTO> data = roomService.getRoomData(roomId).attendees;
        return ResponseEntity.ok().body(data);
    }

//    @GetMapping("/get-ggmap-url")
//    public String getGGMapUrl(@RequestParam("attendeeId") String attendeeId, HttpSession session) {
//        String roomId = session.getAttribute("roomId").toString();
//        if(attendeeService.checkIfAttendeeIsInRoom(attendeeId, roomId)) {
//            return "redirect:" + roomService.getGGMapUrl(roomId, attendeeId);
//        }
//        return "redirect:" + baseUrl + "/error";
//    }

    @MessageMapping("/setAttendeeStatus")
    public void setAttendeeStatus(String data, SimpMessageHeaderAccessor headerAccessor) {
        HttpSession session = (HttpSession) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("HTTP_SESSION");
        roomService.setAttendeeStatus(data, session, (String) headerAccessor.getSessionAttributes().get("ip"));
    }
}
