package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.zafar.onlineshoptelegrambot.db.entity.ads.Ads;

import java.util.List;
import java.util.UUID;

public interface AdsRepository extends JpaRepository<Ads, UUID> {
    @Query("select a from Ads a order by a.created desc")
    List<Ads>list();
}
