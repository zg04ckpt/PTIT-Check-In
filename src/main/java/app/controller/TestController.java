package app.controller;

import app.dtos.CheckInDTO;
import app.dtos.CreateTestDTO;
import app.services.TestService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

//Bắt các link + xử lý tính hợp lệ của dữ liệu + đẩy dữ liệu vào file html

@Controller
@RequestMapping("/tests")
public class TestController {
    private TestService service;

    public TestController(TestService service) {
        this.service = service;
    }

    @GetMapping("")
    public String getHome() {
        return "index.html";
    }

    @PostMapping("")
    public String findRoom(@RequestParam("roomCode") String roomCode, Model model) {
        if(roomCode.length() != 6) {
            model.addAttribute("message", "Mã phải gồm 6 chữ số");
            return "index.html";
        }
        // xử lý


        return "redirect:/tests/check-in";
    }


    @PostMapping("/check-in")
    public String checkIn(@ModelAttribute("Data")CheckInDTO data, Model model) {
        if(data.checkInCode == null) {
            model.addAttribute("message", "Vui lòng điền mã điểm danh");
            return "check-in.html";
        }

        // xử lý

        return "check-in.html";
    }
}
