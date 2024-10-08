package app.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    public String name;

    public String email;
    public String pass;
    public String confirmPass;

}
