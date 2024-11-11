package app.services;

import app.models.Log;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ILogService {
    void writeLog(String desc, HttpServletRequest request);
    List<Log> getLogsOfRoom(String roomId);
    List<Log> getLogsOfAttendee(String attendeeId);
}
