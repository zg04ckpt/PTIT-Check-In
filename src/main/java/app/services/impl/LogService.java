package app.services.impl;

import app.dtos.system.LogDTO;
import app.models.Attendee;
import app.models.Log;
import app.repositories.ILogRepository;
import app.repositories.IRoomRepository;
import app.services.ILogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogService implements ILogService {
    private final ILogRepository _logRepository;
    private final IRoomRepository _roomRepository;

    @Autowired
    public LogService(ILogRepository logRepository, IRoomRepository roomRepository) {
        _logRepository = logRepository;
        _roomRepository = roomRepository;
    }

    @Override
    public void writeLog(String desc, String roomId, String attendeeId, HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        _logRepository.save(new Log(clientIp, desc, attendeeId, roomId));
    }

    @Override
    public void removeAllLogOfAttendee(String attendeeId) {
        _logRepository.deleteByAttendeeId(attendeeId);
    }

    @Override
    public void removeAllLogOfRoom(String roomId) {
        List<Attendee> attendees = _roomRepository.getReferenceById(roomId).getAttendees();
        for(Attendee e : attendees) {
            _logRepository.deleteByAttendeeId(e.getId());
        }
        _logRepository.deleteByRoomId(roomId);
    }

    @Override
    public List<LogDTO> getLogsOfRoom(String roomId) {
        List<Log> logs = _logRepository.findByRoomId(roomId);
        List<LogDTO> data = new ArrayList<>();
        logs.forEach(e -> {
           LogDTO dto = new LogDTO();
           dto.ip = e.getIp();
           dto.time = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy").format(e.getTime());
           dto.description = e.getDescription();
           data.add(dto);
        });
        return data;
    }

    @Override
    public List<LogDTO> getLogsOfAttendee(String attendeeId) {
        List<Log> logs = _logRepository.findByAttendeeId(attendeeId);
        List<LogDTO> data = new ArrayList<>();
        logs.forEach(e -> {
            LogDTO dto = new LogDTO();
            dto.ip = e.getIp();
            dto.time = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy").format(e.getTime());
            dto.description = e.getDescription();
            data.add(dto);
        });
        return data;
    }
}
