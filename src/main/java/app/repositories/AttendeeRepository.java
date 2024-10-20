package app.repositories;

import app.models.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendeeRepository extends JpaRepository<Attendee, String> {
    //kiểm tra xem trong csdl có tồn tại roomId hay checkInCode trùng khớp không
    boolean existsByRoomId(String roomId);
    boolean existsByCheckInCode(String checkInCode);
}
