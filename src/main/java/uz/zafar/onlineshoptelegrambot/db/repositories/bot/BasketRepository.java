package uz.zafar.onlineshoptelegrambot.db.repositories.bot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.order.Basket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BasketRepository extends JpaRepository<Basket, UUID> {
    @Query("SELECT b from Basket b where b.customer.pkey=:customer_id and b.productType.pkey=:product_type_id and b.type='DRAFT'")
    Optional<Basket> checkBasket(@Param("customer_id") UUID customerId,
                                 @Param("product_type_id") UUID productTypeId);

    @Query("SELECT b from Basket b where b.customer.pkey=:customer_id and b.type='DRAFT' order by b.created asc")
    List<Basket> myBaskets(@Param("customer_id") UUID customerId);
    @Query("select b from Basket b where b.customer.pkey=:customerId and b.productType.pkey=:typeId and b.type='DRAFT'")
    Optional<Basket>findByCustomerIdAndTypeId(@Param("customerId") UUID customerId,@Param("typeId") UUID typeId);
}
