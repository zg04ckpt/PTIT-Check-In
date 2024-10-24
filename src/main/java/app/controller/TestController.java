package app.controller;

import app.dtos.CheckInDTO;
import app.dtos.CreateRoomDTO;
import app.dtos.CreateTestDTO;
import app.services.TestService;
import jakarta.validation.Valid;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/tests")
public class TestController {
    private final TestService service;

    public TestController(TestService service) {
        this.service = service;
    }

    //GET
    @GetMapping("/link1")
    public String get(Model model) {
        CreateTestDTO data = new CreateTestDTO();
        model.addAttribute("data", data);
        return "file1.html";
    }

    //POST: tham số kiểu DTO
    @PostMapping("/link1")
    public String post(@ModelAttribute("data") CreateTestDTO data,  Model model) {
        //xử lý khi gặp lỗi
        var hasErrors = true;
        if(hasErrors) {
            model.addAttribute("data", data);
            model.addAttribute("message", "Có lỗi xảy ra");
            return "file2.html";
        }

        //chuyển hướng đến link2 vơí tham số kiểu query
        String a = "a";
        int b = 1;
        return "redirect:/tests/link2?" + "a=" + a + "&b=" + b;


        //chuyển hướng đến link3 thường
        //return "redirect:/tests/link3";


        //chuyển hướng đến link4 với tham số kiểu đường dẫn
        //String id = "skjlj";
        //return "redirect:/tests/link4/{" + id + "}";
    }

    @GetMapping("/link2")
    public String get2(@RequestParam String a, @RequestParam int b, Model model) {
        // xử lý
        return "file2.html";
    }

    @GetMapping("/link3")
    public String get3(Model model) {
        // xử lý
        return "file3.html";
    }

    @GetMapping("/link4/{id}")
    public String get4(@PathVariable("id") String id,  Model model) {
        // xử lý
        return "file4.html";
    }
}
