package app.controller;

import app.dtos.CreateRoomDTO;
import app.dtos.RoomDTO;
import app.enums.RoomStatus;
import app.models.Room;
import app.services.FileService;
import app.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Objects;

// id phong mau: 6f7ffd60-959a-4953-9444-6bf0919dbe4a
// ma phong mau: HS12JK
@Controller
@RequestMapping("/rooms")
public class RoomsController {
    private final RoomService roomService;
    private final FileService fileService;

    public RoomsController(RoomService roomService, FileService fileService) {
        this.roomService = roomService;
        this.fileService = fileService;
    }

    // Tạo phòng
    @GetMapping("/create-room")
    public String getCreateForm(Model model) {
        CreateRoomDTO data = new CreateRoomDTO();
        model.addAttribute("data", data);
        return "create-room.html";
    }

    @PostMapping("/create-room")
    public String createRoom(@ModelAttribute("data") CreateRoomDTO data, Model model, HttpServletRequest request, HttpSession session) {
        if(data.name.isEmpty()) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Tên phòng trống");
            return "create-room.html";
        }

        if(data.createBy.isEmpty()) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Tên chủ phòng trống");
            return "create-room.html";
        }

        if(data.attendees.isEmpty()) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Danh sách người tham gia trống");
            return "create-room.html";
        }

        if(roomService.isRoomNameExisted(data.name)) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Tên phòng bị trùng");
            return "create-room.html";
        }

        if(!roomService.isAttendeeListValid(data.attendees)) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Danh sách người tham gia không hợp lệ");
            return "create-room.html";
        }

        Room newRoom = roomService.createNewRoom(data, request);
        if(newRoom == null) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Tạo phòng thất bại, vui lòng thử lại!");
            return "create-room.html";
        }

        // Lưu session
        session.setAttribute("roomId", newRoom.getId());

        return "redirect:/rooms/" + newRoom.getId();
    }

    // Quản lý phòng
    @GetMapping("/{roomId}")
    public String getRoom(@PathVariable("roomId") String roomId,  Model model) {
        if(roomService.getStatus(roomId) == RoomStatus.PENDING) {
            return "redirect:/rooms/" + roomId + "/wait-open";
        }

        model.addAttribute("data", roomService.getRoomData(roomId));
        return "room.html";
    }

    @GetMapping("/{roomId}/wait-open")
    public String getWaitOpenPage(@PathVariable("roomId") String roomId,  Model model) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("remaining", roomService.getRemainingSecondsUntilRoomOpens(roomId));
        return "wait-open.html";
    }

    @PostMapping("/open-room")
    public String openRoom(@RequestBody String roomId) {
        roomService.openRoom(roomId);
        return "redirect:/rooms/" + roomId;
    }

    @GetMapping("/result")
    public String getResult(@RequestParam("roomId") String roomId, Model model, HttpSession session) {
        // Xóa session
        session.removeAttribute("roomId");

        //Gửi thông báo đóng phòng đến các client
        roomService.sendCloseRoomMessage(roomId);
        model.addAttribute("data", roomService.getResult(roomId));
        return "result.html";
    }

    @GetMapping("/export/{roomId}")
    public ResponseEntity<InputStreamResource> exportResult(@PathVariable String roomId) {
        ByteArrayInputStream stream = fileService.exportDataToExcelFile(roomId);
        if(stream == null)  {
            return ResponseEntity.internalServerError().body(null);
        }
        return ResponseEntity.ok()
                .body(new InputStreamResource(stream));
    }

    @MessageMapping("/setAttendeeStatus")
    public void setAttendeeStatus(String data, SimpMessageHeaderAccessor headerAccessor) {
        HttpSession session = (HttpSession) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("HTTP_SESSION");
        roomService.setAttendeeStatus(data, session);
    }
}
