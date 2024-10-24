package app.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hibernate.query.sqm.mutation.internal.Handler;
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
            if(session.getAttribute("roomId") != null) {
                String roomId = session.getAttribute("roomId").toString();
                if(request.getRequestURI().contains("/rooms/" + roomId) || request.getRequestURI().contains("result")) {
                    return true;
                }
                response.sendRedirect("/rooms/" + roomId);
                return false;
            }

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
