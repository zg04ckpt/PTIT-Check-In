package app.services;

import app.dtos.system.LogDTO;
import app.models.Log;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ILogService {
    void writeLog(String desc, String roomId, String attendeeId, HttpServletRequest request);
    void removeAllLogOfAttendee(String attendeeId);
    void removeAllLogOfRoom(String roomId);
    List<LogDTO> getLogsOfRoom(String roomId);
    List<LogDTO> getLogsOfAttendee(String attendeeId);
}
