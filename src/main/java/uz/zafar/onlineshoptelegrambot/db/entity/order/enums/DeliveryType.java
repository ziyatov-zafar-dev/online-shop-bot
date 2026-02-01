package uz.zafar.onlineshoptelegrambot.db.entity.order.enums;

public enum DeliveryType {

    PICKUP(
            "O'zim olib ketaman",
            "Ўзим олиб кетаман",
            "Самовывоз",
            "Pickup"
    ),

    DELIVERY_INSIDE(
            "Manzilga yetkazib berish",
            "Манзилга етказиб бериш",
            "Доставка по городу",
            "Delivery"
    ),

    DELIVERY_OUTSIDE(
            "Shahar tashqarisiga yetkazib berish",
            "Шаҳар ташқарисига етказиб бериш",
            "Доставка за город",
            "Outside delivery"
    );

    private final String descriptionUz;
    private final String descriptionCyr;
    private final String descriptionRu;
    private final String descriptionEn;

    DeliveryType(
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

    public String getDescriptionUz() {
        return descriptionUz;
    }

    public String getDescriptionCyr() {
        return descriptionCyr;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }
}
