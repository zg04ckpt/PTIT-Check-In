package app.repositories;

import app.models.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Integer> {
    List<Log> findByRoomId(String roomId);
    List<Log> findByAttendeeId(String attendeeId);
}
