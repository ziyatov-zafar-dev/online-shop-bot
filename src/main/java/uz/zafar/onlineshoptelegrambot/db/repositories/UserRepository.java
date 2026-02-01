package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("select u from User u where u.role='ADMIN'")
    List<User> admins();

    @Query("select u from User u where u.role='SUPER_ADMIN'")
    List<User> superadmins();
}
