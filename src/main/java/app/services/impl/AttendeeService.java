package app.services.impl;

import app.dtos.attendee.AttendeeStatusDTO;
import app.dtos.attendee.CheckInDTO;
import app.dtos.system.MessageDTO;
import app.dtos.attendee.WaitingRoomDTO;
import app.enums.CheckInStatus;
import app.enums.MessageType;
import app.models.Attendee;
import app.models.Room;
import app.repositories.IAttendeeRepository;
import app.repositories.IRoomRepository;
import app.services.IAttendeeService;
import app.services.ILogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AttendeeService implements IAttendeeService {
    private final IAttendeeRepository attendeeRepository;
    private final IRoomRepository roomRepository;
    private final SimpMessagingTemplate sender;
    private final ILogService logService;

    @Autowired
    public AttendeeService(IAttendeeRepository attendeeRepository, IRoomRepository roomRepository, SimpMessagingTemplate sender, ILogService logService) {
        this.attendeeRepository = attendeeRepository;
        this.roomRepository = roomRepository;
        this.sender = sender;
        this.logService = logService;
    }

    @Override
    public Attendee getByCheckInCodeAndRoomId(String checkInCode, String roomId) {
        return attendeeRepository.findByCheckInCodeAndRoomId(checkInCode, roomId);
    }

    @Override
    public boolean checkIfAttendeeIsInRoom(String attendeeId, String roomId) {
        return attendeeRepository.existsByIdAndRoomId(attendeeId, roomId);
    }

    @Override
    public WaitingRoomDTO getWaitingData(String roomId, String attendeeId) {
        Room room = roomRepository.getReferenceById(roomId);
        Attendee attendee = attendeeRepository.getReferenceById(attendeeId);

        WaitingRoomDTO data = new WaitingRoomDTO();
        data.roomId = roomId;
        data.attendeeId = attendeeId;
        data.attendeeName = attendee.getName();
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
        // Thiết đặt thông tin checkin cho attendee
        attendee.setLatitude(data.latitude);
        attendee.setLongitude(data.longitude);
        attendee.setCheckInStatus(CheckInStatus.WAIT_APPROVAL);
        attendee.setAttendOn(LocalDateTime.now());
        attendee.setUserAgent(request.getHeader("User-Agent"));
        attendeeRepository.saveAndFlush(attendee);
    }

    @Override
    @Transactional
    public void setAttendeeStatus(String attendeeId, String roomId, String ip, CheckInStatus status) {
        // Cập nhật trạng thái của ngươời tham gia
        Attendee attendee = attendeeRepository.getReferenceById(attendeeId);
        attendee.setCheckInStatus(status);
        attendee.setIp(ip);
        attendeeRepository.saveAndFlush(attendee);

        // Ghi log
        if(status == CheckInStatus.WAIT_APPROVAL) {
            logService.writeLog(attendee.getName() + " đã vào phòng.", roomId, attendeeId, ip);
        } else if (status == CheckInStatus.OUT_OF_ROOM) {
            logService.writeLog(attendee.getName() + " đã thoát phòng.", roomId, attendeeId, ip);
        }

        // Gửi message đến phòng
        MessageDTO<AttendeeStatusDTO> message = new MessageDTO<>();
        message.roomId = roomId;
        message.type = MessageType.ATTENDEE_STATUS;
        message.data = new AttendeeStatusDTO();
        message.data.attendeeId = attendeeId;
        message.data.attendeeStatus = status;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(message);
            sender.convertAndSend("/topic/rooms/" + roomId, jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi gửi message (setAttendeeStatus)", e);
        }
    }
}
