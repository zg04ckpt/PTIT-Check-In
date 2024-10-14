package app.dtos;

import lombok.Data;

@Data
public class ResultDTO {
    public int accepted;
    public int rejected;
    public int pending;
    public int outOfRoom;
}
