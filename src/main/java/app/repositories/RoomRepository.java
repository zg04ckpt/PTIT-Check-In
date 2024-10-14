package app.repositories;

import app.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, String> {
    Room findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
