package app.services.impl;

import app.dtos.*;
import app.enums.CheckInStatus;
import app.enums.MessageType;
import app.models.Attendee;
import app.models.Room;
import app.repositories.AttendeeRepository;
import app.repositories.RoomRepository;
import app.services.RoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final AttendeeRepository attendeeRepository;
    private final SimpMessagingTemplate sender;
    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, AttendeeRepository attendeeRepository, SimpMessagingTemplate sender) {
        this.roomRepository = roomRepository;
        this.attendeeRepository = attendeeRepository;
        this.sender = sender;
    }

    //code ở đây
    @Override
    public Room findByCode(String code) {
        return roomRepository.findByCode(code);
    }

    @Override
    public Room findById(String Id) {
        return roomRepository.getReferenceById(Id);
    }

    @Override
    public boolean isRoomNameExisted(String name) {
        return roomRepository.existsByName(name);
    }

    @Override
    public boolean isRoomExisted(String roomId) {
        return roomRepository.existsById(roomId);
    }

    @Override
    public Room createNewRoom(CreateRoomDTO data, HttpServletRequest request) {
        Room room = new Room(
                UUID.randomUUID().toString(),
                data.name,
                data.createBy,
                data.requireCheckLocation,
                data.latitude,
                data.longitude,
                data.range,
                data.startTime,
                data.endTime,
                data.enableAutoApproval,
                LocalDateTime.now(),
                false
        );

        // Tạo link phòng
        room.setUrl("https://" + request.getServerName() + "/attendees/join-room?roomId=" + room.getId());

        // Tạo mã phòng
        room.generateRandomCode();
        while (roomRepository.existsByCode(room.getCode())) {
            room.generateRandomCode();
        }
        Room newRoom = roomRepository.save(room);

        // Lưu danh sách người tham gia
        ArrayList<Attendee> attendees = new ArrayList<>();
        for(CreateAttendeeDTO e : data.attendees) {
            Attendee newAtt = new Attendee(
                    UUID.randomUUID().toString(),
                    e.checkInCode,
                    e.name
            );
            newAtt.setCheckInStatus(CheckInStatus.OUT_OF_ROOM);
            newAtt.setRoom(room);
            attendees.add(newAtt);
        }
        attendeeRepository.saveAll(attendees);

        return newRoom;
    }

    @Override
    public RoomDTO getRoomData(String roomId) {
        Room room = roomRepository.getReferenceById(roomId);

        RoomDTO data = new RoomDTO();
        data.id = room.getId();
        data.name = room.getName();
        data.createBy = room.getCreateBy();
        data.requireCheckLocation = room.isRequireCheckLocation();
        data.latitude = room.getLatitude();
        data.longitude = room.getLongitude();
        data.acceptRange = room.getAcceptRange();
        data.startTime = room.getStartTime();
        data.endTime = room.getEndTime();
        data.enableAutoApproval = room.isEnableAutoApproval();
        data.createOn = room.getCreateOn();
        data.code = room.getCode();
        data.url = room.getUrl();
        List<Attendee> attendees = room.getAttendees();
        ArrayList<AttendeeDTO> attendeeDTOs = new ArrayList<>();
        attendees.forEach(e -> {
            AttendeeDTO attendeeDTO = new AttendeeDTO();
            attendeeDTO.id = e.getId();
            attendeeDTO.name = e.getName();
            attendeeDTO.checkInCode = e.getCheckInCode();
            attendeeDTO.checkInStatus = e.getCheckInStatus().ordinal();
            attendeeDTOs.add(attendeeDTO);
        });
        data.attendees = attendeeDTOs;

        return data;
    }

    @Override
    public ResultDTO getResult(String roomId) {
        int success = 0, reject = 0, wait = 0;
        Room room = roomRepository.getReferenceById(roomId);
        for(var e : room.getAttendees()) {
            switch (e.getCheckInStatus()) {
                case ACCEPTED -> success++;
                case REJECTED -> reject++;
                case WAIT_APPROVAL -> wait++;
            }
        }
        ResultDTO data = new ResultDTO();
        data.roomId = room.getId();
        data.roomName = room.getName();
        data.accepted = success;
        data.rejected = reject;
        data.pending = wait;
        data.outOfRoom = room.getAttendees().size() - success - reject - wait;

        return data;
    }

    @Override
    public void setAttendeeStatus(String jsonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            MessageDTO<AttendeeStatusDTO> message = mapper.readValue(jsonData, new TypeReference<>() {});
            if(message.type == MessageType.ATTENDEE_STATUS) {
                int result = attendeeRepository.updateStatusById(message.data.attendeeId, message.data.attendeeStatus);
                if(result > 0) {
                    sender.convertAndSend("/topic/rooms/" + message.roomId, jsonData);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi parse json data (setAttendeeStatus)", ex);
        }
    }

    @Override
    public boolean isAttendeeListValid(List<CreateAttendeeDTO> data) {
        for(CreateAttendeeDTO e : data) {
            if(e.checkInCode.isEmpty() || e.name.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void sendCloseRoomMessage(String roomId) {
        try {
            List<Attendee> attendees = roomRepository.getReferenceById(roomId).getAttendees();
            MessageDTO<HashMap<String, CheckInResultDTO>> message = new MessageDTO<>();
            message.type = MessageType.CLOSE_ROOM;
            message.roomId = roomId;
            message.data = new HashMap<>();
            for(Attendee attendee : attendees) {
                CheckInResultDTO result = new CheckInResultDTO();
                result.success = attendee.getCheckInStatus() == CheckInStatus.ACCEPTED;
                message.data.put(attendee.getId(), result);
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(message);
            sender.convertAndSend("/topic/rooms/" + roomId, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi gửi thông báo đóng phòng (sendCloseRoomMessage)", e);
        }
    }
}
