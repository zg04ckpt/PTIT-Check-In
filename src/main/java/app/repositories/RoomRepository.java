package app.repositories;

import app.enums.RoomStatus;
import app.models.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {
    //dựa vào thuộc tính code để tìm kiếm phòng
    Room findByCode(String code);
    //sử dụng code để kiểm tra xem có tồn tại phòng không
    boolean existsByCode(String code);
    boolean existsByName(String name);
  //  Optional<Room> findById(String id);
//    @Modifying
//    @Transactional
//    @Query("UPDATE Room a a.Status = :status WHERE a.id = :id")
//    int updateStatusById(String id, RoomStatus status);
}
