package app.dtos.attendee;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WaitingRoomDTO {
    public String roomId;
    public String attendeeId;
    public String attendeeName;
    public String roomName;
    public LocalDateTime end;
    public String roomCode;
    public String roomOwner;
    public String checkInCode;
}
