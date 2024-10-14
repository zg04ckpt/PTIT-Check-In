package app.repositories;

import app.models.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendeeRepository extends JpaRepository<Attendee, String> {
    Attendee findByRoomIdAndCheckInCode(String roomId, String checkInCode);
}
