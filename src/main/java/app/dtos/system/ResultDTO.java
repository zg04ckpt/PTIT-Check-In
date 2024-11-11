package app.dtos.system;

import lombok.Data;

@Data
public class ResultDTO {
    public String roomId;
    public String roomName;
    public int accepted;
    public int rejected;
    public int pending;
    public int outOfRoom;
}
