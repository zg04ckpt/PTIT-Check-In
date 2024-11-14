package app.controller;

import app.dtos.system.LogDTO;
import app.models.Log;
import app.services.IAttendeeService;
import app.services.ILogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/logs")
public class LogsController {
    private final ILogService logService;
    private final IAttendeeService attendeeService;

    public LogsController(ILogService logService, IAttendeeService attendeeService) {
        this.logService = logService;
        this.attendeeService = attendeeService;
    }

    @GetMapping("/room")
    public ResponseEntity<List<LogDTO>> getLogsOfRoom(HttpSession session) {
        // Nếu ko là chủ phòng thì không được xem log
        if(session.getAttribute("roomId") == null) {
            return ResponseEntity.badRequest().body(null);
        }
        String roomId = session.getAttribute("roomId").toString();
        return ResponseEntity.ok(logService.getLogsOfRoom(roomId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<LogDTO>> getAll(HttpSession session) {
        // Nếu ko là chủ phòng thì không được xem log
        if(session.getAttribute("isAdmin") == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(logService.getAllLogs());
    }

    @GetMapping("/attendees/{id}")
    public ResponseEntity<List<LogDTO>> getLogsOfAttendee(@PathVariable String id, HttpSession session) {
        // Nếu ko là chủ phòng thì không được xem log của người tham gia
        if(session.getAttribute("roomId") == null) {
            return ResponseEntity.badRequest().body(null);
        }
        String roomId = session.getAttribute("roomId").toString();
        //Kiểm tra nếu người tham gia là thành viên phòng thì mới được xem log
        if(attendeeService.checkIfAttendeeIsInRoom(id, roomId)) {
            return ResponseEntity.ok(logService.getLogsOfAttendee(id));
        }
        return ResponseEntity.badRequest().body(null);
    }
}
