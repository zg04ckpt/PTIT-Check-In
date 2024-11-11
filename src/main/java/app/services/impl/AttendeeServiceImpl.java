package app.services.impl;

import app.dtos.attendee.AttendeeStatusDTO;
import app.dtos.attendee.CheckInDTO;
import app.dtos.system.MessageDTO;
import app.dtos.attendee.WaitingRoomDTO;
import app.enums.CheckInStatus;
import app.enums.MessageType;
import app.models.Attendee;
import app.models.Room;
import app.repositories.AttendeeRepository;
import app.repositories.RoomRepository;
import app.services.AttendeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AttendeeServiceImpl implements AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate sender;

    @Autowired
    public AttendeeServiceImpl(AttendeeRepository attendeeRepository, RoomRepository roomRepository, SimpMessagingTemplate sender) {
        this.attendeeRepository = attendeeRepository;
        this.roomRepository = roomRepository;
        this.sender = sender;
    }

    @Override
    public Attendee getByCheckInCodeAndRoomId(String checkInCode, String roomId) {
        return attendeeRepository.findByCheckInCodeAndRoomId(checkInCode, roomId);
    }

    @Override
    public WaitingRoomDTO getWaitingData(String roomId, String attendeeId) {
        Room room = roomRepository.getReferenceById(roomId);
        Attendee attendee = attendeeRepository.getReferenceById(attendeeId);

        WaitingRoomDTO data = new WaitingRoomDTO();
        data.roomId = roomId;
        data.attendeeId = attendeeId;
        data.roomName = room.getName();
        data.roomCode = room.getCode();
        data.end = room.getEndTime();
        data.checkInCode = attendee.getCheckInCode();
        data.roomOwner = room.getCreateBy();

        return data;
    }

    @Override
    public CheckInDTO getCheckInData(String roomId) {
        Room room = roomRepository.getReferenceById(roomId);
        CheckInDTO data = new CheckInDTO();
        data.roomId = room.getId();
        data.roomName = room.getName();
        data.roomCode = room.getCode();
        data.requireCheckLocation = room.isRequireCheckLocation();

        return data;
    }


    @Override
    public void checkIn(Attendee attendee, CheckInDTO data, HttpServletRequest request) {
        // thiết đặt trạng thái cho attendee
        String userAgent = request.getHeader("User-Agent");
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        attendee.setLatitude(data.latitude);
        attendee.setLongitude(data.longitude);
        attendee.setCheckInStatus(CheckInStatus.WAIT_APPROVAL);
        attendee.setAttendOn(LocalDateTime.now());
        attendee.setIp(clientIp);
        attendee.setUserAgent(userAgent);
        attendeeRepository.save(attendee);

        // gửi thông báo attendee đã tham gia tới phòng
        MessageDTO<AttendeeStatusDTO> message = new MessageDTO<>();
        message.type = MessageType.ATTENDEE_STATUS;
        message.roomId = data.roomId;
        message.data = new AttendeeStatusDTO();
        message.data.attendeeId = attendee.getId();
        message.data.attendeeStatus = CheckInStatus.WAIT_APPROVAL;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(message);
            sender.convertAndSend("/topic/rooms/" + data.roomId, jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi chuyển đối object -> json (checkIn)", e);
        }
    }
}
