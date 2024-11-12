package app.dtos.system;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogDTO {
    public String ip;
    public String time;
    public String description;
}
