package app.controller;

import app.dtos.CheckInDTO;
import app.dtos.CreateTestDTO;
import app.dtos.WaitingRoomDTO;
import app.models.Room;
import app.services.AttendeeService;
import app.services.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//checkInCode mau: B22DCCN001, B22DCCN002, B22DCCN003, B22DCCN004

@Controller
@RequestMapping("/attendees")
public class AttendeesController {
    private final AttendeeService attendeeService;
    private final RoomService roomService;

    public AttendeesController(AttendeeService attendeeService, RoomService roomService) {
        this.attendeeService = attendeeService;
        this.roomService = roomService;
    }

    //code ở đây
    @GetMapping("/join-room")
    public String getHandle(@RequestParam String roomId, Model model) {
        //sử dụng roomId để lấy giá trị roomId từ trong query sau đó từ giá trị đấy gọi sang hàm findById trong roomService để lấy được cấu hình phòng
        Room roomConfiguration = roomService.findById(roomId);//tìm phòng theo roomId và lấy cấu hình phòng
        //khởi tại CheckInDTO với các cấu hình cần thiết lấy từ roomConfiguration bên trên
        CheckInDTO roomCheckIn = new CheckInDTO();
        roomCheckIn.roomId=roomConfiguration.getId();
        roomCheckIn.roomName= roomConfiguration.getName();
        roomCheckIn.roomCode=roomConfiguration.getCode();
        roomCheckIn.requireCheckLocation=roomConfiguration.isRequireCheckLocation();
        //trả về check-in.html kèm theo bến DTO
        model.addAttribute("data", roomCheckIn);
        return "check-in.html";
    }

    @PostMapping("/join-room")
    public String postHandle(@ModelAttribute("data") CheckInDTO data, Model model) {
        //@ModelAttribute là anotation hoạt động tương tự @RequestParam nhưng có tác dụng ánh xạ đến mô hình(ở đây là ánh xạ đến biến data vừa ược gửi lên)
        if(attendeeService.existsByRoomId(data.roomId)&&attendeeService.existsByCheckInCode(data.checkInCode)){
            //tìm kiếm dữ liệu attendees theo roomId và checkInCode nếu có đính kèm thêm biến kiểu query
            return "redirect:/attendees/waiting?"+"roomId="+data.roomId+"&checkInCode="+data.checkInCode;
        }
        //nếu không có dữ liệu roomId hoặc checkInCode thì trả về thông báo lỗi
        model.addAttribute("data", data);
        model.addAttribute("message", "Sai mã rồi bạn êi");
        return "check-in.html";
    }

    @GetMapping("/waiting")
    public String handleWaiting(@RequestParam String roomId,@RequestParam String checkInCode,Model model){

        Room roomConfiguration = roomService.findById(roomId);//tìm phòng theo roomId và lấy cấu hình phòng
        WaitingRoomDTO waitingRoomDTO = new WaitingRoomDTO();
        waitingRoomDTO.roomName= roomConfiguration.getName();
        waitingRoomDTO.roomCode= roomConfiguration.getCode();
        waitingRoomDTO.checkInCode=checkInCode;
        waitingRoomDTO.roomOwner=roomConfiguration.getCreateBy();
        model.addAttribute("data",waitingRoomDTO );
        return "waiting-room.html";
    }
}
