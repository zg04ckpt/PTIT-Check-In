package web.repository;

import web.model.enums.RoomStatus;
import web.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface IRoomRepository extends JpaRepository<Room, String> {
    //dựa vào thuộc tính code để tìm kiếm phòng
    Room findByCode(String code);
    //sử dụng code để kiểm tra xem có tồn tại phòng không
    boolean existsByCode(String code);
    boolean existsByName(String name);
    @Query("SELECT r.startTime from Room r WHERE r.id = :roomId")
    LocalDateTime getStartTime(String roomId);
    @Query("SELECT r.status from Room r WHERE r.id = :roomId")
    RoomStatus getRoomStatus(String roomId);
    @Modifying
    @Transactional
    @Query("UPDATE Room r SET r.status = :status WHERE r.id = :id")
    int updateRoomStatus(String id, RoomStatus status);
}
