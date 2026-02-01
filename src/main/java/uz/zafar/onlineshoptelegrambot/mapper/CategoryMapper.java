package uz.zafar.onlineshoptelegrambot.mapper;

import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.dto.product.response.CategoryResponse;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductResponse;

import java.util.List;

@Component
public class CategoryMapper {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public CategoryMapper(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getPkey(),
                category.getNameUz(),
                category.getNameCyr(),
                category.getNameEn(),
                category.getNameRu(),
                category.getDescriptionUz(),
                category.getDescriptionCyr(),
                category.getDescriptionRu(),
                category.getDescriptionEn(),
                category.getOrderNumber(),
                category.getImageUrl(),
                category.getParent() == null ? null : category.getParent().getPkey(),
                (category.getChildren().isEmpty() || category.getChildren() == null) ? List.of() : toList(category.getChildren()),
                productMapper.toList(productRepository.findAllSellerProductByCategoryId(category.getPkey())),
                category.getCreatedAt(), category.getUpdatedAt()
        );
    }

    public List<CategoryResponse> toList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)
                .toList();
    }
}
