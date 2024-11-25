package web.service.impl;

import web.dto.system.LogDTO;
import web.model.Attendee;
import web.model.Log;
import web.repository.ILogRepository;
import web.repository.IRoomRepository;
import web.service.ILogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
    public List<LogDTO> getAllLogs() {
        List<Log> logs = _logRepository.findAll();
        logs.sort(Comparator.comparing(Log::getTime).reversed());
        List<LogDTO> data = new ArrayList<>();
        logs.forEach(e -> {
            if(e.getRoomId() == null && e.getAttendeeId() == null) {
                LogDTO dto = new LogDTO();
                dto.ip = e.getIp();
                dto.time = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy").format(e.getTime());
                dto.description = e.getDescription();
                data.add(dto);
            }
        });
        return data;
    }

    @Override
    public void writeLog(String desc, String roomId, String attendeeId, String ip) {
        _logRepository.save(new Log(ip, desc, attendeeId, roomId));
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
    public void removeAllLogs() {
        List<Log> logs = _logRepository.findAll()
                .stream().filter(e -> e.getRoomId() == null && e.getAttendeeId() == null).toList();
        _logRepository.deleteAll(logs);
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
        logs.sort(Comparator.comparing(Log::getTime).reversed());
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
        logs.sort(Comparator.comparing(Log::getTime).reversed());
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
