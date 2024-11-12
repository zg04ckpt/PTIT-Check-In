package app.dtos.attendee;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SearchRoomDTO {
    public String roomCode;

}
