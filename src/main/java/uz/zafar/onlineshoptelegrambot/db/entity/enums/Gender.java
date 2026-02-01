package uz.zafar.onlineshoptelegrambot.db.entity.enums;

public enum Gender {
    MALE("Erkak", "Мужчина", "Male", "Эркак"),
    FEMALE("Ayol", "Женщина", "Female", "Аёл");

    private final String msgUz;
    private final String msgRu;
    private final String msgEn;
    private final String msgCyrillic;

    Gender(String msgUz, String msgRu, String msgEn, String msgCyrillic) {
        this.msgUz = msgUz;
        this.msgRu = msgRu;
        this.msgEn = msgEn;
        this.msgCyrillic = msgCyrillic;
    }

    public String getMsgUz() {
        return msgUz;
    }

    public String getMsgRu() {
        return msgRu;
    }

    public String getMsgEn() {
        return msgEn;
    }

    public String getMsgCyrillic() {
        return msgCyrillic;
    }

    public String getMessage(String lang) {
        return switch (lang.toLowerCase()) {
            case "uz" -> msgUz;
            case "ru" -> msgRu;
            case "en" -> msgEn;
            case "cyrillic" -> msgCyrillic;
            default -> msgEn;
        };
    }
}
