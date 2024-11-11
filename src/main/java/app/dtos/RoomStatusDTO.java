package app.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Data//hiển thị trạng thái phòng
public class RoomStatusDTO {
    public String id;
    public String name; //tên phòng
    public String createBy; // người tạo phòng
    public double latitude; // vĩ độ mốc (phòng)
    public double longitude; // kinh độ mốc (phòng)
    public double acceptRange; // phạm vi hop lệ

    public LocalDateTime startTime; // thời gian mở phòng (null -> mở lúc tạo)
    public LocalDateTime endTime; // thời gian đóng phòng (null ->đóng bằng tay)
    public boolean isEnded; // đã đóng phòng hay chưa
}
