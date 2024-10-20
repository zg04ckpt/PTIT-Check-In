package app.repositories;

import app.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, String> {
    //dựa vào thuộc tính code để tìm kiếm phòng
    Room findByCode(String code);
    //sử dụng code để kiểm tra xem có tồn tại phòng không
    boolean existsByCode(String code);
}
