package app.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTestDTO {
    @NotEmpty(message = "Thuộc tính 1 không thể để trống")
    @Size(min = 2, max = 10, message = "Thuộc tính 1 phải dài từ 2 đến 10 kí tự")
    public String prop1;
    @NotEmpty(message = "Thuộc tính 2 không thể để trống")
    public String prop2;
}
