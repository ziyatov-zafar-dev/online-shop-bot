package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.onlineshoptelegrambot.db.entity.SellerVideo;

public interface SellerVideoRepository extends JpaRepository<SellerVideo, Integer> {
}
