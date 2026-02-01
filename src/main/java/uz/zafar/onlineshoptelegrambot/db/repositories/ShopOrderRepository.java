package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, UUID> {
    @Query("""
                select so
                from ShopOrder so
                where so.customer.pkey = :pkey
            """)
    List<ShopOrder> findAllByCustomer_Pkey(UUID pkey);

    @Query("select sh from ShopOrder sh where sh.customer.pkey=:customer_id order by sh.updatedAt asc")
    List<ShopOrder> myOrders(@Param("customer_id") UUID customerId);

    @Query("select sh from ShopOrder sh where sh.shop.pkey=:shopId order by sh.createdAt desc")
    List<ShopOrder> findAllByShopId(@Param("shopId") UUID pkey);

    @Query("select sh from ShopOrder sh where sh.status=:status")
    List<ShopOrder> findAllByStatus(@Param("status") ShopOrderStatus status);

    @Query("""
                select sh
                from ShopOrder sh
                where sh.createdAt >= :fromDate
            """)
    List<ShopOrder> findAllByLastWeek(@Param("fromDate") LocalDateTime fromDate);

    @Query("""
                select sh 
                from ShopOrder sh
                where sh.createdAt >= :fromDate
            """)
    List<ShopOrder> findAllByLastMonth(@Param("fromDate") LocalDateTime fromDate);

    @Query("""
                select so
                from ShopOrder so
                where so.seller.pkey = :sellerId
            """)
    List<ShopOrder> findSellerOrders(@Param("sellerId") Long sellerId);


}
