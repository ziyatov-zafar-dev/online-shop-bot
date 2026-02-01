package uz.zafar.onlineshoptelegrambot.db.entity.enums;

public enum AppliedTo {
    PRODUCT("Mahsulot uchun", "Для продукта", "For product", "Маҳсулот учун"),
    PLATFORM_PLAN("Obuna uchun", "Для подписки", "For subscription plan", "Обуна учун");

    private final String descriptionUz;
    private final String descriptionRu;
    private final String descriptionEn;
    private final String descriptionCyrillic;

    AppliedTo(String descriptionUz, String descriptionRu, String descriptionEn, String descriptionCyrillic) {
        this.descriptionUz = descriptionUz;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
        this.descriptionCyrillic = descriptionCyrillic;
    }

    public String getDescriptionUz() {
        return descriptionUz;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public String getDescriptionCyrillic() {
        return descriptionCyrillic;
    }

    public String getDescription(String lang) {
        return switch (lang.toLowerCase()) {
            case "uz" -> descriptionUz;
            case "ru" -> descriptionRu;
            case "cyrillic" -> descriptionCyrillic;
            default -> descriptionEn;
        };
    }
}
