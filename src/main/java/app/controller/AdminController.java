package app.controller;

import app.dtos.management.AdminPasswordDTO;
import app.dtos.management.RoomStatusDTO;
import app.models.Room;
import app.services.ILogService;
import app.services.impl.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Thịnh + Hưng
@Controller
@RequestMapping("/admin")
public class AdminController {
    public final RoomService roomService;
    public final ILogService logService;

    @Value("${spring.base-url}")
    public String baseUrl;

    public AdminController(RoomService roomService, ILogService logService) {
        this.roomService = roomService;
        this.logService = logService;
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
    public String login(@ModelAttribute("data") AdminPasswordDTO data, Model model, HttpSession session){
        if(!data.PasswordDTO.equals("123456")){
            model.addAttribute("data",data);
            model.addAttribute("message","Sai mật khẩu");
            return "admin-login.html";
        }

        session.setAttribute("isAdmin", true);

        return "redirect:" + baseUrl + "/admin/dashboard";
    }

    //lấy danh sách phòng và chuyển đến trang dashboard
    @GetMapping("/dashboard")
    public String listRoom(Model model, HttpSession session){
        if(session.getAttribute("isAdmin") == null) {
            return "redirect:" + baseUrl + "/error";
        }
        List<RoomStatusDTO> listRoomStatus= roomService.listRoomStatusDTO();
        model.addAttribute("listRoomStatus",listRoomStatus);
        return "admin-dashboard";
    }

    //lấy id qua đường dẫn và xóa
    @DeleteMapping("/dashboard/{id}/delete")
    public ResponseEntity<String> deleteByID(@PathVariable("id") String id, HttpSession session){
        Room room=roomService.findById(id);
        if(room!=null && session.getAttribute("isAdmin") != null) {
            roomService.deleteById(id);
            return ResponseEntity.ok().body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @DeleteMapping("/logs")
    public ResponseEntity<String> deleteAllSystemLogs(HttpSession session){
        if(session.getAttribute("isAdmin") != null) {
            logService.removeAllLogs();
            return ResponseEntity.ok().body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }
}
