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

        if(session != null) {
            // Nếu có roomId => đang là chủ phòng
            if(session.getAttribute("roomId") != null) {
                String roomId = session.getAttribute("roomId").toString();
                String currentUri = request.getRequestURI();
                if(currentUri.contains("/rooms/" + roomId) || currentUri.contains("result") || currentUri.contains("open-room")) {
                    return true;
                }
                response.sendRedirect("/rooms/" + roomId);
                return false;
            }

            // Nếu có cả attendeeId và joinedRoomId thì đang là người tham gia
            if(session.getAttribute("attendeeId") != null && session.getAttribute("joinedRoomId") != null) {
                String attendeeId = session.getAttribute("attendeeId").toString();
                String joinedRoomId = session.getAttribute("joinedRoomId").toString();
                String redirectUrl = "/attendees/waiting?roomId=" + joinedRoomId + "&attendeeId=" + attendeeId;
                if(!request.getRequestURI().contains("/waiting")) {
                    response.sendRedirect(redirectUrl);
                    return false;
                }
            }
        }
        return true;
    }
}
