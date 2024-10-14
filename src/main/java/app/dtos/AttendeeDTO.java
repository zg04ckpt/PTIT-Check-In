package app.dtos;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendeeDTO {
    public String id;
    public String checkInCode;
    public String name;
    public double latitude; //Vĩ độ
    public double longitude; //Kinh độ
    @Enumerated(EnumType.ORDINAL)
    public int checkInStatus;
    public double distance; //km
    public String ipAddress; //IP check-in
    private LocalDateTime attendOn;
}
