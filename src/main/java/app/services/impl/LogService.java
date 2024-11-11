package app.services.impl;

import app.models.Log;
import app.repositories.LogRepository;
import app.services.ILogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService implements ILogService {
    private final LogRepository _logRepository;

    @Autowired
    public LogService(LogRepository logRepository) {
        _logRepository = logRepository;
    }

    @Override
    public void writeLog(String desc, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        HttpSession session = request.getSession(false);
        String attendeeId = null;
        if(session.getAttribute("attendeeId") != null) {
            attendeeId = session.getAttribute("attendeeId").toString();
        }
        String roomId = null;
        if(session.getAttribute("roomId") != null) {
            roomId = session.getAttribute("roomId").toString();
        }
        _logRepository.save(new Log(ip, desc, attendeeId, roomId));
    }

    @Override
    public List<Log> getLogsOfRoom(String roomId) {
        return _logRepository.findByRoomId(roomId);
    }

    @Override
    public List<Log> getLogsOfAttendee(String attendeeId) {
        return _logRepository.findByAttendeeId(attendeeId);
    }
}
