package web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.entity.User;

@Repository//giúp tạo biến UserRepository
public interface UserRepository extends JpaRepository<User, String> {

}
