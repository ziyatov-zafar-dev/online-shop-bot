package uz.zafar.onlineshoptelegrambot.bot.msg;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.AdminCard;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.common.Discount;
import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.DiscountType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.dto.gson.IpWhoIsResponseDto;
import uz.zafar.onlineshoptelegrambot.service.GsonService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class SellerMsg {


    private final GsonService gsonService;

    public SellerMsg(GsonService gsonService) {
        this.gsonService = gsonService;
    }

    public String welcomeCyr(String fullName) {
        return "🇺🇿👋 Ассалому алайкум, <b>" + fullName + "</b>!\n" +
                "🛒 Сотувчи бўлимига хуш келибсиз!\n\n" +
                "🚀 Бу бўлимда сиз:\n" +
                "• Маҳсулотларингизни осонгина қўшишингиз мумкин\n" +
                "• Тез ва осон сотиш имкониятига эга бўласиз\n" +
                "• Максус маркетинг воситалари билан мижозларни жалб қилишингиз мумкин\n\n" +
                "🌐 Илтимос, о'зингизга керакли тилни танланг:";
    }

    public String wrongBtn(Language language) {
        return switch (language) {
            case UZBEK -> "❌ Iltimos, faqat menyudagi tugmalardan foydalaning!";
            case CYRILLIC -> "❌ Илтимос, фақат менюдаги тугмалардан фойдаланинг!";
            case RUSSIAN -> "❌ Пожалуйста, используйте только кнопки из меню!";
            case ENGLISH -> "❌ Please use only buttons from the menu!";
        };
    }

    public String changedLanguage(Language language) {
        return switch (language) {
            case UZBEK ->
                    "✅ Til muvaffaqiyatli o‘zgartirildi! Siz endi O‘zbekcha interfeysdan foydalanishingiz mumkin.";
            case CYRILLIC ->
                    "✅ Тил муваффақиятли ўзгартирилди! Сиз эндиликда Ўзбекча интерфейсдан фойдаланишингиз мумкин.";
            case RUSSIAN -> "✅ Язык успешно изменен! Теперь вы можете использовать интерфейс на русском языке.";
            case ENGLISH -> "✅ Language changed successfully! You can now use the interface in English.";
            default -> "✅ Til muvaffaqiyatli o‘zgartirildi!";
        };
    }

    public String shareContact(Language language) {
        return switch (language) {
            case CYRILLIC -> "📞 Илтимос, контактингизни улашинг.";
            case RUSSIAN -> "📞 Пожалуйста, поделитесь вашим контактом.";
            case ENGLISH -> "📞 Please share your contact.";
            default -> "📞 Iltimos, kontaktingizni ulashing.";
        };
    }

    public String changeLanguage(Language language) {
        if (language == Language.CYRILLIC) return "\uD83C\uDF10 Илтимос, қуйидаги тиллардан бирини танланг:";
        else if (language == Language.RUSSIAN) return "\uD83C\uDF10 Пожалуйста, выберите один из языков:";
        else if (language == Language.ENGLISH) return "\uD83C\uDF10 Please choose one of the available languages:";
        return "\uD83C\uDF10 Iltimos, quyidagi tillardan birini tanlang:";
    }

    public String savedContact(Language language) {
        return switch (language) {
            case CYRILLIC -> "✅ Контакт муваффақиятли сақланди!";
            case RUSSIAN -> "✅ Контакт успешно сохранен!";
            case ENGLISH -> "✅ Contact saved successfully!";
            default -> "✅ Kontakt muvaffaqiyatli saqlandi!";
        };
    }

    public String requestSellerLogin(Language language) {
        return switch (language) {
            case CYRILLIC -> "🔑 Илтимос, шахсий маълумотларингизни тасдиқланг. Қуйидаги тугмани босиб логин қилинг.";
            case RUSSIAN -> "🔑 Пожалуйста, подтвердите свои личные данные. Нажмите кнопку ниже и войдите в систему.";
            case ENGLISH -> "🔑 Please verify your personal information. Click the button below to login.";
            default -> "🔑 Iltimos, shaxsiy ma’lumotlaringizni tasdiqlang. Quyidagi tugmani bosing va login qiling.";
        };
    }

    public String menu(Language language) {
        return switch (language) {
            case CYRILLIC -> "\uD83C\uDFE0 Асосий менюга хуш келибсиз!";
            case RUSSIAN -> "\uD83C\uDFE0 Добро пожаловать в главное меню!";
            case ENGLISH -> "\uD83C\uDFE0 Welcome to the main menu!";
            default -> "\uD83C\uDFE0 Asosiy menyudasiz";
        };
    }

    public String myDevices(List<BotSeller> users, Language language, Long chatId) throws Exception {


        if (users == null || users.isEmpty()) {
            return switch (language) {
                case UZBEK -> "📭 Sizda hozircha ulangan qurilmalar mavjud emas.";
                case CYRILLIC -> "📭 Сизда ҳозирча уланган қурилмалар мавжуд эмас.";
                case RUSSIAN -> "📭 У вас пока нет подключённых устройств.";
                case ENGLISH -> "📭 You have no connected devices yet.";
            };
        }

        StringBuilder sb = new StringBuilder();

        // ===== HEADER =====
        sb.append(
                switch (language) {
                    case UZBEK -> "🖥 Sizning ulangan qurilmalaringiz:\n\n";
                    case CYRILLIC -> "🖥 Сизнинг уланган қурилмаларингиз:\n\n";
                    case RUSSIAN -> "🖥 Ваши подключённые устройства:\n\n";
                    case ENGLISH -> "🖥 Your connected devices:\n\n";
                }
        );

        // ===== 1. CURRENT DEVICE =====
        BotSeller currentDevice = null;
        for (BotSeller user : users) {
            if (user.getChatId() != null && user.getChatId().equals(chatId)) {
                currentDevice = user;
                break;
            }
        }

        int index = 1;

        if (currentDevice != null) {
            appendDevice(sb, currentDevice, language, index++, true);
        }

        // ===== 2. OTHER DEVICES =====
        for (BotSeller user : users) {
            if (currentDevice != null && user == currentDevice) {
                continue;
            }
            appendDevice(sb, user, language, index++, false);
        }

        return sb.toString();
    }


    private void appendDevice(StringBuilder sb,
                              BotSeller user,
                              Language language,
                              int index,
                              boolean isCurrentDevice) throws Exception {

        // IP manzilidan ma'lumot olish
        IpWhoIsResponseDto location = gsonService.getLocation(user.getIp()).getData();
        String nameText = getAboutUser(language, user);

        sb.append(index).append(". ")
                .append("👤 ").append(nameText);

        if (isCurrentDevice) {
            sb.append(
                    switch (language) {
                        case UZBEK -> "  ✅ (Mazkur qurilma)";
                        case CYRILLIC -> "  ✅ (Мазкур қурилма)";
                        case RUSSIAN -> "  ✅ (Текущее устройство)";
                        case ENGLISH -> "  ✅ (Current device)";
                    }
            );
        }

        sb.append("\n");

        // Username
        sb.append(
                        switch (language) {
                            case UZBEK, ENGLISH -> "🔗 Username: ";
                            case CYRILLIC -> "🔗 Юзернейм: ";
                            case RUSSIAN -> "🔗 Имя пользователя: ";
                        }
                ).append(user.getUsername() != null ? "@" + user.getUsername() : "-")
                .append("\n");

        // Chat ID
        sb.append(
                        switch (language) {
                            case UZBEK, RUSSIAN, ENGLISH -> "🆔 ID: ";
                            case CYRILLIC -> "🆔 Чат ID: ";
                        }
                ).append(user.getChatId())
                .append("\n");

        // Full address in one line (IP ko'rsatilmaydi)
        if (location != null && location.isSuccess()) {
            String fullAddress = buildFullAddress(location, language);
            sb.append("📍 ")
                    .append(
                            switch (language) {
                                case UZBEK -> "Manzil: ";
                                case CYRILLIC -> "Манзил: ";
                                case RUSSIAN -> "Местоположение: ";
                                case ENGLISH -> "Location: ";
                            }
                    )
                    .append(fullAddress)
                    .append("\n");
        } else {
            sb.append("📍 ")
                    .append(
                            switch (language) {
                                case UZBEK -> "Manzil: Ma'lumot olinmadi";
                                case CYRILLIC -> "Манзил: Маълумот олинмади";
                                case RUSSIAN -> "Местоположение: Не получено";
                                case ENGLISH -> "Location: Not available";
                            }
                    )
                    .append("\n");
        }

        // Internet Provider (agar mavjud bo'lsa)
        if (location != null && location.getConnection() != null &&
                location.getConnection().getIsp() != null &&
                !location.getConnection().getIsp().isEmpty()) {
            sb.append("📡 ")
                    .append(
                            switch (language) {
                                case UZBEK -> "Internet provayderi: ";
                                case CYRILLIC -> "Интернет провайдери: ";
                                case RUSSIAN -> "Интернет-провайдер: ";
                                case ENGLISH -> "Internet provider: ";
                            }
                    )
                    .append(location.getConnection().getIsp())
                    .append("\n");
        }

        // Connected time
        sb.append(
                        switch (language) {
                            case UZBEK -> "⏱ Ulangan vaqti: ";
                            case CYRILLIC -> "⏱ Уланган вақти: ";
                            case RUSSIAN -> "⏱ Время подключения: ";
                            case ENGLISH -> "⏱ Connected at: ";
                        }
                ).append(formatDateTime(user.getConnectingToSellerAt(), language))
                .append("\n\n");
    }

    private String buildFullAddress(IpWhoIsResponseDto location, Language language) {
        StringBuilder address = new StringBuilder();

        if (location.getFlag() != null && location.getFlag().getEmoji() != null) {
            address.append(location.getFlag().getEmoji()).append(" ");
        }

        address.append(location.getCountry());

        if (location.getCity() != null && !location.getCity().isEmpty() &&
                !location.getCity().equalsIgnoreCase("Unknown")) {
            address.append(", ").append(location.getCity());
        }

        if (location.getTimezone() != null && location.getTimezone().getAbbr() != null) {
            address.append(" (").append(location.getTimezone().getAbbr()).append(")");
        }

        return address.toString();
    }


    private static @NonNull String getAboutUser(Language language, BotSeller user) {
        String fullName = ((user.getFirstName() != null ? user.getFirstName() : "")
                + " "
                + (user.getLastName() != null ? user.getLastName() : "")).trim();

        return fullName.isEmpty()
                ? switch (language) {
            case UZBEK -> "Nomaʼlum foydalanuvchi";
            case CYRILLIC -> "Номаълум фойдаланувчи";
            case RUSSIAN -> "Неизвестный пользователь";
            case ENGLISH -> "Unknown user";
        }
                : fullName;
    }

    private String formatDateTime(LocalDateTime time, Language language) {
        if (time == null) {
            return switch (language) {
                case UZBEK -> "Nomaʼlum";
                case CYRILLIC -> "Номаълум";
                case RUSSIAN -> "Неизвестно";
                case ENGLISH -> "Unknown";
            };
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return time.format(formatter);
    }

    public String warningDevice(Language language, IpWhoIsResponseDto location, BotSeller user) {

        String fullAddress = getFullAddress(location, language);
        String userFullName = getUserFullName(user, language);

        String username = user.getUsername() != null
                ? "@" + user.getUsername()
                : switch (language) {
            case UZBEK -> "username mavjud emas";
            case CYRILLIC -> "юзернейм мавжуд эмас";
            case RUSSIAN -> "username отсутствует";
            case ENGLISH -> "username not available";
        };

        return switch (language) {

            case UZBEK -> "⚠️ *Yangi qurilmadan kirish aniqlandi!*\n\n" +
                    "👤 *Foydalanuvchi:* " + userFullName + "\n" +
                    "🔗 *Username:* " + username + "\n\n" +
                    "📍 *Joylashuv:* " + fullAddress;

            case CYRILLIC -> "⚠️ *Янги қурилмадан кириш аниқланди!*\n\n" +
                    "👤 *Фойдаланувчи:* " + userFullName + "\n" +
                    "🔗 *Юзернейм:* " + username + "\n\n" +
                    "📍 *Жойлашув:* " + fullAddress;

            case RUSSIAN -> "⚠️ *Обнаружен вход с нового устройства!*\n\n" +
                    "👤 *Пользователь:* " + userFullName + "\n" +
                    "🔗 *Username:* " + username + "\n\n" +
                    "📍 *Местоположение:* " + fullAddress;

            case ENGLISH -> "⚠️ *New device login detected!*\n\n" +
                    "👤 *User:* " + userFullName + "\n" +
                    "🔗 *Username:* " + username + "\n\n" +
                    "📍 *Location:* " + fullAddress;
        };
    }

    private String getFullAddress(IpWhoIsResponseDto location, Language language) {

        if (location == null || !location.isSuccess()) {
            return switch (language) {
                case UZBEK -> "❌ Joylashuv aniqlanmadi";
                case CYRILLIC -> "❌ Жойлашув аниқланмади";
                case RUSSIAN -> "❌ Местоположение не определено";
                case ENGLISH -> "❌ Location not detected";
            };
        }

        StringBuilder address = new StringBuilder();

        if (location.getFlag() != null && location.getFlag().getEmoji() != null) {
            address.append(location.getFlag().getEmoji()).append(" ");
        }

        address.append("*").append(location.getCountry()).append("*");

        boolean hasCity = location.getCity() != null && !location.getCity().isBlank()
                && !"Unknown".equalsIgnoreCase(location.getCity());

        boolean hasRegion = location.getRegion() != null && !location.getRegion().isBlank()
                && !location.getRegion().equalsIgnoreCase(location.getCity());

        if (hasCity || hasRegion) {
            address.append(" (");
            if (hasCity) {
                address.append(location.getCity());
                if (hasRegion) {
                    address.append(", ").append(location.getRegion());
                }
            } else {
                address.append(location.getRegion());
            }
            address.append(")");
        }

        if (location.getTimezone() != null && location.getTimezone().getAbbr() != null) {
            address.append(" 🕒 ").append(location.getTimezone().getAbbr());
        }

        return address.toString();
    }

    private String getUserFullName(BotSeller user, Language language) {

        String firstName = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String lastName = user.getLastName() != null ? user.getLastName().trim() : "";

        if (firstName.isEmpty() && lastName.isEmpty()) {
            return switch (language) {
                case UZBEK -> "👤 Nomaʼlum foydalanuvchi";
                case CYRILLIC -> "👤 Номаълум фойдаланувчи";
                case RUSSIAN -> "👤 Неизвестный пользователь";
                case ENGLISH -> "👤 Unknown user";
            };
        }
        return ("*" + firstName + " " + lastName + "*").trim();
    }

    public String requestAddShopPlease(Language language) {
        return switch (language) {
            case UZBEK -> "Sizda hozircha do‘kon mavjud emas.\n" +
                    "Davom etish uchun avval do‘kon qo‘shishingiz kerak.";

            case CYRILLIC -> "Сизда ҳозирча дўкон мавжуд эмас.\n" +
                    "Давом этиш учун аввал дўкон қўшишингиз керак.";

            case RUSSIAN -> "У вас пока нет магазина.\n" +
                    "Чтобы продолжить, сначала добавьте магазин.";

            case ENGLISH -> "You don't have a shop yet.\n" +
                    "To continue, please add a shop first.";
        };
    }

    public String yourProductLists(Language language) {
        return switch (language) {
            case UZBEK -> "Sizning mahsulotlaringiz ro'yxati";
            case CYRILLIC -> "Сизнинг маҳсулотларингиз рўйхати";
            case RUSSIAN -> "Список ваших товаров";
            case ENGLISH -> "Your product list";
        };
    }

    public String notFoundProductMsg(Language language) {
        return switch (language) {
            case UZBEK -> "📦❌ Ushbu mahsulot topilmadi";
            case CYRILLIC -> "📦❌ Ушбу маҳсулот топилмади";
            case RUSSIAN -> "📦❌ Данный товар не найден";
            case ENGLISH -> "📦❌ This product was not found";
        };
    }


    /*public String aboutProduct(Language language,
                               Product product,
                               List<ProductType> types,
                               String referralLink) {
        int typeCount = types.size();

        String productName;
        String productDescription;
        String shareText;

        switch (language) {
            case RUSSIAN -> {
                productName = product.getNameRu();
                productDescription = product.getDescriptionRu();
                shareText = "Поделитесь по этой ссылке";
            }
            case CYRILLIC -> {
                productName = product.getNameCyr();
                productDescription = product.getDescriptionCyr();
                shareText = "Ушбу ҳавола орқали улашинг";
            }
            case ENGLISH -> {
                productName = product.getNameEn();
                productDescription = product.getDescriptionEn();
                shareText = "Share using this link";
            }
            default -> { // UZBEK
                productName = product.getNameUz();
                productDescription = product.getDescriptionUz();
                shareText = "Ushbu havola orqali ulashing";
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("🛍 ").append(productName).append("\n\n");

        if (productDescription != null && !productDescription.isBlank()) {
            sb.append(productDescription).append("\n\n");
        }

        sb.append("📦 ");

        sb.append(switch (language) {
            case RUSSIAN -> "Вариантов:";
            case CYRILLIC -> "Вариантлар сони:";
            case ENGLISH -> "Variants:";
            default -> "Variantlar soni:";
        }).append(" ").append(typeCount).append("\n\n");

        int index = 1;
        for (ProductType type : types) {

            if (Boolean.TRUE.equals(type.getDeleted())) continue;

            String typeName = switch (language) {
                case RUSSIAN -> type.getNameRu();
                case CYRILLIC -> type.getNameCyr();
                case ENGLISH -> type.getNameEn();
                default -> type.getNameUz();
            };

            sb.append(index++).append(") ")
                    .append(typeName)
                    .append("\n");

            sb.append(switch (language) {
                case RUSSIAN -> "   💰 Цена: ";
                case CYRILLIC -> "   💰 Нарх: ";
                case ENGLISH -> "   💰 Price: ";
                default -> "   💰 Narx: ";
            }).append(type.getPrice()).append("\n");

            sb.append(switch (language) {
                case RUSSIAN -> "   📊 Количество: ";
                case CYRILLIC -> "   📊 Сони: ";
                case ENGLISH -> "   📊 Stock: ";
                default -> "   📊 Soni: ";
            }).append(type.getStock()).append("\n\n");
        }

        if (referralLink != null && !referralLink.isBlank()) {
            sb.append("🔗 ")
                    .append(shareText)
                    .append("\n")
                    .append(referralLink);
        }

        return sb.toString();
    }*/

    public String aboutProduct(Language language,
                               Product product,
                               List<ProductType> types,
                               String referralLink) {

        int typeCount = types.size();

        String productName;
        String productDescription;
        String shareText;

        switch (language) {
            case RUSSIAN -> {
                productName = product.getNameRu();
                productDescription = product.getDescriptionRu();
                shareText = "Поделитесь по этой ссылке";
            }
            case CYRILLIC -> {
                productName = product.getNameCyr();
                productDescription = product.getDescriptionCyr();
                shareText = "Ушбу ҳавола орқали улашинг";
            }
            case ENGLISH -> {
                productName = product.getNameEn();
                productDescription = product.getDescriptionEn();
                shareText = "Share using this link";
            }
            default -> { // UZBEK
                productName = product.getNameUz();
                productDescription = product.getDescriptionUz();
                shareText = "Ushbu havola orqali ulashing";
            }
        }

        // DecimalFormat bilan bo'sh joyli minglik ajratgich
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' '); // bo'sh joy
        symbols.setDecimalSeparator('.');  // o'nlik nuqta, kerak bo'lsa
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        df.setGroupingUsed(true);

        StringBuilder sb = new StringBuilder();

        // Mahsulot nomi va tavsifi
        sb.append("🛍 <b>").append(productName).append("</b>\n\n");
        if (productDescription != null && !productDescription.isBlank()) {
            sb.append(productDescription).append("\n\n");
        }

        // Variantlar soni
        sb.append("📦 ").append(switch (language) {
            case RUSSIAN -> "Вариантов:";
            case CYRILLIC -> "Вариантлар сони:";
            case ENGLISH -> "Variants:";
            default -> "Variantlar soni:";
        }).append(" ").append(typeCount).append("\n\n");

        int index = 1;
        for (ProductType type : types) {
            if (Boolean.TRUE.equals(type.getDeleted())) continue;

            String typeName = switch (language) {
                case RUSSIAN -> type.getNameRu();
                case CYRILLIC -> type.getNameCyr();
                case ENGLISH -> type.getNameEn();
                default -> type.getNameUz();
            };

            sb.append(index++).append(") <b>").append(typeName).append("</b>\n");

            BigDecimal price = type.getPrice();
            Discount discount = product.getDiscount();

            if (discount != null) {
                BigDecimal discountedPrice;

                switch (discount.getType()) {
                    case PERCENT ->
                            discountedPrice = price.subtract(price.multiply(discount.getValue()).divide(BigDecimal.valueOf(100)));
                    case FIXED -> discountedPrice = price.subtract(discount.getValue());
                    default -> discountedPrice = price;
                }

                // Chegirma turi va qiymati
                String discountText = switch (discount.getType()) {
                    case PERCENT -> discount.getValue() + "%";
                    case FIXED -> df.format(discount.getValue()) + " sum";
                    default -> "";
                };

                sb.append(switch (language) {
                            case RUSSIAN -> "   💰 Цена: ";
                            case CYRILLIC -> "   💰 Нарх: ";
                            case ENGLISH -> "   💰 Price: ";
                            default -> "   💰 Narx: ";
                        }).append("<s>").append(df.format(price)).append("</s> -> ")
                        .append(df.format(discountedPrice))
                        .append(" (").append(discountText).append(")\n");

            } else {
                sb.append(switch (language) {
                    case RUSSIAN -> "   💰 Цена: ";
                    case CYRILLIC -> "   💰 Нарх: ";
                    case ENGLISH -> "   💰 Price: ";
                    default -> "   💰 Narx: ";
                }).append(df.format(price)).append("\n");
            }

            sb.append(switch (language) {
                case RUSSIAN -> "   📊 Количество: ";
                case CYRILLIC -> "   📊 Сони: ";
                case ENGLISH -> "   📊 Stock: ";
                default -> "   📊 Soni: ";
            }).append(type.getStock()).append("\n\n");
        }

        if (referralLink != null && !referralLink.isBlank()) {
            sb.append("🔗 ").append(shareText).append("\n").append(referralLink);
        }

        return sb.toString();
    }


    public String wrongProductPhoto(Language language) {
        if (language == Language.UZBEK) return "Ushbu mahsulot nomining rasmini olishda xatolik yuz berdi.\n" +
                "Iltimos, mahsulotlarni boshqarish bo‘limiga o‘tib mahsulot rasmini almashtiring.\n";
        else if (language == Language.CYRILLIC) {
            return "Ушбу маҳсулот номининг расмини олишда хатолик юз берди.\n" +
                    "Илтимос, маҳсулотларни бошқариш бўлимига ўтиб маҳсулот расмини алмаштиринг.\n";
        } else if (language == Language.ENGLISH) {
            return "An error occurred while retrieving the image of this product.\n" +
                    "Please go to the product management section and replace the product image.\n";
        }
        return "Произошла ошибка при получении изображения данного товара.\n" +
                "Пожалуйста, перейдите в раздел управления товарами и замените изображение товара.\n";
    }

    public String logoutSeller(Language language) {
        return switch (language) {
            case UZBEK -> "Siz profildan chiqdingiz. Yana kirish uchun /start ni bosing";
            case CYRILLIC -> "Сиз профилдан чиқдингиз. Яна кириш учун /start ни босинг";
            case RUSSIAN -> "Вы вышли из профиля. Для повторного входа нажмите /start";
            default -> "You have logged out. Press /start to log in again";
        };
    }

    public String successAllLogoutDevice(Language language) {
        return switch (language) {
            case UZBEK -> """
                    ✅ Barcha qurilmalardan muvaffaqiyatli chiqarildi!
                    
                    Endi faqat joriy qurilmangiz orqali boshqarishingiz mumkin
                    """;
            case CYRILLIC -> """
                    ✅ Барча қурилмалардан муваффақиятли чиқарилди!
                    
                    Энди фақат жорий қурилмангиз орқали бошқаришингиз мумкин
                    """;
            case RUSSIAN -> """
                    ✅ Успешно вышли со всех устройств!
                    
                    Теперь вы можете управлять только с текущего устройства
                    """;
            default -> """
                    ✅ Successfully logged out from all devices!
                    
                    Now you can only manage from your current device
                    """;
        };
    }

    public String subscriptionExpiredMessage(Language language) {
        return switch (language) {
            case UZBEK -> "⛔ Obuna muddatingiz tugadi.\n\nIltimos, obunangizni yangilang.";
            case CYRILLIC -> "⛔ Обуна муддатингиз тугади.\n\nИлтимос, обунангизни янгиланг.";
            case RUSSIAN -> "⛔ Срок вашей подписки истёк.\n\nПожалуйста, обновите подписку.";
            default -> "⛔ Your subscription has expired.\n\nPlease renew your subscription.";
        };
    }

    private String getCardTypeText(Language language, AdminCard adminCard) {
        return switch (adminCard.getCardType()) {
            case HUMO -> getHumoText(language);
            case UZCARD -> getUzcardText(language);
            default -> "Card";
        };
    }

    private String getHumoText(Language language) {
        return switch (language) {
            case UZBEK, CYRILLIC, RUSSIAN, ENGLISH -> "HUMO";
        };
    }

    private String getUzcardText(Language language) {
        return switch (language) {
            case UZBEK, CYRILLIC, RUSSIAN, ENGLISH -> "UZCARD";
        };
    }

    private String getMessageIntro(Language language) {
        return switch (language) {
            case UZBEK -> "💰 Hisobni to'ldirish uchun ushbu kartaga to'lov qilishingiz kerak:";
            case CYRILLIC -> "💰 Ҳисобни тўлдириш учун ушбу картанга тўлов қилишингиз керак:";
            case RUSSIAN -> "💰 Для пополнения счета необходимо оплатить на эту карту:";
            case ENGLISH -> "💰 To top up your balance, please pay to this card:";
        };
    }

    private String getCardInfoLabel(Language language) {
        return switch (language) {
            case UZBEK -> "📝 Karta ma'lumotlari";
            case CYRILLIC -> "📝 Карта маълумотлари";
            case RUSSIAN -> "📝 Информация о карте";
            case ENGLISH -> "📝 Card information";
        };
    }

    private String getCardTypeLabel(Language language) {
        return switch (language) {
            case UZBEK -> "Karta turi";
            case CYRILLIC -> "Карта тури";
            case RUSSIAN -> "Тип карты";
            case ENGLISH -> "Card type";
        };
    }

    private String getCardNumberLabel(Language language) {
        return switch (language) {
            case UZBEK -> "Karta raqam";
            case CYRILLIC -> "Карта рақам";
            case RUSSIAN -> "Номер карты";
            case ENGLISH -> "Card number";
        };
    }

    private String getCardOwnerLabel(Language language) {
        return switch (language) {
            case UZBEK -> "Karta egasi";
            case CYRILLIC -> "Карта эгаси";
            case RUSSIAN -> "Владелец карты";
            case ENGLISH -> "Card owner";
        };
    }

    private String getReceiptNote(Language language) {
        return switch (language) {
            case UZBEK -> "💳 To'lov qilganingizdan so'ng, chekni **rasm formatida** yuboring";
            case CYRILLIC -> "💳 Тўлов қилганингиздан сўнг, чекни **расм форматида** юборинг";
            case RUSSIAN -> "💳 После оплаты отправьте чек в **формате изображения**";
            case ENGLISH -> "💳 After payment, send the receipt in **image format**";
        };
    }

    public String getReceiptNoteForMenu(Language language) {
        return switch (language) {
            case UZBEK -> "💳 Chekni <b>rasm formatida</b> yuboring";
            case CYRILLIC -> "💳 Чекни <b>расм форматида</b> юборинг";
            case RUSSIAN -> "💳 Отправьте чек в <b>формате изображения</b>";
            case ENGLISH -> "💳 Send the receipt in <b>image format</b>";
        };
    }


    public String paymentInformation(Language language, AdminCard adminCard) {
        if (adminCard == null) {
            return "";
        }

        String cardTypeText = getCardTypeText(language, adminCard);

        String messageIntro = getMessageIntro(language);
        String cardInfoLabel = getCardInfoLabel(language);
        String cardTypeLabel = getCardTypeLabel(language);
        String cardNumberLabel = getCardNumberLabel(language);
        String cardOwnerLabel = getCardOwnerLabel(language);
        String receiptNote = getReceiptNote(language);
        String importantNote = getImportantNote(language);

        return String.format(
                "%s\n\n%s\n%s: %s\n%s: %s\n%s: %s\n\n%s\n\n%s",
                messageIntro,
                cardInfoLabel,
                cardTypeLabel, cardTypeText,
                cardNumberLabel, adminCard.getNumber(),
                cardOwnerLabel, adminCard.getOwner(),
                receiptNote,
                importantNote
        );
    }

    private String getImportantNote(Language language) {
        return switch (language) {
            case UZBEK -> "⚠️ **DIQQAT:** Faqat rasm formatidagi cheklar qabul qilinadi (JPG, PNG).";
            case CYRILLIC -> "⚠️ **ДИҚҚАТ:** Фақат расм форматидаги чеклар қабул қилинади (JPG, PNG).";
            case RUSSIAN -> "⚠️ **ВНИМАНИЕ:** Принимаются только чеки в формате изображения (JPG, PNG).";
            case ENGLISH -> "⚠️ **IMPORTANT:** Only receipts in image format are accepted (JPG, PNG).";
        };
    }

    public String getCheckImage(Language language) {
        return switch (language) {
            case UZBEK -> "📄 Iltimos, to‘lov kvitansiyasining rasmini yuboring:";
            case CYRILLIC -> "📄 Илтимос, тўлов квитанциясининг расмини юборинг:";
            case RUSSIAN -> "📄 Пожалуйста, отправьте изображение квитанции о платеже:";
            case ENGLISH -> "📄 Please send the image of your payment receipt:";
        };
    }

    public String failedUploadCheck(Language language) {
        return switch (language) {
            case UZBEK -> "❌ To'lov kvitansiyasini yuklashda xatolik yuz berdi. Iltimos, chekni qaytadan yuboring.";
            case CYRILLIC -> "❌ Тўлов квитанциясини юклашда хатолик юз берди. Илтимос, чекни қайта юборинг.";
            case RUSSIAN -> "❌ Ошибка при загрузке квитанции о платеже. Пожалуйста, отправьте чек заново.";
            case ENGLISH -> "❌ Error occurred while uploading the payment receipt. Please resend the check.";
        };
    }


    public String failedCheckCreatingImage(Language language) {
        return switch (language) {
            case UZBEK -> "❌ Kvitansiyani chizishda xatolik yuz berdi. Iltimos, qaytadan urinib ko‘ring.";
            case CYRILLIC -> "❌ Квитанцияни чизишда хатолик юз берди. Илтимос, қайтадан уриниб кўринг.";
            case RUSSIAN -> "❌ При создании квитанции произошла ошибка. Пожалуйста, попробуйте снова.";
            case ENGLISH -> "❌ An error occurred while generating the receipt. Please try again.";
        };
    }


    public String senderCheckForChecking(Language language, String transactionId) {
        return switch (language) {
            case UZBEK -> String.format(
                    "✅ Sizning to'lov kvitansiyangiz adminga tekshirish uchun yuborildi.\n" +
                            "Agar muammo chiqsa, transaction ID: %s ni admin ga yuborishingiz mumkin.",
                    transactionId
            );
            case CYRILLIC -> String.format(
                    "✅ Сизнинг тўлов квитанциянгиз админга текшириш учун юборилди.\n" +
                            "Агар муаммо чиқса, transaction ID: %s ни админга юборишингиз мумкин.",
                    transactionId
            );
            case RUSSIAN -> String.format(
                    "✅ Ваша квитанция о платеже отправлена администратору для проверки.\n" +
                            "Если возникнут проблемы, вы можете отправить transaction ID: %s администратору.",
                    transactionId
            );
            case ENGLISH -> String.format(
                    "✅ Your payment receipt has been sent to the admin for verification.\n" +
                            "If any issues occur, you can send transaction ID: %s to the admin.",
                    transactionId
            );
        };
    }

    public String subscriptionPlanMsg(Language language) {
        return switch (language) {
            case UZBEK -> "✨ Obunani faollashtirish uchun o'zingizga kerakli obuna turini tanlang ✅";
            case RUSSIAN -> "✨ Чтобы активировать подписку, выберите нужный тип подписки ✅";
            case ENGLISH -> "✨ To activate your subscription, select the subscription type you need ✅";
            case CYRILLIC -> "✨ Обунани фаоллаштириш учун о'зингизга керакли обуна турини танланг ✅";
        };
    }


    public String subscriptionInformation(Language language, SubscriptionPlan subscriptionPlan, Discount discount) {
        String originalPrice = formatPrice(subscriptionPlan.getPrice(), language) + " 💰";
        String discountedPrice = "";
        String discountText = "";

        // Chegirma bo'lsa narxni hisoblash
        if (discount != null && subscriptionPlan.isDiscount()) {
            BigDecimal discountAmount = discount.getValue();
            if (discount.getType() == DiscountType.PERCENT) {
                BigDecimal percentPrice = subscriptionPlan.getPrice()
                        .multiply(BigDecimal.valueOf(100).subtract(discountAmount))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                discountedPrice = formatPrice(percentPrice, language) + " 💰";

                // Chegirma matnini tillarga mos tarjima qilish
                discountText = switch (language) {
                    case UZBEK ->
                            " (⏬ " + discountAmount.toString().substring(0, discountAmount.toString().indexOf('.')) + "% chegirma!)";
                    case CYRILLIC ->
                            " (⏬ " + discountAmount.toString().substring(0, discountAmount.toString().indexOf('.')) + "% чегирма!)";
                    case RUSSIAN ->
                            " (⏬ " + discountAmount.toString().substring(0, discountAmount.toString().indexOf('.')) + "% скидка!)";
                    case ENGLISH ->
                            " (⏬ " + discountAmount.toString().substring(0, discountAmount.toString().indexOf('.')) + "% discount!)";
                };
            } else if (discount.getType() == DiscountType.FIXED) {
                BigDecimal fixedPrice = subscriptionPlan.getPrice().subtract(discountAmount);
                if (fixedPrice.compareTo(BigDecimal.ZERO) < 0) {
                    fixedPrice = BigDecimal.ZERO;
                }
                discountedPrice = formatPrice(fixedPrice, language) + " 💰";

                // Fix chegirma uchun tillarga mos tarjima
                discountText = switch (language) {
                    case UZBEK -> " (⏬ " + formatPrice(discountAmount, language) + " chegirma!)";
                    case CYRILLIC -> " (⏬ " + formatPrice(discountAmount, language) + " чегирма!)";
                    case RUSSIAN -> " (⏬ " + formatPrice(discountAmount, language) + " скидка!)";
                    case ENGLISH -> " (⏬ " + formatPrice(discountAmount, language) + " discount!)";
                };
            }
        }

        // Plan nomini tillarga mos tarjima qilish
        String planName = switch (language) {
            case UZBEK -> switch (subscriptionPlan.getName()) {
                case MONTH_1 -> "📅 1 oylik obuna ✨";
                case MONTH_2 -> "📅 2 oylik obuna ✨";
                case MONTH_3 -> "📅 3 oylik obuna ✨";
                case MONTH_6 -> "📅 6 oylik obuna ✨";
                case MONTH_12 -> "📅 12 oylik obuna ✨";
                case TRIAL -> "🆓 Sinov muddati ⏳";
                case ACTIVE_ALWAYS -> "♾️ Umrbod obuna 💎";
                case EXPIRED -> "❌ Muddati tugagan ⚠️";
            };
            case CYRILLIC -> switch (subscriptionPlan.getName()) {
                case MONTH_1 -> "📅 1 ойлик обуна ✨";
                case MONTH_2 -> "📅 2 ойлик обуна ✨";
                case MONTH_3 -> "📅 3 ойлик обуна ✨";
                case MONTH_6 -> "📅 6 ойлик обуна ✨";
                case MONTH_12 -> "📅 12 ойлик обуна ✨";
                case TRIAL -> "🆓 Синoв муддати ⏳";
                case ACTIVE_ALWAYS -> "♾️ Умрбод обуна 💎";
                case EXPIRED -> "❌ Муддати тугаган ⚠️";
            };
            case RUSSIAN -> switch (subscriptionPlan.getName()) {
                case MONTH_1 -> "📅 1 месяц подписки ✨";
                case MONTH_2 -> "📅 2 месяца подписки ✨";
                case MONTH_3 -> "📅 3 месяца подписки ✨";
                case MONTH_6 -> "📅 6 месяцев подписки ✨";
                case MONTH_12 -> "📅 12 месяцев подписки ✨";
                case TRIAL -> "🆓 Пробный период ⏳";
                case ACTIVE_ALWAYS -> "♾️ Пожизненная подписка 💎";
                case EXPIRED -> "❌ Срок истек ⚠️";
            };
            case ENGLISH -> switch (subscriptionPlan.getName()) {
                case MONTH_1 -> "📅 1 month subscription ✨";
                case MONTH_2 -> "📅 2 months subscription ✨";
                case MONTH_3 -> "📅 3 months subscription ✨";
                case MONTH_6 -> "📅 6 months subscription ✨";
                case MONTH_12 -> "📅 12 months subscription ✨";
                case TRIAL -> "🆓 Trial period ⏳";
                case ACTIVE_ALWAYS -> "♾️ Lifetime subscription 💎";
                case EXPIRED -> "❌ Expired ⚠️";
            };
        };

        // Narxni ko'rsatish formati
        String priceDisplay;
        if (!discountedPrice.isEmpty()) {
            // Chegirma bo'lsa: eski narx ustiga chiziq + yangi narx
            priceDisplay = "<s>" + originalPrice + "</s> " + discountedPrice + discountText;
        } else {
            // Chegirma bo'lmasa: oddiy narx
            priceDisplay = originalPrice;
        }

        // Asosiy matnni tillarga mos tarjima qilish
        return switch (language) {
            case UZBEK -> "<b>" + planName + "</b>\n" +
                    "<b>Narx:</b> " + priceDisplay + "\n" +
                    "✨ <i>Obunangizni faollashtirish uchun tanlang!</i>";

            case CYRILLIC -> "<b>" + planName + "</b>\n" +
                    "<b>Нархи:</b> " + priceDisplay + "\n" +
                    "✨ <i>Обунангизни фаоллаштириш учун танланг!</i>";

            case RUSSIAN -> "<b>" + planName + "</b>\n" +
                    "<b>Цена:</b> " + priceDisplay + "\n" +
                    "✨ <i>Выберите, чтобы активировать подписку!</i>";

            case ENGLISH -> "<b>" + planName + "</b>\n" +
                    "<b>Price:</b> " + priceDisplay + "\n" +
                    "✨ <i>Select to activate your subscription!</i>";
        };
    }


    private String formatPrice(BigDecimal price, Language language) {
        if (price == null) {
            price = BigDecimal.ZERO;
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        formatter.setGroupingSize(3);

        String formattedPrice = formatter.format(price);

        // Valyuta belgisi
        String currency = getCurrencySymbol(language);

        return formattedPrice + " " + currency;
    }

    private String getCurrencySymbol(Language language) {
        // O'zbekiston so'mi uchun
        return switch (language) {
            case UZBEK -> "so'm";
            case RUSSIAN, CYRILLIC -> "сум";
            case ENGLISH -> "UZS";
        };

    }

    public String getPlanName(Language language, SubscriptionPlan subscriptionPlan) {
        return switch (language) {
            case UZBEK -> switch (subscriptionPlan.getName()) {
                case MONTH_1 -> "📅 1 oylik obuna ✨";
                case MONTH_2 -> "📅 2 oylik obuna ✨";
                case MONTH_3 -> "📅 3 oylik obuna ✨";
                case MONTH_6 -> "📅 6 oylik obuna ✨";
                case MONTH_12 -> "📅 12 oylik obuna ✨";
                case TRIAL -> "🆓 Sinov muddati ⏳";
                case ACTIVE_ALWAYS -> "♾️ Umrbod obuna 💎";
                case EXPIRED -> "❌ Muddati tugagan ⚠️";
            };
            case CYRILLIC -> switch (subscriptionPlan.getName()) {
                case MONTH_1 -> "📅 1 ойлик обуна ✨";
                case MONTH_2 -> "📅 2 ойлик обуна ✨";
                case MONTH_3 -> "📅 3 ойлик обуна ✨";
                case MONTH_6 -> "📅 6 ойлик обуна ✨";
                case MONTH_12 -> "📅 12 ойлик обуна ✨";
                case TRIAL -> "🆓 Синов муддати ⏳";
                case ACTIVE_ALWAYS -> "♾️ Умрбод обуна 💎";
                case EXPIRED -> "❌ Муддати тугаган ⚠️";
            };
            case RUSSIAN -> switch (subscriptionPlan.getName()) {
                case MONTH_1 -> "📅 1 месяц подписки ✨";
                case MONTH_2 -> "📅 2 месяца подписки ✨";
                case MONTH_3 -> "📅 3 месяца подписки ✨";
                case MONTH_6 -> "📅 6 месяцев подписки ✨";
                case MONTH_12 -> "📅 12 месяцев подписки ✨";
                case TRIAL -> "🆓 Пробный период ⏳";
                case ACTIVE_ALWAYS -> "♾️ Бессрочная подписка 💎";
                case EXPIRED -> "❌ Срок истёк ⚠️";
            };
            case ENGLISH -> switch (subscriptionPlan.getName()) {
                case MONTH_1 -> "📅 1 month subscription ✨";
                case MONTH_2 -> "📅 2 months subscription ✨";
                case MONTH_3 -> "📅 3 months subscription ✨";
                case MONTH_6 -> "📅 6 months subscription ✨";
                case MONTH_12 -> "📅 12 months subscription ✨";
                case TRIAL -> "🆓 Trial period ⏳";
                case ACTIVE_ALWAYS -> "♾️ Lifetime subscription 💎";
                case EXPIRED -> "❌ Expired ⚠️";
            };
        };
    }

    public String updatedSubscriptionPlan(Language language, SubscriptionPlan subscriptionPlan) {
        String planName = getPlanName(language, subscriptionPlan);
        return switch (language) {
            case UZBEK -> planName + " muvaffaqiyatli yangilandi! ✅";
            case CYRILLIC -> planName + " муваффақиятли янгиланди! ✅";
            case RUSSIAN -> planName.replace("oylik obuna", "месяц подписки") + " успешно обновлено! ✅";
            case ENGLISH -> planName.replace("oylik obuna", "month subscription") + " has been successfully updated! ✅";
        };
    }

    public String accountIsLittle(Language language) {
        return switch (language) {
            case UZBEK ->
                    "❌ Sizning balansingiz yetarli emas. Iltimos, hisobingizni to‘ldiring va qayta urinib ko‘ring.";
            case CYRILLIC ->
                    "❌ Сизнинг балансингиз етарли эмас. Илтимос, ҳисобингизни тўлдиринг ва қайтадан уриниб кўринг.";
            case RUSSIAN -> "❌ На вашем балансе недостаточно средств. Пожалуйста, пополните баланс и попробуйте снова.";
            case ENGLISH -> "❌ Your balance is insufficient. Please top up your account and try again.";
        };
    }

    public String myBalance(Language language, Seller seller, boolean expired, SubscriptionPlan subscription) {
        if (expired) {
            String balance = seller.getBalance() != null
                    ? formatPrice(seller.getBalance(), language)
                    : formatPrice(BigDecimal.ZERO, language);
            Long id = seller.getPkey();
            return switch (language) {
                case UZBEK -> "💰 Hisobingiz: " + balance + " \n\n👤 ID raqam: " + id;
                case CYRILLIC -> "💰 Ҳисобингиз: " + balance + " \n\n👤 ID рақам: " + id;
                case RUSSIAN -> "💰 Ваш баланс: " + balance + " \n\n👤 ID номер: " + id;
                case ENGLISH -> "💰 Your balance: " + balance + " \n\n👤 ID number: " + id;
            };
        } else {
            return myBalanceAndAccountInformation(language, seller, subscription);
        }
    }

    private String myBalanceAndAccountInformation(
            Language language,
            Seller seller,
            SubscriptionPlan subscription
    ) {

        BigDecimal balance = seller.getBalance() != null ? seller.getBalance() : BigDecimal.ZERO;

        LocalDateTime expiresAt = seller.getPlanExpiresAt();
        SubscriptionPlanType planType = subscription != null ? subscription.getName() : SubscriptionPlanType.EXPIRED;

        String expireText;
        if (planType == SubscriptionPlanType.ACTIVE_ALWAYS) {
            expireText = switch (language) {
                case UZBEK -> "Cheksiz (Umrbod)";
                case CYRILLIC -> "Чексиз (Умрбод)";
                case RUSSIAN -> "Бессрочно";
                case ENGLISH -> "Lifetime";
            };
        } else if (expiresAt != null) {
            expireText = expiresAt.toLocalDate().toString();
        } else {
            expireText = switch (language) {
                case UZBEK -> "Mavjud emas";
                case CYRILLIC -> "Мавжуд эмас";
                case RUSSIAN -> "Недоступно";
                case ENGLISH -> "Not available";
            };
        }

        String planName = planType.getFullDescription(language);

        return switch (language) {
            case UZBEK -> """
                    💰 <b>Hisobingiz</b>
                    
                    💵 Balans: <b>%s</b>
                    
                    📦 Obuna: <b>%s</b>
                    ⏳ Tugash sanasi: <b>%s</b>
                    """.formatted(formatPrice(balance, language), planName, expireText);

            case CYRILLIC -> """
                    💰 <b>Ҳисобингиз</b>
                    
                    💵 Баланс: <b>%s</b>
                    
                    📦 Обуна: <b>%s</b>
                    ⏳ Тугаш санаси: <b>%s</b>
                    """.formatted(formatPrice(balance, language), planName, expireText);
            case RUSSIAN -> """
                    💰 <b>Ваш аккаунт</b>
                    
                    💵 Баланс: <b>%s</b>
                    
                    📦 Подписка: <b>%s</b>
                    ⏳ Действует до: <b>%s</b>
                    """.formatted(formatPrice(balance, language), planName, expireText);
            case ENGLISH -> """
                    💰 <b>Your Account</b>
                    
                    💵 Balance: <b>%s</b>
                    
                    📦 Subscription: <b>%s</b>
                    ⏳ Expires at: <b>%s</b>
                    """.formatted(formatPrice(balance, language), planName, expireText);
        };
    }


    public String rejectedMsg(Language language) {
        return switch (language) {

            case UZBEK -> """
                    Hurmatli foydalanuvchi!
                    
                    Siz tomonidan yuborilgan ariza adminstratsiya tomonidan batafsil ko‘rib chiqildi. \
                    Tekshiruv natijalariga ko‘ra, ushbu ariza hozirgi holatda tasdiqlanmadi va bekor qilindi.
                    
                    Agar sizda qo‘shimcha savollar bo‘lsa yoki arizani qayta topshirmoqchi bo‘lsangiz, \
                    ma’lumotlarni to‘liq va aniq shaklda yuborishingizni so‘raymiz.
                    
                    Tushunganingiz uchun rahmat.""";

            case CYRILLIC -> """
                    Ҳурматли фойдаланувчи!
                    
                    Сиз томонидан юборилган ариза администрация томонидан батафсил кўриб чиқилди. \
                    Текширув натижаларига кўра, ушбу ариза ҳозирги ҳолатда тасдиқланмади ва бекор қилинди.
                    
                    Агар сизда қўшимча саволлар бўлса ёки аризани қайта топширмоқчи бўлсангиз, \
                    маълумотларни тўлиқ ва аниқ шаклда юборишингизни сўраймиз.
                    
                    Тушунганингиз учун раҳмат.""";

            case RUSSIAN -> """
                    Уважаемый пользователь!
                    
                    Ваша заявка была внимательно рассмотрена администрацией. \
                    По результатам проверки данная заявка не была одобрена и была отклонена.
                    
                    Если у вас возникли вопросы или вы хотите отправить заявку повторно, \
                    пожалуйста, убедитесь, что все данные указаны корректно и полностью.
                    
                    Благодарим за понимание.""";

            case ENGLISH -> """
                    Dear user,
                    
                    Your application has been carefully reviewed by the administration. \
                    Based on the review results, the application was not approved and has been rejected.
                    
                    If you have any questions or wish to submit the application again, \
                    please make sure all information is provided accurately and completely.
                    
                    Thank you for your understanding.""";
        };
    }

    public String help(Language language) {
        return switch (language) {
            case UZBEK ->
                    "😊 Yordam yoki savollaringiz bo‘lsa, quyidagi kontaktlar orqali biz bilan bog‘lanishingiz mumkin.";
            case CYRILLIC ->
                    "😊 Ёрдам ёки саволларингиз бўлса, қуйидаги контактлар орқали биз билан боғланишингиз мумкин.";
            case RUSSIAN ->
                    "😊 Если у вас есть вопросы или нужна помощь, вы можете связаться с нами по следующим контактам.";
            case ENGLISH ->
                    "😊 If you have any questions or need assistance, you can contact us using the details below.";
        };
    }


    public String sellerProfileInformation(Language language, Seller seller, User user) {
        String fullName = user.getFullName();
        String phone = seller.getPhone();

        return switch (language) {
            case UZBEK -> "👤 Sizning profilingiz:\n\n" +
                    "To‘liq ism: " + fullName + "\n" +
                    "Telefon raqami: " + phone;
            case CYRILLIC -> "👤 Сизнинг профилингиз:\n\n" +
                    "Тўлиқ исм: " + fullName + "\n" +
                    "Телефон рақами: " + phone;
            case RUSSIAN -> "👤 Ваш профиль:\n\n" +
                    "Полное имя: " + fullName + "\n" +
                    "Номер телефона: " + phone;
            case ENGLISH -> "👤 Your profile:\n\n" +
                    "Full name: " + fullName + "\n" +
                    "Phone number: " + phone;
        };
    }


    public String editPhone(Language language, String phone) {

        String currentPhone = phone != null ? phone : "-";

        return switch (language) {
            case UZBEK -> "📞 Telefon raqamini o‘zgartirish\n\n" +
                    "Joriy telefon raqamingiz: <code>" + currentPhone + "</code>\n\n" +
                    "Yangi telefon raqamingizni yuboring.";

            case CYRILLIC -> "📞 Телефон рақамини ўзгартириш\n\n" +
                    "Жорий телефон рақамингиз: <code>" + currentPhone + "</code>\n\n" +
                    "Янги телефон рақамингизни юборинг.";

            case RUSSIAN -> "📞 Изменение номера телефона\n\n" +
                    "Текущий номер телефона: <code>" + currentPhone + "</code>\n\n" +
                    "Пожалуйста, отправьте новый номер телефона.";

            case ENGLISH -> "📞 Change phone number\n\n" +
                    "Current phone number: <code>" + currentPhone + "</code>\n\n" +
                    "Please send your new phone number.";
        };
    }

    public String editFullName(Language language, User user) {
        if (user == null) return ".";
        String currentFullName =
                user.getFullName() != null && !user.getFullName().isBlank()
                        ? user.getFullName()
                        : "-";

        return switch (language) {
            case UZBEK -> "📛 To‘liq ismni o‘zgartirish\n\n" +
                    "Joriy to‘liq ismingiz: <code>" + currentFullName + "</code>\n\n" +
                    "Yangi to‘liq ismingizni yuboring.";

            case CYRILLIC -> "📛 Тўлиқ исмни ўзгартириш\n\n" +
                    "Жорий тўлиқ исмингиз: <code>" + currentFullName + "</code>\n\n" +
                    "Янги тўлиқ исмингизни юборинг.";

            case RUSSIAN -> "📛 Изменение полного имени\n\n" +
                    "Текущее полное имя: <code>" + currentFullName + "</code>\n\n" +
                    "Пожалуйста, отправьте новое полное имя.";

            case ENGLISH -> "📛 Change full name\n\n" +
                    "Current full name: <code>" + currentFullName + "</code>\n\n" +
                    "Please send your new full name.";
        };
    }

    public String changed(Language language) {
        return switch (language) {
            case UZBEK -> "✅ Muvaffaqiyatli o‘zgartirildi.";
            case CYRILLIC -> "✅ Муваффақиятли ўзгартирилди.";
            case RUSSIAN -> "✅ Успешно изменено.";
            case ENGLISH -> "✅ Successfully updated.";
        };
    }


    public String cardInfo(Language language, Seller seller) {

        String cardNumber = seller.getCardNumber() != null
                ? maskCardNumber(seller.getCardNumber())
                : "-";

        String cardOwner = seller.getCardOwner() != null
                ? seller.getCardOwner()
                : "-";

        return switch (language) {
            case UZBEK -> "💳 Karta ma’lumotlari\n\n" +
                    "💳 Karta raqami: <code>" + cardNumber + "</code>\n" +
                    "👤 Karta egasi: <code>" + cardOwner + "</code>";

            case CYRILLIC -> "💳 Карта маълумотлари\n\n" +
                    "💳 Карта рақами: <code>" + cardNumber + "</code>\n" +
                    "👤 Карта эгаси: <code>" + cardOwner + "</code>";

            case RUSSIAN -> "💳 Информация о карте\n\n" +
                    "💳 Номер карты: <code>" + cardNumber + "</code>\n" +
                    "👤 Владелец карты: <code>" + cardOwner + "</code>";

            case ENGLISH -> "💳 Card information\n\n" +
                    "💳 Card number: <code>" + cardNumber + "</code>\n" +
                    "👤 Card holder: <code>" + cardOwner + "</code>";
        };
    }

    private String maskCardNumber(String cardNumber) {
        return cardNumber;
    }

    public String editingCardPhotoMsg(Language language) {
        return switch (language) {
            case UZBEK -> "Iltimos, yangi kartaning rasmini yuboring 📸";
            case CYRILLIC -> "Илтимос, янги картанинг расмини юборинг 📸";
            case RUSSIAN -> "Пожалуйста, отправьте фото новой карты 📸";
            case ENGLISH -> "Please send the photo of your new card 📸";
        };
    }

    public String editingCardOwner(Language language, String cardOwner) {
        return switch (language) {
            case UZBEK ->
                    "Siz hozir `" + cardOwner + "` nomli karta egasini o‘zgartiryapsiz. Iltimos, yangi ism va familiyani yuboring:";
            case CYRILLIC ->
                    "Сиз ҳозир `" + cardOwner + "` номли карта эгасини ўзгартиряпсиз. Илтимос, янги исм ва фамилияни юборинг:";
            case RUSSIAN ->
                    "Вы сейчас изменяете владельца карты `" + cardOwner + "`. Пожалуйста, отправьте новое имя и фамилию:";
            case ENGLISH -> "You are now editing the card owner `" + cardOwner + "`. Please send the new full name:";
        };
    }

    public String editingCardNumber(Language language) {
        return switch (language) {
            case UZBEK -> "Iltimos, yangi karta raqamini yuboring:";
            case CYRILLIC -> "Илтимос, янги карта рақамини юборинг:";
            case RUSSIAN -> "Пожалуйста, отправьте новый номер карты:";
            case ENGLISH -> "Please send the new card number:";
        };
    }

    public String confirmLogout(Language language) {
        return switch (language) {
            case UZBEK -> "⚠️ Siz haqiqatdan ham tizimdan chiqishni xohlaysizmi?";
            case CYRILLIC -> "⚠️ Сиз ҳақиқатан ҳам тизимдан чиқишни хоҳлайсизми?";
            case RUSSIAN -> "⚠️ Вы действительно хотите выйти из системы?";
            case ENGLISH -> "⚠️ Are you sure you want to log out?";
        };
    }

    public String successfullyLogout(Language language) {
        return switch (language) {
            case UZBEK -> "Siz muvaffaqiyatli tizimdan chiqdiniz ✅";
            case CYRILLIC -> "Сиз муваффақиятли тизимдан чиқдингиз ✅";
            case RUSSIAN -> "Вы успешно вышли из системы ✅";
            case ENGLISH -> "You have successfully logged out ✅";
        };
    }

}
