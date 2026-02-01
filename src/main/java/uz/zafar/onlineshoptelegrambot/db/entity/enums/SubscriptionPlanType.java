package uz.zafar.onlineshoptelegrambot.db.entity.enums;

public enum SubscriptionPlanType {
    MONTH_1(1, "1 oy", "1 месяц", "1 month", "1 ой"),
    MONTH_2(2, "2 oy", "2 месяца", "2 months", "2 ой"),
    MONTH_3(3, "3 oy", "3 месяца", "3 months", "3 ой"),
    MONTH_6(6, "6 oy", "6 месяцев", "6 months", "6 ой"),
    MONTH_12(12, "12 oy", "12 месяцев", "12 months", "12 ой"),
    TRIAL(1, "Sinov muddati", "Пробный период", "Trial", "Синов муддати"),
    EXPIRED(0, "Muddati tugagan", "Срок истек", "Expired", "Муддати тугаган"),
    ACTIVE_ALWAYS(1200, "Umrbod obuna", "Бессрочная подписка", "Lifetime subscription", "Умрбод обуна");

    private final int month;
    private final String descriptionUz;
    private final String descriptionRu;
    private final String descriptionEn;
    private final String descriptionCyrillic;

    SubscriptionPlanType(int month, String descriptionUz, String descriptionRu, String descriptionEn, String descriptionCyrillic) {
        this.month = month;
        this.descriptionUz = descriptionUz;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
        this.descriptionCyrillic = descriptionCyrillic;
    }
    public int getMonth() {
        return month;
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

    public String getFullDescription(Language language) {
        return switch (this) {

            case MONTH_1 -> switch (language) {
                case UZBEK -> "1 oylik obuna";
                case CYRILLIC -> "1 ойлик обуна";
                case RUSSIAN -> "Подписка на 1 месяц";
                case ENGLISH -> "1-month subscription";
            };

            case MONTH_2 -> switch (language) {
                case UZBEK -> "2 oylik obuna";
                case CYRILLIC -> "2 ойлик обуна";
                case RUSSIAN -> "Подписка на 2 месяца";
                case ENGLISH -> "2-month subscription";
            };

            case MONTH_3 -> switch (language) {
                case UZBEK -> "3 oylik obuna";
                case CYRILLIC -> "3 ойлик обуна";
                case RUSSIAN -> "Подписка на 3 месяца";
                case ENGLISH -> "3-month subscription";
            };

            case MONTH_6 -> switch (language) {
                case UZBEK -> "6 oylik obuna";
                case CYRILLIC -> "6 ойлик обуна";
                case RUSSIAN -> "Подписка на 6 месяцев";
                case ENGLISH -> "6-month subscription";
            };

            case MONTH_12 -> switch (language) {
                case UZBEK -> "12 oylik obuna";
                case CYRILLIC -> "12 ойлик обуна";
                case RUSSIAN -> "Подписка на 12 месяцев";
                case ENGLISH -> "12-month subscription";
            };

            case TRIAL -> switch (language) {
                case UZBEK -> "Sinov muddati (bepul)";
                case CYRILLIC -> "Синов муддати (бепул)";
                case RUSSIAN -> "Пробный период (бесплатно)";
                case ENGLISH -> "Trial period (free)";
            };

            case EXPIRED -> switch (language) {
                case UZBEK -> "Obuna muddati tugagan";
                case CYRILLIC -> "Обуна муддати тугаган";
                case RUSSIAN -> "Подписка истекла";
                case ENGLISH -> "Subscription expired";
            };

            case ACTIVE_ALWAYS -> switch (language) {
                case UZBEK -> "Umrbod obuna (cheksiz)";
                case CYRILLIC -> "Умрбод обуна (чексиз)";
                case RUSSIAN -> "Бессрочная подписка";
                case ENGLISH -> "Lifetime subscription";
            };
        };
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
