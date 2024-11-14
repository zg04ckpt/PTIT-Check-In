package app.dtos.management;

import app.enums.RoomStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data//hiển thị trạng thái phòng
public class RoomStatusDTO {
    public String id;
    public String name; //tên phòng
    public String createBy; // người tạo phòng
    public LocalDateTime startTime; // thời gian mở phòng (null -> mở lúc tạo)
    public LocalDateTime endTime; // thời gian đóng phòng (null ->đóng bằng tay)
    @Enumerated(EnumType.ORDINAL)
    public RoomStatus status; // trạng thái phòng
}