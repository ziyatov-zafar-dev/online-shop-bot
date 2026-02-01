package uz.zafar.onlineshoptelegrambot.db.repositories.category;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;

import java.util.UUID;

public interface ProductTypeImageRepository extends JpaRepository<ProductTypeImage, UUID> {
}
