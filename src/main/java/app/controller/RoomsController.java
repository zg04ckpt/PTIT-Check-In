package app.controller;

import app.dtos.*;
import app.enums.CheckInStatus;
import app.models.Attendee;
import app.models.Room;
import app.repositories.AttendeeRepository;
import app.repositories.RoomRepository;
import app.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// id phong mau: 6f7ffd60-959a-4953-9444-6bf0919dbe4a
// ma phong mau: HS12JK
@Controller
@RequestMapping("/rooms")
public class RoomsController {
    private RoomService roomService;
    //test
    private RoomRepository roomRepository;
    private AttendeeRepository attendeeRepository;

    public RoomsController(RoomService roomService, RoomRepository roomRepository, AttendeeRepository attendeeRepository) {
        this.roomService = roomService;
        this.roomRepository = roomRepository;
        this.attendeeRepository = attendeeRepository;
    }

    //endpoints
    @GetMapping("/create-room")
    public String getCreateForm(Model model) {
        CreateRoomDTO data = new CreateRoomDTO();
        model.addAttribute("data", data);
        return "create-room.html";
    }

    @PostMapping("/create-room")
    public String createRoom(@ModelAttribute("data") CreateRoomDTO data, Model model, HttpServletRequest request) {
        if(data.name.isEmpty()) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Tên phòng trống");
            return "create-room.html";
        }

        if(data.createBy.isEmpty()) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Tên chủ phòng trống");
            return "create-room.html";
        }

        if(data.attendees.isEmpty()) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Danh sách người tham gia trống");
            return "create-room.html";
        }

        if(roomRepository.existsByName(data.name)) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Tên phòng bị trùng");
            return "create-room.html";
        }

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

        room.setUrl(request.getScheme() + "://www." + request.getServerName() + "/rooms/" + room.getId());

        room.generateRandomCode();
        while (roomRepository.existsByCode(room.getCode())) {
            room.generateRandomCode();
        }
        Room result = roomRepository.save(room);
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
        return "redirect:/rooms/" + result.getId();
    }

    @GetMapping("/{roomId}")
    public String getRoom(@PathVariable("roomId") String roomId,  Model model) {
        Room room = roomRepository.getReferenceById(roomId);
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.id = room.getId();
        roomDTO.name = room.getName();
        roomDTO.createBy = room.getCreateBy();
        roomDTO.requireCheckLocation = room.isRequireCheckLocation();
        roomDTO.latitude = room.getLatitude();
        roomDTO.longitude = room.getLongitude();
        roomDTO.acceptRange = room.getAcceptRange();
        roomDTO.startTime = room.getStartTime();
        roomDTO.endTime = room.getEndTime();
        roomDTO.enableAutoApproval = room.isEnableAutoApproval();
        roomDTO.createOn = room.getCreateOn();
        roomDTO.code = room.getCode();
        roomDTO.url = room.getUrl();
        List<Attendee> attendees = room.getAttendees();
        List<AttendeeDTO> attendeeDTOs = new ArrayList<>();
        attendees.forEach(e -> {
            AttendeeDTO attendeeDTO = new AttendeeDTO();
            attendeeDTO.id = e.getId();
            attendeeDTO.name = e.getName();

            attendeeDTO.checkInCode = e.getCheckInCode();
            attendeeDTO.checkInStatus = e.getCheckInStatus().ordinal();
            attendeeDTOs.add(attendeeDTO);
        });
        roomDTO.attendees = attendeeDTOs;
//        roomDTO.attendees.get(0).checkInStatus = CheckInStatus.ACCEPTED.ordinal();
//        roomDTO.attendees.get(1).checkInStatus = CheckInStatus.WAIT_APPROVAL.ordinal();
//        roomDTO.attendees.get(2).checkInStatus = CheckInStatus.WAIT_APPROVAL.ordinal();
        model.addAttribute("data", roomDTO);
        return "room.html";
    }

    @PostMapping("/result")
    public String getResult(@ModelAttribute("data") RoomDTO data, Model model) {
        int success = 0, reject = 0, wait = 0;
        for(var e : data.attendees) {
            switch (e.checkInStatus) {
                case 2 -> success++;
                case 3 -> reject++;
                case 1 -> wait++;
            }
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.accepted = success;
        resultDTO.rejected = reject;
        resultDTO.pending = wait;
        resultDTO.outOfRoom = data.attendees.size() - success - reject - wait;

        model.addAttribute("data", resultDTO);
        return "result.html";
    }
}
