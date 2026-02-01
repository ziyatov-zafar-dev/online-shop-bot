package uz.zafar.onlineshoptelegrambot.db.entity.enums;

public enum DiscountType {
    FIXED("Belgilangan chegirma", "Фиксированная скидка", "Fixed discount", "Белгиланган чегирма"),
    PERCENT("Foizli chegirma", "Процентная скидка", "Percent discount", "Фоизли чегирма"),
    NONE("Chegirma yo'q", "Скидка отсутствует", "No discount", "Чегирма йўқ");

    private final String descriptionUz;
    private final String descriptionRu;
    private final String descriptionEn;
    private final String descriptionCyrillic;

    DiscountType(String descriptionUz, String descriptionRu, String descriptionEn, String descriptionCyrillic) {
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

    // Til bo'yicha description olish metodi
    public String getDescription(String lang) {
        return switch (lang.toLowerCase()) {
            case "uz" -> descriptionUz;
            case "ru" -> descriptionRu;
            case "en" -> descriptionEn;
            case "cyrillic" -> descriptionCyrillic;
            default -> descriptionEn;
        };
    }
}
