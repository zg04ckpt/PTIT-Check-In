package web.config;

import web.model.enums.CheckInStatus;
import web.service.IAttendeeService;
import web.service.ILogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Objects;

@Component
public class WebSocketListener {
    @Autowired
    private IAttendeeService attendeeService;
    @Autowired
    private SimpMessagingTemplate sender;
    @Autowired
    private ILogService logService;

    @EventListener
    public void handleConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // Lấy các thông tin từ header
        boolean isRoomOwner = Boolean.parseBoolean(headerAccessor.getFirstNativeHeader("isRoomOwner"));
        Map<String, Object> sessionAttr = Objects.requireNonNull(headerAccessor.getSessionAttributes());
        if(!isRoomOwner) {
            String attendeeId = headerAccessor.getFirstNativeHeader("attendeeId");
            String roomId = headerAccessor.getFirstNativeHeader("roomId");
            String ip = headerAccessor.getFirstNativeHeader("ip");

            // Kiểm tra kết nối socket không hợp lệ
            if(attendeeId != null && roomId != null && ip != null && attendeeService.checkIfAttendeeIsInRoom(attendeeId, roomId)) {
                // Gửi event trạng thái cho phòng
                attendeeService.setAttendeeStatus(attendeeId, roomId, ip, CheckInStatus.WAIT_APPROVAL);

                // Lưu thông tin người tham gia vào session

                sessionAttr.put("isRoomOwner", false);
                sessionAttr.put("attendeeId", attendeeId);
                sessionAttr.put("roomId", roomId);
                sessionAttr.put("ip", ip);
            }
        } else {
            // do some thing when room open
            String ip = headerAccessor.getFirstNativeHeader("ip");
            sessionAttr.put("ip", ip);
        }
    }

    @EventListener
    public void handleDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // Lấy thông tin trong phien lam viec
        Map<String, Object> sessionAttr = Objects.requireNonNull(headerAccessor.getSessionAttributes());
        // Xử lý đóng kết nối cho attendee
        if(sessionAttr.get("isRoomOwner") != null && !(boolean)sessionAttr.get("isRoomOwner")) {
            String attendeeId = (String) headerAccessor.getSessionAttributes().get("attendeeId");
            String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
            String ip = (String) headerAccessor.getSessionAttributes().get("ip");
            attendeeService.setAttendeeStatus(attendeeId, roomId, ip, CheckInStatus.OUT_OF_ROOM);
            // System log
            logService.writeLog("User mất kết nối", null, null, ip);
        }
    }
}
