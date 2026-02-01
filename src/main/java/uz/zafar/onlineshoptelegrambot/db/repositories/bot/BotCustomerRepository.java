package uz.zafar.onlineshoptelegrambot.db.repositories.bot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;

import java.util.Optional;
import java.util.UUID;

public interface BotCustomerRepository extends JpaRepository<BotCustomer, UUID> {
    @Query("select b from BotCustomer b where b.chatId=:ch")
    Optional<BotCustomer> checkUser(@Param("ch") Long chatId);

    @Query("select b from BotCustomer b where b.pkey=:ch")
    Optional<BotCustomer> checkUser(@Param("ch") UUID id);
}
