package app.dtos;

import lombok.Data;

@Data
public class WaitingRoomDTO {
    public String roomId;
    public String attendeeId;
    public String roomName;
    public String roomCode;
    public String roomOwner;
    public String checkInCode;
}
