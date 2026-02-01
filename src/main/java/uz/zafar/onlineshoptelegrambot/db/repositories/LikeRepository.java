package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.like.Like;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    @Query("select l from Like l where l.product.pkey=:pid")
    List<Like> findAllByProductId(@Param("pid") UUID productId);

    @Query("select l from Like l where l.product.pkey=:pid and l.customer.pkey=:customer_id")
    Optional<Like> isLike(@Param("pid") UUID productId, @Param("customer_id") UUID customerId);
}
