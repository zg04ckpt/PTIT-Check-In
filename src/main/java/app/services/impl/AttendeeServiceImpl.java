package app.services.impl;

import app.dtos.CheckInDTO;
import app.enums.CheckInStatus;
import app.models.Attendee;
import app.repositories.AttendeeRepository;
import app.repositories.RoomRepository;
import app.services.AttendeeService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AttendeeServiceImpl implements AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final SimpMessagingTemplate sender;

    @Autowired
    public AttendeeServiceImpl(AttendeeRepository attendeeRepository, SimpMessagingTemplate sender) {
        this.attendeeRepository = attendeeRepository;
        this.sender = sender;
    }

    @Override
    public Attendee getByCheckInCodeAndRoomId(String checkInCode, String roomId) {
        return attendeeRepository.findByCheckInCodeAndRoomId(checkInCode, roomId);
    }

    @Override
    public Attendee getById(String id) {
        return attendeeRepository.getReferenceById(id);
    }

    @Override
    public void checkIn(Attendee attendee, CheckInDTO data) {
        // thiết đặt trạng thái cho attendee
        attendee.setLatitude(data.latitude);
        attendee.setLongitude(data.longitude);
        attendee.setCheckInStatus(CheckInStatus.WAIT_APPROVAL);
        attendeeRepository.save(attendee);

        // gửi thông báo attendee đã tham gia tới phòng
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", attendee.getId());
        jsonObject.addProperty("status", CheckInStatus.WAIT_APPROVAL.ordinal());
        sender.convertAndSend("/topic/rooms/" + data.roomId, jsonObject.toString());
    }
}
