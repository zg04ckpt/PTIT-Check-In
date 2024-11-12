package app.interceptors;

import app.services.ILogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    private final ILogService logService;
    @Value("${spring.base-url}")
    private String baseUrl;

    public RequestInterceptor(ILogService logService) {
        this.logService = logService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(
            request.getRequestURI().contains("/js/") ||
            request.getRequestURI().contains("/css/") ||
            request.getRequestURI().contains("/images/") ||
            request.getRequestURI().contains("/get-ggmap-url") ||
            request.getRequestURI().contains("/logs/")
        ) {
            return true;
        }

        logService.writeLog("Truy cập trang web", null, null, request);

        HttpSession session = request.getSession(false);
        String currentUri = request.getRequestURI();
        if(session != null) {
            // Nếu có roomId => đang là chủ phòng
            if(session.getAttribute("roomId") != null) {
                if(
                    !currentUri.endsWith("/rooms/") &&
                    !currentUri.endsWith("/rooms/result") &&
                    !currentUri.endsWith("/rooms/export") &&
                    !currentUri.endsWith("/rooms/open-room") &&
                    !currentUri.endsWith("/rooms/wait-open") &&
                    !currentUri.endsWith("/rooms/attendees-info")
                ) {
                    response.sendRedirect(baseUrl + "/rooms/");
                    return false;
                }
            }

            // Nếu có cả attendeeId và joinedRoomId thì đang là người tham gia
            if(session.getAttribute("attendeeId") != null && session.getAttribute("joinedRoomId") != null) {
                if(
                    !currentUri.endsWith("/attendees/waiting") &&
                    !currentUri.endsWith("/attendees/clear-session")
                ) {
                    response.sendRedirect(baseUrl + "/attendees/waiting");
                    return false;
                }
            }
        }
        return true;
    }
}
