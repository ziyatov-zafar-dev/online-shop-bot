package uz.zafar.onlineshoptelegrambot.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductResponse;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductTypeImageResponse;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductTypeResponse;

import java.util.Comparator;
import java.util.List;

@Component
public class ProductMapper {
    private final ProductTypeMapper productTypeMapper;
    private final DiscountMapper discountMapper;
    private final ShopMapper shopMapper;

    public ProductMapper(ProductTypeMapper productTypeMapper, DiscountMapper discountMapper, ShopMapper shopMapper) {
        this.productTypeMapper = productTypeMapper;
        this.discountMapper = discountMapper;
        this.shopMapper = shopMapper;
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) return new ProductResponse();
        return new ProductResponse(
                product.getPkey(),
                product.getNameUz(),
                product.getNameRu(),
                product.getNameEn(),
                product.getNameCyr(),
                product.getDescriptionUz(),
                product.getDescriptionCyr(),
                product.getDescriptionRu(),
                product.getDescriptionEn(),
                product.getSku(),
                product.getStatus(),
                product.getProductTypes() == null ? null : productTypeMapper.toList(
                        product.getProductTypes().stream()
                                .filter(type -> !type.getDeleted())
                                .sorted(Comparator.comparing(ProductType::getCreatedAt))
                                .toList()

                ),
                product.getCategory().getPkey(),
                product.getDiscount() == null ? null : discountMapper.toResponse(product.getDiscount()),
                shopMapper.toResponse(product.getShop()),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );

    }

    public List<ProductResponse> toList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    public Page<ProductResponse> toPage(Page<Product> products) {
        return products.map(this::toResponse);
    }
}
