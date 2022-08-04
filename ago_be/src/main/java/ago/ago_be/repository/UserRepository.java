package ago.ago_be.repository;


import ago.ago_be.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
}
