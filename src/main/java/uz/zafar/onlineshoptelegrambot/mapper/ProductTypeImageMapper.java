package uz.zafar.onlineshoptelegrambot.mapper;

import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductTypeImageResponse;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductTypeResponse;

import java.util.List;

@Component
public class ProductTypeImageMapper {
    public ProductTypeImageResponse toResponse(ProductTypeImage image) {
        ProductTypeImageResponse response = new ProductTypeImageResponse();
        response.setId(image.getPkey());
        response.setImageUrl(image.getImageUrl());
        response.setImgName(image.getImgName());
        response.setImgSize(image.getImgSize());
        response.setMain(image.getMain());
        response.setProductTypeId(image.getProductType().getPkey());
        response.setCreatedAt(image.getCreatedAt());
        response.setUpdatedAt(image.getUpdatedAt());
        return response;
    }

    public List<ProductTypeImageResponse> toList(List<ProductTypeImage> images) {
        return images.stream()
                .map(this::toResponse)
                .toList();
    }

}
