package app.exceptions;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, HttpSession session) {

        // Xóa phiên làm việc khi có ngoại lệ
        session.removeAttribute("attendeeId");
        session.removeAttribute("roomId");
        session.removeAttribute("joinedRoomId");

        return "redirect:/error";
    }
}
