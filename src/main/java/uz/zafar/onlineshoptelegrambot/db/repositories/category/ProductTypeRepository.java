package uz.zafar.onlineshoptelegrambot.db.repositories.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;

import java.util.List;
import java.util.UUID;

public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {
    @Query("select pt from ProductType pt where pt.product.pkey=:id and pt.deleted=false order by pt.createdAt asc")
    List<ProductType> findAllByProductId(@Param("id") UUID productId);
}
