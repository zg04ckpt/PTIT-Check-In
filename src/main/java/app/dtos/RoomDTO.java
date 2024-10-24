package app.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoomDTO {
    public String id;
    public String name;
    public String createBy;
    public boolean requireCheckLocation;
    public double latitude;
    public double longitude;
    public double acceptRange; //meter
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public boolean enableAutoApproval;
    public LocalDateTime createOn;
    public String code;
    public String url;
    public List<AttendeeDTO> attendees;
}
