package uz.zafar.onlineshoptelegrambot.bot.kyb.seller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;
import uz.zafar.onlineshoptelegrambot.dto.bot.kyb.Button;
import uz.zafar.onlineshoptelegrambot.dto.bot.kyb.ButtonType;

import java.util.ArrayList;
import java.util.List;

@Component
public class SellerButton {
    private final String orderWebappUrl;
    private final String paymentWebbAppUrl;
    private final String financeWebbAppUrl;
    private final String commentaryWebappUrl;

    public SellerButton(@Value("${telegram.app.seller.order.webapp.url}") String orderWebappUrl,
                        @Value("${telegram.app.seller.payment.webapp.url}") String paymentWebbAppUrl,
                        @Value("${telegram.app.seller.commentary.webapp.url}") String commentaryWebappUrl,
                        @Value("${telegram.app.seller.finance.webapp.url}") String financeWebbAppUrl
    ) {
        this.orderWebappUrl = orderWebappUrl;
        this.paymentWebbAppUrl = paymentWebbAppUrl;
        this.financeWebbAppUrl = financeWebbAppUrl;
        this.commentaryWebappUrl = commentaryWebappUrl;
    }

    /*public List<Button> menu(Language language, String shopWebappUrl, Long chatId) {
        return switch (language) {
            case CYRILLIC -> List.of(
                    new Button("🏬 Дўконларим", shopWebappUrl),
                    new Button("📦 Маҳсулотлар", ButtonType.TEXT),
                    new Button("🛒 Буюртмалар", orderWebappUrl + "/" + chatId),
                    new Button("🖥 Қурилмалар", ButtonType.TEXT),
                    new Button("👤 Ҳисобим", ButtonType.TEXT),
//                    new Button("🔁 Обунани янгилаш", ButtonType.TEXT),
                    new Button("💰 Молия", ButtonType.TEXT),
                    new Button("📊 Статистика", ButtonType.TEXT),
                    new Button("💬 Шархлар", ButtonType.TEXT),
                    new Button("⚙️ Созламалар", ButtonType.TEXT),
                    new Button("📞 Ёрдам", ButtonType.TEXT),
                    new Button("⛔ Чиқиш", ButtonType.TEXT)
            );

            case RUSSIAN -> List.of(
                    new Button("🏬 Мои магазины", shopWebappUrl),
                    new Button("📦 Товары", ButtonType.TEXT),
                    new Button("🛒 Заказы", orderWebappUrl + "/" + chatId),
                    new Button("🖥 Устройства", ButtonType.TEXT),
                    new Button("👤 Мой аккаунт", ButtonType.TEXT),
//                    new Button("🔁 Продлить подписку", ButtonType.TEXT),
                    new Button("💰 Финансы", ButtonType.TEXT),
                    new Button("📊 Статистика", ButtonType.TEXT),
                    new Button("💬 Отзывы", ButtonType.TEXT),
                    new Button("⚙️ Настройки", ButtonType.TEXT),
                    new Button("📞 Поддержка", ButtonType.TEXT),
                    new Button("⛔ Выйти", ButtonType.TEXT)
            );

            case ENGLISH -> List.of(
                    new Button("🏬 My shops", shopWebappUrl),
                    new Button("📦 Products", ButtonType.TEXT),
                    new Button("🛒 Orders", orderWebappUrl + "/" + chatId),
                    new Button("🖥 Devices", ButtonType.TEXT),
                    new Button("👤 My account", ButtonType.TEXT),
//                    new Button("🔁 Renew subscription", ButtonType.TEXT),
                    new Button("💰 Finance", ButtonType.TEXT),
                    new Button("📊 Statistics", ButtonType.TEXT),
                    new Button("💬 Reviews", ButtonType.TEXT),
                    new Button("⚙️ Settings", ButtonType.TEXT),
                    new Button("📞 Support", ButtonType.TEXT),
                    new Button("⛔ Logout", ButtonType.TEXT)
            );

            default -> List.of(
                    new Button("🏬 Do‘konlarim", shopWebappUrl),
                    new Button("📦 Mahsulotlar", ButtonType.TEXT),
                    new Button("🛒 Buyurtmalar", orderWebappUrl + "/" + chatId),
                    new Button("🖥 Qurilmalar", ButtonType.TEXT),
                    new Button("👤 Hisobim", ButtonType.TEXT),
//                    new Button("🔁 Obunani yangilash", ButtonType.TEXT),
                    new Button("💰 Moliya", ButtonType.TEXT),
                    new Button("📊 Statistika", ButtonType.TEXT),
                    new Button("💬 Sharhlar", ButtonType.TEXT),
                    new Button("⚙️ Sozlamalar", ButtonType.TEXT),
                    new Button("📞 Yordam", ButtonType.TEXT),
                    new Button("⛔ Chiqish", ButtonType.TEXT)
            );
        };
    }*/
    public List<Button> menu(Language language, String shopWebappUrl, Long chatId) {
        return switch (language) {

            case CYRILLIC -> List.of(
                    new Button("🏬 Дўконларим", shopWebappUrl),
                    new Button("📦 Маҳсулотлар", ButtonType.TEXT),
                    new Button("🛒 Буюртмалар", orderWebappUrl + "/" + chatId),
                    new Button("🖥 Қурилмалар", ButtonType.TEXT),
                    new Button("👤 Ҳисобим", ButtonType.TEXT),
                    new Button("💳 Тўловларим", paymentWebbAppUrl + "/" + chatId), // ✅ NEW
                    new Button("💰 Молия", this.financeWebbAppUrl + "/" + chatId),
//                    new Button("📊 Статистика", ButtonType.TEXT),
                    new Button("💬 Шархлар", commentaryWebappUrl + "/" + chatId),
                    new Button("⚙️ Созламалар", ButtonType.TEXT),
                    new Button("📞 Ёрдам", ButtonType.TEXT),
                    new Button("⛔ Чиқиш", ButtonType.TEXT)
            );

            case RUSSIAN -> List.of(
                    new Button("🏬 Мои магазины", shopWebappUrl),
                    new Button("📦 Товары", ButtonType.TEXT),
                    new Button("🛒 Заказы", orderWebappUrl + "/" + chatId),
                    new Button("🖥 Устройства", ButtonType.TEXT),
                    new Button("👤 Мой аккаунт", ButtonType.TEXT),
                    new Button("💳 Мои платежи", paymentWebbAppUrl + "/" + chatId),
                    new Button("💰 Финансы", this.financeWebbAppUrl + "/" + chatId),
//                    new Button("📊 Статистика", ButtonType.TEXT),
                    new Button("💬 Отзывы", commentaryWebappUrl + "/" + chatId),
                    new Button("⚙️ Настройки", ButtonType.TEXT),
                    new Button("📞 Поддержка", ButtonType.TEXT),
                    new Button("⛔ Выйти", ButtonType.TEXT)
            );

            case ENGLISH -> List.of(
                    new Button("🏬 My shops", shopWebappUrl),
                    new Button("📦 Products", ButtonType.TEXT),
                    new Button("🛒 Orders", orderWebappUrl + "/" + chatId),
                    new Button("🖥 Devices", ButtonType.TEXT),
                    new Button("👤 My account", ButtonType.TEXT),
                    new Button("💳 My payments", paymentWebbAppUrl + "/" + chatId), // ✅ NEW
                    new Button("💰 Finance", this.financeWebbAppUrl + "/" + chatId),
//                    new Button("📊 Statistics", ButtonType.TEXT),
                    new Button("💬 Reviews", commentaryWebappUrl + "/" + chatId),
                    new Button("⚙️ Settings", ButtonType.TEXT),
                    new Button("📞 Support", ButtonType.TEXT),
                    new Button("⛔ Logout", ButtonType.TEXT)
            );


            default -> List.of(
                    new Button("🏬 Do‘konlarim", shopWebappUrl),
                    new Button("📦 Mahsulotlar", ButtonType.TEXT),
                    new Button("🛒 Buyurtmalar", orderWebappUrl + "/" + chatId),
                    new Button("🖥 Qurilmalar", ButtonType.TEXT),
                    new Button("👤 Hisobim", ButtonType.TEXT),
                    new Button("💳 To‘lovlarim", paymentWebbAppUrl + "/" + chatId), // ✅ NEW
                    new Button("💰 Moliya", this.financeWebbAppUrl + "/" + chatId),
//                    new Button("📊 Statistika", ButtonType.TEXT),
                    new Button("💬 Sharhlar", commentaryWebappUrl + "/" + chatId),
                    new Button("⚙️ Sozlamalar", ButtonType.TEXT),
                    new Button("📞 Yordam", ButtonType.TEXT),
                    new Button("⛔ Chiqish", ButtonType.TEXT)
            );
        };
    }


    public List<Button> requestLang() {
        ButtonType type = ButtonType.TEXT;
        return List.of(
                new Button("🇺🇿 O'zbekcha", type),
                new Button("🇺🇿 Ўзбекча", type),
                new Button("🇷🇺 Русский", type),
                new Button("🇬🇧 English", type)
        );
    }

    public List<Button> logoutAllBtn(Language language) {
        ButtonType type = ButtonType.TEXT;

        return List.of(
                new Button(switch (language) {
                    case UZBEK -> "📱🚫 Barcha qurilmalarni chiqarish";
                    case CYRILLIC -> "📱🚫 Барча қурилмалардан чиқариш";
                    case RUSSIAN -> "📱🚫 Выйти со всех устройств";
                    default -> "📱🚫 Log out from all devices";
                }, type),
                new Button(switch (language) {
                    case UZBEK -> "⬅️ Orqaga";
                    case CYRILLIC -> "⬅️ Орқага";
                    case RUSSIAN -> "⬅️ Назад";
                    default -> "⬅️ Back";
                }, type)
        );
    }

    public List<Button> changeLanguageAndShareContact(Language language) {
        return switch (language) {
            case CYRILLIC -> List.of(
                    new Button("📞 Контактни улашиш", ButtonType.CONTACT),
                    new Button("🔄 Тилни ўзгартириш", ButtonType.TEXT)
            );
            case RUSSIAN -> List.of(
                    new Button("📞 Поделиться контактом", ButtonType.CONTACT),
                    new Button("🔄 Изменить язык", ButtonType.TEXT)
            );
            case ENGLISH -> List.of(
                    new Button("📞 Share contact", ButtonType.CONTACT),
                    new Button("🔄 Change language", ButtonType.TEXT)
            );
            default -> List.of(
                    new Button("📞 Kontaktni ulashish", ButtonType.CONTACT),
                    new Button("🔄 Tilni o'zgartirish", ButtonType.TEXT)
            );
        };
    }

    public List<Button> requestSellerLoginAndChangeLanguage(Language language, String webappUrl) {
        return switch (language) {
            case CYRILLIC -> List.of(
                    createWebAppButton("Шахсни тасдиқлаш", webappUrl)
            );
            case RUSSIAN -> List.of(
                    createWebAppButton("Подтвердить личность", webappUrl)
            );
            case ENGLISH -> List.of(
                    createWebAppButton("Verify Identity", webappUrl)
            );
            default -> List.of(
                    createWebAppButton("Shaxsni tasdiqlash", webappUrl)
            );
        };
    }

    public List<Button> requestSellerLoginAndChangeLanguage1(Language language, String webappUrl) {
        return switch (language) {
            case CYRILLIC -> List.of(
                    createWebAppButton("Шахсни тасдиқлаш", webappUrl)
            );
            case RUSSIAN -> List.of(
                    createWebAppButton("Подтвердить личность", webappUrl)
            );
            case ENGLISH -> List.of(
                    createWebAppButton("Verify Identity", webappUrl)
            );
            default -> List.of(
                    createWebAppButton("Shaxsni tasdiqlash", webappUrl)
            );
        };
    }

    private Button createWebAppButton(String text, String webappUrl) {
        Button b = new Button();
        b.setText(text);
        b.setType(ButtonType.WEBAPP);
        b.setWebappUrl(webappUrl);
        return b;
    }

    private Button createTextButton(String text) {
        Button b = new Button();
        b.setText(text);
        b.setType(ButtonType.TEXT);
        return b;
    }

    public List<Button> subscriptionExpiredBtn(Language language) {
        String myAccount = switch (language) {
            case UZBEK -> "👤 Hisobim";
            case CYRILLIC -> "👤 Ҳисобим";
            case RUSSIAN -> "👤 Мой аккаунт";
            default -> "👤 My account";
        };
        String topUp = switch (language) {
            case UZBEK -> "💳 Hisobni to‘ldirish";
            case CYRILLIC -> "💳 Ҳисобни тўлдириш";
            case RUSSIAN -> "💳 Пополнить счёт";
            default -> "💳 Top up balance";
        };
        String renew = switch (language) {
            case UZBEK -> "🔄 Obunani yangilash";
            case CYRILLIC -> "🔄 Обунани янгилаш";
            case RUSSIAN -> "🔄 Обновить подписку";
            default -> "🔄 Renew subscription";
        };

        return List.of(
                new Button(myAccount, ButtonType.TEXT),
                new Button(topUp, ButtonType.TEXT),
                new Button(renew, ButtonType.TEXT)
        );
    }

    public List<Button> notShops(Language language, String webappUrl) {
        String text;

        switch (language) {
            case UZBEK -> text = "Do‘konlar bo‘limi";
            case CYRILLIC -> text = "Дўконлар бўлими";
            case RUSSIAN -> text = "Раздел магазинов";
            default -> text = "Shops section";
        }

        Button webAppButton = new Button(text, webappUrl);

        return List.of(webAppButton);
    }

    public List<Button> backBtn(Language language) {
        ButtonType type = ButtonType.TEXT;

        String text = switch (language) {
            case UZBEK -> "⬅️ Orqaga";
            case CYRILLIC -> "⬅️ Орқага";
            case RUSSIAN -> "⬅️ Назад";
            case ENGLISH -> "⬅️ Back";
        };

        return List.of(new Button(text, type));
    }

    public List<Button> payment(Language language) {
        ButtonType type = ButtonType.TEXT;
        String backText = switch (language) {
            case UZBEK -> "⬅️ Orqaga";
            case CYRILLIC -> "⬅️ Орқага";
            case RUSSIAN -> "⬅️ Назад";
            case ENGLISH -> "⬅️ Back";
        };

        String paidText = switch (language) {
            case UZBEK -> "✅ To'lov qildim";
            case CYRILLIC -> "✅ Тўлов қилдим";
            case RUSSIAN -> "✅ Оплатил";
            case ENGLISH -> "✅ I paid";
        };

        return List.of(
                new Button(paidText, type),
                new Button(backText, type)
        );
    }


    public List<Button> selectSubscriptionPlans(Language language, List<SubscriptionPlanType> planNames) {
        List<Button> buttons = new ArrayList<>();
        for (SubscriptionPlanType plan : planNames) {
            String text = switch (language) {
                case UZBEK -> getUzbekPlanText(plan);
                case RUSSIAN -> getRussianPlanText(plan);
                case ENGLISH -> getEnglishPlanText(plan);
                case CYRILLIC -> getCyrillicPlanText(plan);
            };
            buttons.add(new Button(text, ButtonType.TEXT));
        }

        buttons.add(backBtn(language).get(0));

        return buttons;
    }

    // ================== Text helper methods ==================

    private String getUzbekPlanText(SubscriptionPlanType plan) {
        return switch (plan) {
            case MONTH_1 -> "📅 1 oylik obuna ✨";
            case MONTH_2 -> "📅 2 oylik obuna ✨";
            case MONTH_3 -> "📅 3 oylik obuna ✨";
            case MONTH_6 -> "📅 6 oylik obuna ✨";
            case MONTH_12 -> "📅 12 oylik obuna ✨";
            case TRIAL -> "🆓 Sinov muddati ⏳";
            case ACTIVE_ALWAYS -> "♾️ Umrbod obuna 💎";
            case EXPIRED -> "❌ Muddati tugagan ⚠️";
        };
    }

    private String getRussianPlanText(SubscriptionPlanType plan) {
        return switch (plan) {
            case MONTH_1 -> "📅 1 месяц подписки ✨";
            case MONTH_2 -> "📅 2 месяца подписки ✨";
            case MONTH_3 -> "📅 3 месяца подписки ✨";
            case MONTH_6 -> "📅 6 месяцев подписки ✨";
            case MONTH_12 -> "📅 12 месяцев подписки ✨";
            case TRIAL -> "🆓 Пробный период ⏳";
            case ACTIVE_ALWAYS -> "♾️ Бессрочная подписка 💎";
            case EXPIRED -> "❌ Срок истек ⚠️";
        };
    }

    private String getEnglishPlanText(SubscriptionPlanType plan) {
        return switch (plan) {
            case MONTH_1 -> "📅 1 month subscription ✨";
            case MONTH_2 -> "📅 2 months subscription ✨";
            case MONTH_3 -> "📅 3 months subscription ✨";
            case MONTH_6 -> "📅 6 months subscription ✨";
            case MONTH_12 -> "📅 12 months subscription ✨";
            case TRIAL -> "🆓 Trial period ⏳";
            case ACTIVE_ALWAYS -> "♾️ Lifetime subscription 💎";
            case EXPIRED -> "❌ Expired ⚠️";
        };
    }

    private String getCyrillicPlanText(SubscriptionPlanType plan) {
        return switch (plan) {
            case MONTH_1 -> "📅 1 ойлик обуна ✨";
            case MONTH_2 -> "📅 2 ойлик обуна ✨";
            case MONTH_3 -> "📅 3 ойлик обуна ✨";
            case MONTH_6 -> "📅 6 ойлик обуна ✨";
            case MONTH_12 -> "📅 12 ойлик обуна ✨";
            case TRIAL -> "🆓 Синов муддати ⏳";
            case ACTIVE_ALWAYS -> "♾️ Умрбод обуна 💎";
            case EXPIRED -> "❌ Муддати тугаган ⚠️";
        };
    }


    public List<Button> back(Language language){

        return switch (language) {
            case UZBEK -> List.of(
//                    new Button("📞 Telefon raqamini o‘zgartirish", ButtonType.TEXT),
//                    new Button("📛 To‘liq ismni o‘zgartirish", ButtonType.TEXT),
                    new Button("⬅️ Orqaga", ButtonType.TEXT)
            );

            case CYRILLIC -> List.of(
//                    new Button("📞 Телефон рақамини ўзгартириш", ButtonType.TEXT),
//                    new Button("📛 Тўлиқ исмни ўзгартириш", ButtonType.TEXT),
                    new Button("⬅️ Орқага", ButtonType.TEXT)
            );

            case RUSSIAN -> List.of(
//                    new Button("📞 Изменить номер телефона", ButtonType.TEXT),
//                    new Button("📛 Изменить полное имя", ButtonType.TEXT),
                    new Button("⬅️ Назад", ButtonType.TEXT)
            );

            case ENGLISH -> List.of(
//                    new Button("📞 Change phone number", ButtonType.TEXT),
//                    new Button("📛 Change full name", ButtonType.TEXT),
                    new Button("⬅️ Back", ButtonType.TEXT)
            );
        };
    }
    public List<Button> changeProfile(Language language) {

        return switch (language) {
            case UZBEK -> List.of(
                    new Button("📞 Telefon raqamini o‘zgartirish", ButtonType.TEXT),
                    new Button("📛 To‘liq ismni o‘zgartirish", ButtonType.TEXT),
                    new Button("⬅️ Orqaga", ButtonType.TEXT)
            );

            case CYRILLIC -> List.of(
                    new Button("📞 Телефон рақамини ўзгартириш", ButtonType.TEXT),
                    new Button("📛 Тўлиқ исмни ўзгартириш", ButtonType.TEXT),
                    new Button("⬅️ Орқага", ButtonType.TEXT)
            );

            case RUSSIAN -> List.of(
                    new Button("📞 Изменить номер телефона", ButtonType.TEXT),
                    new Button("📛 Изменить полное имя", ButtonType.TEXT),
                    new Button("⬅️ Назад", ButtonType.TEXT)
            );

            case ENGLISH -> List.of(
                    new Button("📞 Change phone number", ButtonType.TEXT),
                    new Button("📛 Change full name", ButtonType.TEXT),
                    new Button("⬅️ Back", ButtonType.TEXT)
            );
        };
    }

    public List<Button> settings(Language language) {

        return switch (language) {
            case UZBEK -> List.of(
                    new Button("🌐 Tilni o‘zgartirish", ButtonType.TEXT),
                    new Button("👤 Profilni o‘zgartirish", ButtonType.TEXT),
                    new Button("💳 Kartani o‘zgartirish", ButtonType.TEXT),
                    new Button("🔄 Obunani yangilash", ButtonType.TEXT),
                    new Button("⬅️ Orqaga qaytish", ButtonType.TEXT)
            );

            case CYRILLIC -> List.of(
                    new Button("🌐 Тилни ўзгартириш", ButtonType.TEXT),
                    new Button("👤 Профилни ўзгартириш", ButtonType.TEXT),
                    new Button("💳 Картани ўзгартириш", ButtonType.TEXT),
                    new Button("🔄 Обунани янгилаш", ButtonType.TEXT),
                    new Button("⬅️ Орқага қайтиш", ButtonType.TEXT)
            );

            case RUSSIAN -> List.of(
                    new Button("🌐 Изменить язык", ButtonType.TEXT),
                    new Button("👤 Изменить профиль", ButtonType.TEXT),
                    new Button("💳 Изменить карту", ButtonType.TEXT),
                    new Button("🔄 Обновить подписку", ButtonType.TEXT),
                    new Button("⬅️ Назад", ButtonType.TEXT)
            );

            case ENGLISH -> List.of(
                    new Button("🌐 Change language", ButtonType.TEXT),
                    new Button("👤 Edit profile", ButtonType.TEXT),
                    new Button("💳 Change card", ButtonType.TEXT),
                    new Button("🔄 Renew subscription", ButtonType.TEXT),
                    new Button("⬅️ Back", ButtonType.TEXT)
            );
        };
    }


    public List<Button> editCard(Language language) {

        return switch (language) {
            case UZBEK -> List.of(
                    new Button("🖼 Karta rasmini o‘zgartirish", ButtonType.TEXT),
                    new Button("👤 Karta egasini o‘zgartirish", ButtonType.TEXT),
                    new Button("💳 Karta raqamini o‘zgartirish", ButtonType.TEXT),
                    new Button("⬅️ Orqaga", ButtonType.TEXT)
            );

            case CYRILLIC -> List.of(
                    new Button("🖼 Карта расмини ўзгартириш", ButtonType.TEXT),
                    new Button("👤 Карта эгасини ўзгартириш", ButtonType.TEXT),
                    new Button("💳 Карта рақамини ўзгартириш", ButtonType.TEXT),
                    new Button("⬅️ Орқага", ButtonType.TEXT)
            );

            case RUSSIAN -> List.of(
                    new Button("🖼 Изменить изображение карты", ButtonType.TEXT),
                    new Button("👤 Изменить владельца карты", ButtonType.TEXT),
                    new Button("💳 Изменить номер карты", ButtonType.TEXT),
                    new Button("⬅️ Назад", ButtonType.TEXT)
            );

            case ENGLISH -> List.of(
                    new Button("🖼 Change card image", ButtonType.TEXT),
                    new Button("👤 Change card holder", ButtonType.TEXT),
                    new Button("💳 Change card number", ButtonType.TEXT),
                    new Button("⬅️ Back", ButtonType.TEXT)
            );
        };
    }

}
