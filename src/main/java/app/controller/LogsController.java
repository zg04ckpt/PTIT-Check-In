package app.controller;

import app.models.Log;
import app.services.ILogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/logs")
public class LogsController {
    private final ILogService _logService;

    public LogsController(ILogService logService) {
        _logService = logService;
    }

    @GetMapping("/room")
    public ResponseEntity<List<Log>> getLogsOfRoom(HttpSession session) {
        if(session.getAttribute("roomId") == null) {
            return ResponseEntity.badRequest().body(null);
        }
        String roomId = session.getAttribute("roomId").toString();
        return ResponseEntity.ok(_logService.getLogsOfRoom(roomId));
    }

    @GetMapping("/attendee")
    public ResponseEntity<List<Log>> getLogsOfAttendee(HttpSession session) {
        if(session.getAttribute("attendeeId") == null) {
            return ResponseEntity.badRequest().body(null);
        }
        String attendeeId = session.getAttribute("attendeeId").toString();
        return ResponseEntity.ok(_logService.getLogsOfAttendee(attendeeId));
    }
}
