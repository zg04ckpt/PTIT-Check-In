package web.repository;

import web.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ILogRepository extends JpaRepository<Log, Integer> {
    List<Log> findByRoomId(String roomId);
    List<Log> findByAttendeeId(String attendeeId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Log l WHERE l.attendeeId = ?1")
    void deleteByAttendeeId(String attendeeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Log l WHERE l.roomId = ?1")
    void deleteByRoomId(String roomId);
}
