package web.dto.room;

import web.dto.attendee.CreateAttendeeDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
public class CreateRoomDTO {
    public String name;
    public String createBy;
    public ArrayList<CreateAttendeeDTO> attendees = new ArrayList<>();
    public boolean requireCheckLocation;
    public double latitude;
    public double longitude;
    public double range;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public boolean enableAutoApproval;
}
