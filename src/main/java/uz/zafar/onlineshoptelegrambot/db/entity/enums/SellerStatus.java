package uz.zafar.onlineshoptelegrambot.db.entity.enums;

public enum SellerStatus {

    PENDING(
            "Sotuvchi arizasi qabul qilingan, ammo hali administrator tomonidan ko‘rib chiqilmagan. Sotuvchi savdo faoliyatini boshlay olmaydi.",
            "Сотувчи аризаси қабул қилинган, аммо ҳали администратор томонидан кўриб чиқилмаган. Сотувчи савдо фаолиятини бошлай олмайди.",
            "Заявка продавца принята, но ещё не рассмотрена администратором. Продавец не может начать торговую деятельность.",
            "The seller's application has been submitted but has not yet been reviewed by an administrator. The seller cannot start selling."
    ),

    APPROVED(
            "Sotuvchi arizasi administrator tomonidan tasdiqlangan. Sotuvchiga tizimdagi barcha savdo va boshqaruv imkoniyatlari ochiladi.",
            "Сотувчи аризаси администратор томонидан тасдиқланган. Сотувчига тизимдаги барча савдо ва бошқарув имкониятлари очилади.",
            "Заявка продавца одобрена администратором. Продавцу предоставлен полный доступ ко всем функциям продаж и управления.",
            "The seller's application has been approved by an administrator. The seller is granted full access to all sales and management features."
    ),

    REJECTED(
            "Sotuvchi arizasi administrator tomonidan rad etilgan. Sotuvchi savdo faoliyatini boshlay olmaydi va qayta ariza topshirishi talab qilinadi.",
            "Сотувчи аризаси администратор томонидан рад этилган. Сотувчи савдо фаолиятини бошлай олмайди ва қайта ариза топшириши талаб қилинади.",
            "Заявка продавца отклонена администратором. Продавец не может начать торговую деятельность и должен подать заявку повторно.",
            "The seller's application has been rejected by an administrator. The seller cannot start selling and must submit a new application."
    );

    private final String descriptionUz;
    private final String descriptionUzCyrillic;
    private final String descriptionRu;
    private final String descriptionEn;

    SellerStatus(String descriptionUz,
                 String descriptionUzCyrillic,
                 String descriptionRu,
                 String descriptionEn) {
        this.descriptionUz = descriptionUz;
        this.descriptionUzCyrillic = descriptionUzCyrillic;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionUz() {
        return descriptionUz;
    }

    public String getDescriptionUzCyrillic() {
        return descriptionUzCyrillic;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }
}
