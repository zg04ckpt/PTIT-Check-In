package app.controller;

import app.dtos.AdminPasswordDTO;
import app.dtos.CreateTestDTO;
import app.dtos.RoomStatusDTO;
import app.models.Room;
import app.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    public final RoomService roomService;

    public AdminController(RoomService roomService) {
        this.roomService = roomService;
    }
    //lấy mật khẩu người dùng
    @GetMapping("/login")
    public String loginAdmin(Model model){
        AdminPasswordDTO data =new AdminPasswordDTO();
        model.addAttribute("data",data);
        return "admin-login.html";
    }
    //kiểm tra và cho người dùng đăng nhập
    @PostMapping("/login")
    public String login(@ModelAttribute("data") AdminPasswordDTO data,Model model){
        System.out.println(data.PasswordDTO);
        if(!data.PasswordDTO.equals("123456")){
            model.addAttribute("data",data);
            model.addAttribute("message","ông nào đăng nhập thì nhập cho đúng vào:))");
            return "admin-login.html";
        }
        return "redirect:/admin/dashboard";
    }
    //lấy danh sách phòng và chuyển đến trang dashboard
    @GetMapping("/dashboard")
    public String listRoom(Model model){
        List<RoomStatusDTO> listRoomStatus= roomService.listRoomStatusDTO();
        if(!listRoomStatus.isEmpty()){
            for(RoomStatusDTO x:listRoomStatus){
                System.out.println(x);
            }
        }
        model.addAttribute("listRoomStatus",listRoomStatus);
        return "admin-dashboard";
    }
    //lấy id qua đường dẫn và xóa
    @GetMapping("/dashboard/{id}/delete")
    public String deleteByID(@PathVariable("id") String id){
        Room room=roomService.findById(id);
        if(room!=null) {
            roomService.deleteById(id);
        }
        return "admin-dashboard";
    }
}
