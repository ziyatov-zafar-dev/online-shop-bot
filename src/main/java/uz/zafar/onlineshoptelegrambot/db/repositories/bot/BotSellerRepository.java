package uz.zafar.onlineshoptelegrambot.db.repositories.bot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.bot.kyb.seller.SellerKyb;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BotSellerRepository extends JpaRepository<BotSeller, UUID> {
    @Query("select bs from BotSeller bs where bs.chatId=:ch")
    Optional<BotSeller>checkUser(@Param("ch") Long chatId);

    @Query("select u from BotSeller u where u.userid=:uid")
    List<BotSeller> findAllByUserId(@Param("uid") UUID userid);
}
