package app.repositories;

import app.enums.CheckInStatus;
import app.models.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IAttendeeRepository extends JpaRepository<Attendee, String> {
    Attendee findByCheckInCodeAndRoomId(String checkInCode, String roomId);
    boolean existsByIdAndRoomId(String id, String roomId);

    @Modifying
    @Transactional
    @Query("UPDATE Attendee a SET a.checkInStatus = :status WHERE a.id = :id")
    int updateStatusById(String id, CheckInStatus status);
}
