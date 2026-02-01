package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.ads.AdsButton;

import java.util.List;
import java.util.UUID;

public interface AdsButtonRepository extends JpaRepository<AdsButton, UUID> {
    @Query("select a from AdsButton a where a.adsId=:adsId ")
    List<AdsButton> findAllByAdsId(@Param("adsId") UUID adsId);
}
