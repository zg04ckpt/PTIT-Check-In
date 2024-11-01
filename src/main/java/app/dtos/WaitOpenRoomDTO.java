package app.dtos;

import lombok.Data;

@Data
public class WaitOpenRoomDTO {
    public String roomId;
    public String roomName;
    public long remainingTime;
}
