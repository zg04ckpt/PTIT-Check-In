package app.dtos.attendee;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CheckInDTO {
    public String roomId;
    public String roomName;
    public String roomCode;
    @NotEmpty(message = "Vui lòng nhập mã check-in")
    public String checkInCode;
    public boolean requireCheckLocation;
    public double latitude;
    public double longitude;
    public String ipAddress;

}
