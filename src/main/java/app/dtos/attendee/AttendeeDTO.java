package app.dtos.attendee;

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
    public double distance;
    public String device;
    public String browser;
    public String ip; //IP check-in
    public String attendOn;
}
