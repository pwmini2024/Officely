package uni.projects.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.projects.backend.models.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByNameAndSurname(String name, String surname);
    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByPhoneNumberAndDeletedFalse(String email);
}
