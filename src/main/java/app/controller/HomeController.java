package app.controller;

import app.dtos.CreateTestDTO;
import app.dtos.SearchRoomDTO;
import app.models.Room;
import app.services.AttendeeService;
import app.services.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final RoomService roomService;

    public HomeController( RoomService roomService) {
        this.roomService = roomService;
    }

    //code ở đây
    @GetMapping("/")
    public String returnHome( Model model) {
        SearchRoomDTO data = new SearchRoomDTO();
        model.addAttribute("data", data);
        return "index.html";
    }

    @PostMapping("/")
    public String handle(@ModelAttribute("data") SearchRoomDTO data,  Model model) {
        /*kiểm tra lỗi xem đối tượng gửi lên bị lỗi không*/
        if (!roomService.isRoomCodeExists(data.roomCode)) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Nhập đúng tên phòng vào bạn êi,cho nghỉ học giờ:)))");
            return "index.html";/*nếu lối in ra thông báo tra về index*/
        }else {
            /*nếu không lỗi sử dung phương thức findByCode thì đến data.roomCode để lấy ra thông tin phòng*/
            Room room = roomService.findByCode(data.roomCode);
            return "redirect:/attendees/join-room?"+"roomId="+room.getId();/*chuyển hướng phòng sang attendees đính kèm thêm query*/

        }
    }
}
