package app.controller;

import app.dtos.CreateTestDTO;
import app.services.TestService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

//Bắt các link + xử lý tính hợp lệ của dữ liệu + đẩy dữ liệu vào file html

@Controller
@RequestMapping("/tests")
public class TestController {
    private TestService service;

    public TestController(TestService service) {
        this.service = service;
    }

    @GetMapping("/create")
    public String getCreateTestForm(Model model) {
        model.addAttribute("data", new CreateTestDTO());
        return "test.html";
    }

    @PostMapping("/create")
    public String createTest(@Valid @ModelAttribute CreateTestDTO dto, BindingResult result, Model model) {

        if(result.hasErrors()) {
            ArrayList<String> errors = new ArrayList<>();
            for(var error: result.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }
            model.addAttribute("errors", errors);
            model.addAttribute("data", dto);
            return "test.html";
        }

        if(service.createNewTest(dto)) {
            return "redirect:/";
        } else {
            model.addAttribute("errors", new String[]{"Lỗi tạo test"});
            model.addAttribute("data", dto);
            return "test.html";
        }
    }
}
