package web.service;

import web.dto.system.LogDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ILogService {
    List<LogDTO> getAllLogs();
    void writeLog(String desc, String roomId, String attendeeId, String ip);
    void writeLog(String desc, String roomId, String attendeeId, HttpServletRequest request);
    void removeAllLogs();
    void removeAllLogOfRoom(String roomId);
    List<LogDTO> getLogsOfRoom(String roomId);
    List<LogDTO> getLogsOfAttendee(String attendeeId);

}
