package uz.zafar.onlineshoptelegrambot.bot.kyb.user;

import org.apache.commons.codec.language.bm.Lang;
import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.bot.kyb.seller.SellerButton;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;
import uz.zafar.onlineshoptelegrambot.dto.bot.kyb.Button;
import uz.zafar.onlineshoptelegrambot.dto.bot.kyb.ButtonType;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserButton {

    private final SellerButton sellerButton;

    public UserButton(SellerButton sellerButton) {
        this.sellerButton = sellerButton;
    }

    public List<Button> menu(Language language) {
        List<Button> buttons = new ArrayList<>();

        switch (language) {
            case UZBEK -> {
                buttons.add(new Button("🛍️ Buyurtma berish", ButtonType.TEXT));
                buttons.add(new Button("📦 Mening buyurtmalarim", ButtonType.TEXT));
                buttons.add(new Button("👨‍💼 Sotuvchi bo'lish", ButtonType.TEXT));
                buttons.add(new Button("🌐 Tilni o'zgartirish", ButtonType.TEXT));
                buttons.add(new Button("ℹ️ Biz haqimizda", ButtonType.TEXT));
                buttons.add(new Button("📞 Biz bilan bog'lanish", ButtonType.TEXT));
            }
            case CYRILLIC -> {
                buttons.add(new Button("🛍️ Буюртма бериш", ButtonType.TEXT));
                buttons.add(new Button("\uD83D\uDCE6 Менинг буюртмаларим", ButtonType.TEXT));
                buttons.add(new Button("👨‍💼 Сотувчи бўлиш", ButtonType.TEXT));
                buttons.add(new Button("🌐 Тилни ўзгартириш", ButtonType.TEXT));
                buttons.add(new Button("ℹ️ Биз ҳақимизда", ButtonType.TEXT));
                buttons.add(new Button("📞 Биз билан боғланиш", ButtonType.TEXT));
            }
            case RUSSIAN -> {
                buttons.add(new Button("🛍️ Сделать заказ", ButtonType.TEXT));
                buttons.add(new Button("📦 Мои заказы", ButtonType.TEXT));
                buttons.add(new Button("👨‍💼 Стать продавцом", ButtonType.TEXT));
                buttons.add(new Button("🌐 Изменить язык", ButtonType.TEXT));
                buttons.add(new Button("ℹ️ О нас", ButtonType.TEXT));
                buttons.add(new Button("📞 Связаться с нами", ButtonType.TEXT));
            }
            case ENGLISH -> {
                buttons.add(new Button("🛍️ Place Order", ButtonType.TEXT));
                buttons.add(new Button("📦 My Orders", ButtonType.TEXT));
                buttons.add(new Button("👨‍💼 Become a Seller", ButtonType.TEXT));
                buttons.add(new Button("🌐 Change Language", ButtonType.TEXT));
                buttons.add(new Button("ℹ️ About Us", ButtonType.TEXT));
                buttons.add(new Button("📞 Contact Us", ButtonType.TEXT));
            }
        }

        return buttons;
    }

    public List<Button> requestLang() {
        return sellerButton.requestLang();
    }

    public List<Button> contactAndBackButtons(Language language) {

        Button contactButton;
        Button backButton;

        switch (language) {
            case UZBEK -> {
                contactButton = new Button("📞 Kontaktni ulashish", ButtonType.CONTACT);
                backButton = new Button("🔙 Orqaga", ButtonType.TEXT, "BACK", null, null);
            }
            case CYRILLIC -> {
                contactButton = new Button("📞 Контактни улашиш", ButtonType.CONTACT);
                backButton = new Button("🔙 Орқага", ButtonType.TEXT, "BACK", null, null);
            }
            case RUSSIAN -> {
                contactButton = new Button("📞 Поделиться контактом", ButtonType.CONTACT);
                backButton = new Button("🔙 Назад", ButtonType.TEXT, "BACK", null, null);
            }
            case ENGLISH -> {
                contactButton = new Button("📞 Share contact", ButtonType.CONTACT);
                backButton = new Button("🔙 Back", ButtonType.TEXT, "BACK", null, null);
            }
            default -> throw new IllegalStateException("Unexpected value: " + language);
        }

        return List.of(contactButton, backButton);
    }

    public List<Button> isAddedToMyLocation(Language language) {

        List<Button> buttons = new ArrayList<>();

        switch (language) {
            case UZBEK -> {
                buttons.add(new Button(
                        "✅ Tasdiqlash",
                        ButtonType.TEXT
                ));
                buttons.add(new Button(
                        "📌 Manzillarimga qo‘shish",
                        ButtonType.TEXT
                ));
                buttons.add(new Button(
                        "🔙 Orqaga",
                        ButtonType.TEXT
                ));
            }

            case CYRILLIC -> {
                buttons.add(new Button(
                        "✅ Тасдиқлаш",
                        ButtonType.TEXT
                ));
                buttons.add(new Button(
                        "📌 Манзилларимга қўшиш",
                        ButtonType.TEXT
                ));
                buttons.add(new Button(
                        "🔙 Орқага",
                        ButtonType.TEXT
                ));
            }

            case RUSSIAN -> {
                buttons.add(new Button(
                        "✅ Подтвердить",
                        ButtonType.TEXT
                ));
                buttons.add(new Button(
                        "📌 Добавить в мои адреса",
                        ButtonType.TEXT
                ));
                buttons.add(new Button(
                        "🔙 Назад",
                        ButtonType.TEXT
                ));
            }

            case ENGLISH -> {
                buttons.add(new Button(
                        "✅ Confirm",
                        ButtonType.TEXT
                ));
                buttons.add(new Button(
                        "📌 Add to my addresses",
                        ButtonType.TEXT
                ));
                buttons.add(new Button(
                        "🔙 Back",
                        ButtonType.TEXT
                ));
            }
        }

        return buttons;
    }

    private String backText(Language language) {
        return switch (language) {
            case UZBEK -> "🔙 Orqaga";
            case CYRILLIC -> "🔙 Орқага";
            case RUSSIAN -> "🔙 Назад";
            case ENGLISH -> "🔙 Back";
        };
    }

    public List<Button> backBtn(Language language) {
        return List.of(new Button(
                backText(language),
                ButtonType.TEXT
        ));
    }
}
