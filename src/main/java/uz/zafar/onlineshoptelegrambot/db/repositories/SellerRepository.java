package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    @Query("select s from Seller s where s.userid=:userid")
    Optional<Seller> findByUserid(@Param("userid") UUID userid);

    @Query("""
                SELECT s
                FROM Seller s
                WHERE s.planExpiresAt IS NOT NULL
                  AND s.planExpiresAt < :now
                  AND s.subscriptionPlanId IN (
                      SELECT sp.pkey
                      FROM SubscriptionPlan sp
                      WHERE sp.name IN :planTypes
                  )
            """)
    List<Seller> findAllExpiredSellers(
            @Param("now") LocalDateTime now,
            @Param("planTypes") List<SubscriptionPlanType> planTypes
    );


    @Query("""
        SELECT s
        FROM Seller s
        WHERE s.pkey = :sellerId
          AND s.planExpiresAt IS NOT NULL
          AND s.planExpiresAt >= CURRENT_TIMESTAMP
          AND s.status = uz.zafar.onlineshoptelegrambot.db.entity.enums.SellerStatus.APPROVED
        """)
    Optional<Seller> checkActivePlanSeller(@Param("sellerId") Long sellerId);

}
