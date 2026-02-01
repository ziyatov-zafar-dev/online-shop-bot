package uz.zafar.onlineshoptelegrambot.mapper;

import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.dto.shop.response.ShopResponse;

import java.util.List;

@Component
public class ShopMapper {

    public ShopResponse toResponse(Shop shop) {
        if (shop == null) return null;
        return new ShopResponse(
                shop.getPkey(),
                shop.getBrandUrl(),
                shop.getNameUz(),
                shop.getNameCyr(),
                shop.getNameEn(),
                shop.getNameRu(),
                shop.getDescriptionUz(),
                shop.getDescriptionCyr(),
                shop.getDescriptionRu(),
                shop.getDescriptionEn(),
                shop.getLogoUrl(),
                shop.getWorkStartTime(),
                shop.getWorkEndTime(),
                shop.getWorkDays(),
                shop.getPhone(),
                shop.getEmail(),
                shop.getTelegram(),
                shop.getInstagram(),
                shop.getFacebook(),
                shop.getWebsite(),
                shop.getHasPhone() != null ? shop.getHasPhone() : false,
                shop.getHasEmail() != null ? shop.getHasEmail() : false,
                shop.getHasTelegram() != null ? shop.getHasTelegram() : false,
                shop.getHasInstagram() != null ? shop.getHasInstagram() : false,
                shop.getHasFacebook() != null ? shop.getHasFacebook() : false,
                shop.getHasWebsite() != null ? shop.getHasWebsite() : false,
                shop.getCreatedAt() != null ? shop.getCreatedAt().toString() : null,
                shop.getUpdatedAt() != null ? shop.getUpdatedAt().toString() : null,
                shop.getLatitude(),
                shop.getLongitude(),
                shop.getAddress(),
                shop.getDeliveryInfoUz(),
                shop.getDeliveryInfoRu(),
                shop.getDeliveryInfoEn(),
                shop.getDeliveryInfoCyr(),
                shop.getDeliveryPrice(),
                shop.getHasDelivery(),
                shop.getDeliveryOutsidePrice(),
                shop.getHasOutsideDelivery(),
                shop.getSeller() != null ? shop.getSeller().getPkey() : null
        );
    }

    public List<ShopResponse> toList(List<Shop> shops) {
        return shops.stream().map(this::toResponse).toList();
    }
}
