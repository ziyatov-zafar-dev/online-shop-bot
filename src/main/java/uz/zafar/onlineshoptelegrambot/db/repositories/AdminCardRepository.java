package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.onlineshoptelegrambot.db.entity.AdminCard;

public interface AdminCardRepository extends JpaRepository<AdminCard,Integer> {
}
