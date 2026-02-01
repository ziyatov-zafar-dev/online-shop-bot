package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.onlineshoptelegrambot.db.entity.order.OrderItem;
import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByShopOrder(ShopOrder shopOrder);
}
