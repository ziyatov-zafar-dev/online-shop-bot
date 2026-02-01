package uz.zafar.onlineshoptelegrambot.dto.order.response;

import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.DeliveryType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus;

public class ShopOrderStatusInfo {
    private final String name;
    private final String descUz;
    private final String descCyr;
    private final String descEn;
    private final String descRu;

    public ShopOrderStatusInfo(ShopOrderStatus status, DeliveryType type) {
        this.name = status.name();
        this.descCyr = status.getDescriptionCyr(type);
        this.descUz = status.getDescriptionUz(type);
        this.descEn = status.getDescriptionEn(type);
        this.descRu = status.getDescriptionRu(type);
    }

    public String getName() {
        return name;
    }

    public String getDescUz() {
        return descUz;
    }

    public String getDescCyr() {
        return descCyr;
    }

    public String getDescEn() {
        return descEn;
    }

    public String getDescRu() {
        return descRu;
    }
}
