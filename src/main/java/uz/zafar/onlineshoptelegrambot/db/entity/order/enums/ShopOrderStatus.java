package uz.zafar.onlineshoptelegrambot.db.entity.order.enums;

public enum ShopOrderStatus {

    NEW(
            "Yangi buyurtma",
            "Янги буюртма",
            "Новый заказ",
            "New order"
    ),
    PAYMENT_COMPLETED(
            "To‘lov to‘liq amalga oshirildi",
            "Тўлов тўлиқ амалга оширилди",
            "Оплата полностью произведена",
            "Payment fully completed"
    ),
    ACCEPTED(
            "Buyurtma qabul qilindi",
            "Буюртма қабул қилинди",
            "Заказ принят",
            "Order accepted"
    ),

    PREPARING(
            "Buyurtma tayyorlanmoqda",
            "Буюртма тайёрланмоқда",
            "Заказ готовится",
            "Order is being prepared"
    ),

    SENT(
            "Buyurtma jo'natildi",
            "Буюртма жўнатилди",
            "Заказ отправлен",
            "Order sent"
    ),

    COMPLETED(
            "Buyurtma yakunlandi",
            "Буюртма якунланди",
            "Заказ выполнен",
            "Order completed"
    ),

    CANCELLED(
            "Buyurtma bekor qilindi",
            "Буюртма бекор қилинди",
            "Заказ отменён",
            "Order cancelled"
    );

    private final String descriptionUz;
    private final String descriptionCyr;
    private final String descriptionRu;
    private final String descriptionEn;

    ShopOrderStatus(
            String descriptionUz,
            String descriptionCyr,
            String descriptionRu,
            String descriptionEn
    ) {
        this.descriptionUz = descriptionUz;
        this.descriptionCyr = descriptionCyr;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionUz(DeliveryType type) {
        if (type == DeliveryType.PICKUP && this == SENT) {
            return "Buyurtma olib ketishga tayyor";
        }
        return descriptionUz;
    }

    public String getDescriptionCyr(DeliveryType type) {
        if (type == DeliveryType.PICKUP && this == SENT) {
            return "Буюртма олиб кетишга тайёр";
        }
        return descriptionCyr;
    }

    public String getDescriptionRu(DeliveryType type) {
        if (type == DeliveryType.PICKUP && this == SENT) {
            return "Заказ готов к самовывозу";
        }
        return descriptionRu;
    }

    public String getDescriptionEn(DeliveryType type) {
        if (type == DeliveryType.PICKUP && this == SENT) {
            return "Order is ready for pickup";
        }
        return descriptionEn;
    }
}
