package web.exception;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import web.service.ILogService;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private ILogService logService;
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, HttpSession session) {

        // Xóa phiên làm việc khi có ngoại lệ
        session.removeAttribute("attendeeId");
        session.removeAttribute("roomId");
        session.removeAttribute("joinedRoomId");

        // log lỗi
        logService.writeLog("ERROR: " + ex.getMessage(), null, null, "--");

        return "redirect:/error";
    }
}
