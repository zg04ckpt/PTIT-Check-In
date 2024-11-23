package web.service.impl;

import web.dto.attendee.AttendeeDTO;
import web.dto.attendee.AttendeeStatusDTO;
import web.dto.attendee.CheckInResultDTO;
import web.dto.attendee.CreateAttendeeDTO;
import web.dto.management.RoomStatusDTO;
import web.dto.room.CreateRoomDTO;
import web.dto.room.RoomDTO;
import web.dto.system.MessageDTO;
import web.dto.room.ResultDTO;
import web.model.enums.CheckInStatus;
import web.model.enums.MessageType;
import web.model.enums.RoomStatus;
import web.model.Attendee;
import web.model.Room;
import web.repository.IAttendeeRepository;
import web.repository.IRoomRepository;
import web.service.ILogService;
import web.service.IRoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class RoomService implements IRoomService {
    private final IRoomRepository roomRepository;
    private final IAttendeeRepository attendeeRepository;
    private final SimpMessagingTemplate sender;
    private final ILogService logService;
    @Autowired
    public RoomService(IRoomRepository roomRepository, IAttendeeRepository attendeeRepository, SimpMessagingTemplate sender, ILogService logService) {
        this.roomRepository = roomRepository;
        this.attendeeRepository = attendeeRepository;
        this.sender = sender;
        this.logService = logService;
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
                data.enableAutoApproval,
                LocalDateTime.now(),
                false
        );

        //Thiết lập trạng thái phòng
        if(data.startTime == null) {
            room.setStatus(RoomStatus.OPENING);
            room.setStartTime(LocalDateTime.now());
        } else {
            room.setStartTime(data.startTime);
            room.setStatus(RoomStatus.PENDING);
        }
        room.setEndTime(data.endTime);

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
        data.attendees = getAttendeeDTOs(room);

        return data;
    }

    private ArrayList<AttendeeDTO> getAttendeeDTOs(Room room) {
        List<Attendee> attendees = room.getAttendees();
        ArrayList<AttendeeDTO> attendeeDTOs = new ArrayList<>();
        attendees.forEach(e -> {
            AttendeeDTO attendeeDTO = new AttendeeDTO();
            attendeeDTO.id = e.getId();
            attendeeDTO.name = e.getName();
            attendeeDTO.checkInCode = e.getCheckInCode();
            attendeeDTO.checkInStatus = e.getCheckInStatus().ordinal();

            if(e.getCheckInStatus() != CheckInStatus.OUT_OF_ROOM && e.getCheckInStatus() != CheckInStatus.REJECTED) {
                // get additional information
                attendeeDTO.attendOn = DateTimeFormatter.ofPattern("HH:mm:ss").format(e.getAttendOn());
                if(e.getAttendOn().toLocalDate().isEqual(LocalDate.now())) {
                    attendeeDTO.attendOn += " - Hôm nay";
                } else {
                    attendeeDTO.attendOn += " - " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(e.getAttendOn());
                }
                attendeeDTO.distance = getDistance(room.getLatitude(), room.getLongitude(), e.getLatitude(), e.getLongitude());
                attendeeDTO.ip = e.getIp();
                UserAgent userAgent = UserAgent.parseUserAgentString(e.getUserAgent());
                attendeeDTO.device = userAgent.getOperatingSystem().getDeviceType().getName();
                attendeeDTO.browser = userAgent.getBrowser().getName();
            } else {
                attendeeDTO.distance = -1;
                attendeeDTO.attendOn = "--";
                attendeeDTO.ip = "--";
                attendeeDTO.device = "--";
                attendeeDTO.browser = "--";
            }

            attendeeDTOs.add(attendeeDTO);
        });
        return attendeeDTOs;
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Khoảng cách tính bằng km
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
    @Transactional
    public void setAttendeeStatus(String jsonData, HttpSession session, String ip) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            MessageDTO<AttendeeStatusDTO> message = mapper.readValue(jsonData, new TypeReference<>() {});
            Attendee attendee = attendeeRepository.getReferenceById(message.data.attendeeId);
            CheckInStatus status =  message.data.attendeeStatus;
            attendee.setCheckInStatus(status);
            // Ghi log
            if (status == CheckInStatus.WAIT_APPROVAL) {
                logService.writeLog("Đã bỏ điểm danh cho " + attendee.getName(), message.roomId, message.data.attendeeId, ip);
            } else if (status == CheckInStatus.ACCEPTED) {
                logService.writeLog("Đã điểm danh cho " + attendee.getName(), message.roomId, message.data.attendeeId, ip);
            } else if (status == CheckInStatus.OUT_OF_ROOM) {
                logService.writeLog(attendee.getName() + " đã thoát phòng.", message.roomId, message.data.attendeeId, ip);
            } else {
                logService.writeLog(attendee.getName() + " bị cấm vào phòng.", message.roomId, message.data.attendeeId, ip);
            }
            sender.convertAndSend("/topic/rooms/" + message.roomId, jsonData);
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
    public void closeRoom(String roomId) {
        Room room = roomRepository.getReferenceById(roomId);
        room.setEndTime(LocalDateTime.now());
        room.setStatus(RoomStatus.CLOSED);
        roomRepository.save(room);

        // Xoa phòng sau 15p
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {

            logService.writeLog("Đã xóa tự động phòng " + room.getName(), null, null, "--");
            scheduler.shutdown();
        };
        scheduler.schedule(task, 15, TimeUnit.MINUTES);

        // Send closing message
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

    @Override
    public RoomStatus getStatus(String roomId) {
        return roomRepository.getRoomStatus(roomId);
    }

    @Override
    public long getRemainingSecondsUntilRoomOpens(String roomId) {
        LocalDateTime startTime = roomRepository.getStartTime(roomId);
        if(startTime.isAfter(LocalDateTime.now())) {
            return Duration.between(LocalDateTime.now(), startTime).getSeconds();
        }
        return 0;
    }

    @Override
    public void openRoom(String roomId) {
        Room room = roomRepository.getReferenceById(roomId);
        room.setStatus(RoomStatus.OPENING);
        room.setStartTime(LocalDateTime.now());
        roomRepository.save(room);
    }
//    @Override
//    public String getGGMapUrl(String roomId, String attendeeId) {
//        Room room = roomRepository.getReferenceById(roomId);
//        if(!room.isRequireCheckLocation()) {
//            return "/error";
//        }
//        Attendee attendee = attendeeRepository.getReferenceById(attendeeId);
//        //https://www.google.com/maps/place/20%C2%B058'53.6%22N+105%C2%B047'06.5%22E
//        return String.format(
//                "https://www.google.com/maps/place/%sN+$%sE",
//                convertToDMS(attendee.getLatitude()),
//                convertToDMS(attendee.getLongitude())
//        );
//    }
//
//    private String convertToDMS(double decimalDegree) {
//        int degrees = (int) decimalDegree;
//        double fractionalPart = Math.abs(decimalDegree - degrees);
//        int minutes = (int) (fractionalPart * 60);
//        double seconds = (fractionalPart * 60 - minutes) * 60;
//        return degrees + "%C2%B0" + minutes + "'" + seconds + "%22";
//    }

    // ------------------- Manage Thịnh Hưng --------------------
    @Override
    public List<RoomStatusDTO> listRoomStatusDTO() {
        List<RoomStatusDTO> roomStatusDTOList=new ArrayList<>();
        List<Room> listRoom=roomRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");
        for(Room room:listRoom){
            RoomStatusDTO roomStatusDTO=new RoomStatusDTO();
            roomStatusDTO.id=room.getId();
            roomStatusDTO.name=room.getName();
            roomStatusDTO.createBy=room.getCreateBy();
            if(room.getStartTime() != null) {
                roomStatusDTO.startTime= formatter.format(room.getStartTime());
            }
            if(room.getEndTime() != null) {
                roomStatusDTO.endTime=formatter.format(room.getEndTime());
            }
            roomStatusDTO.status = room.getStatus();
            roomStatusDTOList.add(roomStatusDTO);
        }
        return roomStatusDTOList;
    }
    @Override//xóa phòng
    public void deleteById(String id) {
        roomRepository.deleteById(id);
    }
    @Override//kiếm phòng
    public Room findByID(String id) {
        return roomRepository.getReferenceById(id);
    }
}
