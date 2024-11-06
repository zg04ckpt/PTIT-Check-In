package app.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getRequestURI().contains("/js/") || request.getRequestURI().contains("/css/")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        String currentUri = request.getRequestURI();
        if(session != null) {
            // Nếu có roomId => đang là chủ phòng
            if(session.getAttribute("roomId") != null) {
                if(
                    !currentUri.endsWith("/rooms/") &&
                    !currentUri.endsWith("/rooms/result") &&
                    !currentUri.endsWith("/rooms/open-room") &&
                    !currentUri.endsWith("/rooms/wait-open")
                ) {
                    response.sendRedirect("/rooms/");
                    return false;
                }
            }

            // Nếu có cả attendeeId và joinedRoomId thì đang là người tham gia
            if(session.getAttribute("attendeeId") != null && session.getAttribute("joinedRoomId") != null) {
                if(
                    !currentUri.endsWith("/attendees/waiting") &&
                    !currentUri.endsWith("/attendees/clear-session")
                ) {
                    response.sendRedirect("/attendees/waiting");
                    return false;
                }
            }
        }
        return true;
    }
}
