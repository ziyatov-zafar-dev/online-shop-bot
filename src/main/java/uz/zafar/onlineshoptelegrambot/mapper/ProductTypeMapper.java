package uz.zafar.onlineshoptelegrambot.mapper;

import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductTypeResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductTypeMapper {
    private final ProductTypeImageMapper productTypeImageMapper;

    public ProductTypeMapper(ProductTypeImageMapper productTypeImageMapper) {
        this.productTypeImageMapper = productTypeImageMapper;
    }

    public ProductTypeResponse toResponse(ProductType type) {
        return new ProductTypeResponse(
                type.getPkey(),
                type.getNameUz(),
                type.getNameCyr(),
                type.getNameRu(),
                type.getNameEn(),
                type.getPrice(),
                type.getProduct().getPkey(),
                (type.getImages() == null || type.getImages().isEmpty()) ? new ArrayList<>() : productTypeImageMapper.toList(
                        type.getImages().stream()
                                .filter(img -> !img.getDeleted())
                                .sorted(Comparator.comparing(ProductTypeImage::getCreatedAt))
                                .collect(Collectors.toList())
                ),
                type.getCreatedAt(),
                type.getUpdatedAt(),
                type.getStock()
        );
    }

    public List<ProductTypeResponse> toList(List<ProductType> types) {
        return types.stream()
                .map(this::toResponse)
                .toList();

    }
}
