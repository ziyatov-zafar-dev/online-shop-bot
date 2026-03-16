package uz.zafar.onlineshoptelegrambot.bot.msg;

import org.apache.commons.codec.language.bm.Lang;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.common.Discount;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.DiscountType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.WorkDay;
import uz.zafar.onlineshoptelegrambot.db.entity.order.Basket;
import uz.zafar.onlineshoptelegrambot.db.entity.order.OrderItem;
import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.DeliveryType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.db.repositories.OrderItemRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.UserRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateOrderItemRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateShopOrderRequestDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static uz.zafar.onlineshoptelegrambot.db.entity.enums.Language.CYRILLIC;
import static uz.zafar.onlineshoptelegrambot.db.entity.enums.Language.UZBEK;

@Component
public class UserMsg {

    private final ShopRepository shopRepository;
    private final ProductTypeRepository productTypeRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public UserMsg(ShopRepository shopRepository,
                   ProductTypeRepository productTypeRepository,
                   UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.shopRepository = shopRepository;
        this.productTypeRepository = productTypeRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;

    }

    public String welcomeChooseLang(Language language) {
        return switch (language) {
            case UZBEK -> """
                    <b>Assalomu alaykum!</b> 👋
                    
                    Botimizga xush kelibsiz.
                    Botdan foydalanish uchun o'zingizga kerakli tilni tanlang 🌐
                    """;
            case CYRILLIC -> """
                    <b>Салом алайкум!</b> 👋
                    
                    Ботимизга хуш келибсиз.
                    Ботдан фойдаланиш учун ўзингизга керакли тилни танланг 🌐
                    """;
            case RUSSIAN -> """
                    <b>Здравствуйте!</b> 👋
                    
                    Добро пожаловать в наш бот.
                    Чтобы пользоваться ботом, выберите удобный для вас язык 🌐
                    """;
            case ENGLISH -> """
                    <b>Hello!</b> 👋
                    
                    Welcome to our bot.
                    To use the bot, please choose your preferred language 🌐
                    """;
        };
    }

    public String menu(Language language) {
        return switch (language) {
            case UZBEK -> "                    <b>Siz asosiy menyudasiz</b> \uD83D\uDC4B\n".trim();
            case CYRILLIC -> "<b>Сиз асосий менюдасиз</b> \uD83D\uDC4B";
            case RUSSIAN -> "<b>Вы в главном меню</b> \uD83D\uDC4B";
            case ENGLISH -> "  <b>You are in the main menu</b> \uD83D\uDC4B";
        };
    }

    public String successfullyChangedLang(Language language) {
        return switch (language) {
            case UZBEK -> "✅ Til muvaffaqiyatli o‘zgartirildi: 🇺🇿 O‘zbekcha";
            case CYRILLIC -> "✅ Тил муваффақиятли ўзгартирилди: \uD83C\uDDFA\uD83C\uDDFF Кириллча";
            case RUSSIAN -> "✅ Язык успешно изменен: 🇷🇺 Русский";
            case ENGLISH -> "✅ Language successfully changed: 🇬🇧 English";
        };
    }

    public String requestContact(Language language) {
        return switch (language) {
            case UZBEK -> "📞 Buyurtma berish uchun iltimos, kontaktingizni ulashing 😊";
            case CYRILLIC -> "📞 Буюртма бериш учун илтимос, контактингизни улашинг 😊";
            case RUSSIAN -> "📞 Пожалуйста, поделитесь своим контактом для оформления заказа 😊";
            case ENGLISH -> "📞 Please share your contact to place an order 😊";
        };
    }

    public String wrongBtn(Language language) {
        return switch (language) {
            case UZBEK -> "❌ Noto‘g‘ri tugma bosildi!\n" +
                    "👉 Iltimos, pastdagi tugmalardan foydalaning 😊";

            case CYRILLIC -> "❌ Нотўғри тугма босилди!\n" +
                    "👉 Илтимос, пастдаги тугмалардан фойдаланинг 😊";

            case RUSSIAN -> "❌ Нажата неверная кнопка!\n" +
                    "👉 Пожалуйста, используйте кнопки ниже 😊";

            case ENGLISH -> "❌ Wrong button pressed!\n" +
                    "👉 Please use the buttons below 😊";
        };
    }

    public String savedContact(Language language) {
        return switch (language) {
            case UZBEK -> "✅ Kontaktingiz muvaffaqiyatli saqlandi!";
            case CYRILLIC -> "✅ Контактингиз муваффақиятли сақланди!\n";
            case RUSSIAN -> "✅ Ваш контакт успешно сохранён!\n";
            case ENGLISH -> "✅ Your contact has been saved successfully!";
        };
    }

    public String sendLocationOrChooseMyLocation(boolean emptyMyLocations, Language language) {

        return switch (language) {

            case UZBEK -> emptyMyLocations
                    ? "📍 Buyurtmani davom ettirish uchun joylashuvingizni yuborishingiz kerak.\n" +
                    "🗺 Iltimos, lokatsiyani ulashing"
                    : """
                    📍 Buyurtmani davom ettirish uchun:
                    👉 joylashuvingizni yuboring
                    👉 yoki saqlangan manzillaringizdan birini tanlang""";

            case CYRILLIC -> emptyMyLocations
                    ? "📍 Буюртмани давом эттириш учун жойлашувингизни юборишингиз керак.\n" +
                    "🗺 Илтимос, локацияни улашинг"
                    : """
                    📍 Буюртмани давом эттириш учун:
                    👉 жойлашувингизни юборинг
                    👉 ёки сақланган манзилларингиздан бирини танланг""";

            case RUSSIAN -> emptyMyLocations
                    ? "📍 Чтобы продолжить заказ, необходимо отправить вашу локацию.\n" +
                    "🗺 Пожалуйста, поделитесь местоположением"
                    : """
                    📍 Чтобы продолжить заказ:
                    👉 отправьте свою локацию
                    👉 или выберите один из сохранённых адресов""";

            case ENGLISH -> emptyMyLocations
                    ? "📍 To continue your order, you must send your location.\n" +
                    "🗺 Please share your location"
                    : """
                    📍 To continue your order:
                    👉 send your location
                    👉 or choose one of your saved addresses""";
        };
    }

    public String isAddedMyLocation(Language language, String address) {
        return switch (language) {
            case UZBEK -> "👇 <b>Quyidagilardan birini tanlang</b>\n\n" +
                    "🏠 <b>Manzilingiz:</b>\n" +
                    address;

            case CYRILLIC -> "👇 <b>Қуйидагилардан бирини танланг</b>\n\n" +
                    "🏠 <b>Манзилингиз:</b>\n" +
                    address;

            case RUSSIAN -> "👇 <b>Выберите один из вариантов</b>\n\n" +
                    "🏠 <b>Ваш адрес:</b>\n" +
                    address;

            case ENGLISH -> "👇 <b>Please choose one of the options</b>\n\n" +
                    "🏠 <b>Your address:</b>\n" +
                    address;
        };
    }


    public String addedMyLocation(Language language) {
        return switch (language) {
            case UZBEK -> "✅ <b>Muvaffaqiyatli qo‘shildi!</b>\n\n" +
                    "📍 Manzilingiz <b>Manzillarim</b> ro‘yxatiga saqlandi.";

            case CYRILLIC -> "✅ <b>Муваффақиятли қўшилди!</b>\n\n" +
                    "📍 Манзилингиз <b>Манзилларим</b> рўйхатига сақланди.";

            case RUSSIAN -> "✅ <b>Успешно добавлено!</b>\n\n" +
                    "📍 Ваш адрес сохранён в списке <b>Мои адреса</b>.";

            case ENGLISH -> "✅ <b>Successfully added!</b>\n\n" +
                    "📍 Your address has been saved to <b>My Addresses</b>.";
        };
    }

    public String getCategories(Language language) {
        return switch (language) {
            case UZBEK -> """
                    🗂 <b>Kategoriyalar</b>
                    
                    Quyidagi kategoriyalardan birini tanlang 👇""";

            case CYRILLIC -> """
                    🗂 <b>Категориялар</b>
                    
                    Қуйидаги категориялардан бирини танланг 👇""";

            case RUSSIAN -> """
                    🗂 <b>Категории</b>
                    
                    Выберите одну из категорий ниже 👇""";

            case ENGLISH -> """
                    🗂 <b>Categories</b>
                    
                    Please choose one of the categories below 👇""";
        };
    }

    public String categoryInformation(Language language, Category category, int productsSize) {
        switch (language) {
            case UZBEK -> {
                return "📂 <b>" + category.getNameUz() + "</b>\n\n" +
                        "📝 " + category.getDescriptionUz() + "\n\n" +
                        "🛍️ Mahsulotlar soni: <b>" + productsSize + "</b>";
            }
            case CYRILLIC -> {
                return "📂 <b>" + category.getNameCyr() + "</b>\n\n" +
                        "📝 " + category.getDescriptionCyr() + "\n\n" +
                        "🛍️ Маҳсулотлар сони: <b>" + productsSize + "</b>";
            }
            case RUSSIAN -> {
                return "📂 <b>" + category.getNameRu() + "</b>\n\n" +
                        "📝 " + category.getDescriptionRu() + "\n\n" +
                        "🛍️ Количество товаров: <b>" + productsSize + "</b>";
            }
            case ENGLISH -> {
                return "📂 <b>" + category.getNameEn() + "</b>\n\n" +
                        "📝 " + category.getDescriptionEn() + "\n\n" +
                        "🛍️ Products count: <b>" + productsSize + "</b>";
            }
            default -> {
                return category.getNameEn(); // Fallback
            }
        }
    }

    public String fullInformationProductAndProductTypes(Language language, Product product,
                                                        List<ProductType> productTypes,
                                                        ProductType currentProductType) {

        // Asosiy ma'lumotlarni tilga qarab olish
        String productName = getProductNameByLanguage(product, language);
        String description = getProductDescriptionByLanguage(product, language);
        String productTypeName = getProductTypeNameByLanguage(currentProductType, language);

        // Chegirma ma'lumotlari
        Discount discount = product.getDiscount();
        boolean hasDiscount = discount != null && discount.getType() != DiscountType.NONE;

        // Narxni hisoblash (chegirma bilan)
        BigDecimal currentPrice = currentProductType.getPrice();
        BigDecimal discountedPrice = currentPrice;

        // HTML formatdagi message yaratish
        StringBuilder html = new StringBuilder();

        // 1. Sarlavha qismi (rasmdagi kabi markazlashtirilgan)
        html.append("<b>").append(escapeHtml(productName)).append("</b>\n");
        html.append("<i>").append(escapeHtml(productTypeName)).append("</i>\n\n");

        // 2. Tavsif qismi (agar mavjud bo'lsa)
        if (description != null && !description.trim().isEmpty()) {
            // Tavsifni satrlarga bo'lish va formatlash
            String[] descriptionLines = description.split("\n");
            for (String line : descriptionLines) {
                if (!line.trim().isEmpty()) {
                    html.append("• ").append(escapeHtml(line.trim())).append("\n");
                }
            }
            html.append("\n");
        }

        // 3. Narx qismi (chegirma bilan yoki chegirmasiz)
        html.append("<b>%s</b> ".formatted(
                language == Language.UZBEK ? "Narxi:" : (
                        language == CYRILLIC ? "Narxi:" :
                                language == Language.ENGLISH ? "Price:" : "Цена:"
                )
        ));
        if (hasDiscount) {
            discountedPrice = calculateDiscountedPrice(currentPrice, discount);

            // Chegirmali narxni ko'rsatish
            html.append("<b>").append(formatPrice(discountedPrice, language)).append("</b>\n");

            // Eskirgan narxni chizib tashlash
            html.append("<s>").append(formatPrice(currentPrice, language)).append("</s>\n");

            // Chegirma haqida ma'lumot
            html.append("<i>").append(getFormattedDiscountInfo(discount, language)).append("</i>\n\n");
        } else {
            html.append("<b>").append(formatPrice(currentPrice, language)).append("</b>\n\n");
        }

        if (currentProductType.getStock() <= 0) {
            html.append("<code>❌ %s</code>\n".formatted(
                    language == Language.UZBEK ? "Zaxira tugagan" :
                            language == Language.RUSSIAN ? "Распродано" : (
                                    language == Language.ENGLISH ? "Out of stock" :
                                            "Захира тугаган"
                            )
            ));
        } else if (currentProductType.getStock() < 10) {
            html.append("<code>⚠️ ").append(currentProductType.getStock()).append(" %s</code>\n".formatted(
                    language == Language.UZBEK ? "ta qoldi" : (
                            language == CYRILLIC ? "та қолди" : (
                                    language == Language.RUSSIAN ? "осталось" : "left"
                            )
                    )
            ));
        }

        if (productTypes != null && productTypes.size() > 1) {
            html.append("\n");
            html.append("<b>%s</b>\n".formatted(
                    language == Language.UZBEK ? "Boshqa variantlar:" : (
                            language == Language.RUSSIAN ? "Другие варианты:" : (
                                    language == Language.ENGLISH ? "Other options:" : "Бошқа вариантлар:"
                            )
                    )
            ));

            for (ProductType type : productTypes) {
                if (!type.getPkey().equals(currentProductType.getPkey())) {
                    String variantName = getProductTypeNameByLanguage(type, language);
                    String variantPrice = formatPrice(type.getPrice(), language);
                    String variantStatus = type.getStock() > 0 ? "✅" : "❌";

                    html.append(variantStatus).append(" ")
                            .append(escapeHtml(variantName))
                            .append(" - ").append(variantPrice).append("\n");
                }
            }
        }

//         8. Vaqt (agar kerak bo'lsa)
        html.append("\n").append(getCurrentTime());

        return html.toString();
    }

    private String getFormattedDiscountInfo(Discount discount, Language language) {
        if (discount == null || discount.getType() == DiscountType.NONE) {
            return "";
        }

        String prefix = getDiscountPrefix(language);

        if (discount.getType() == DiscountType.PERCENT) {
            // Foizni butun songa aylantirish
            BigDecimal discountPercentage = normalizeDiscountPercentage(discount.getValue());
            return prefix + ": " + discountPercentage.stripTrailingZeros().toPlainString() + "%";
        } else if (discount.getType() == DiscountType.FIXED) {
            return prefix + ": " + formatPrice(discount.getValue(), language);
        }

        return "";
    }

    private BigDecimal normalizeDiscountPercentage(BigDecimal percentage) {
        if (percentage == null) {
            return BigDecimal.ZERO;
        }

        // O'nli kasrlarni olib tashlash
        percentage = percentage.stripTrailingZeros();

        // Agar scale 0 dan kichik yoki teng bo'lsa (butun son)
        if (percentage.scale() <= 0) {
            return percentage;
        }

        // O'nli kasr bo'lsa, butun songa yaxlitlash
        return percentage.setScale(0, RoundingMode.HALF_UP);
    }

    private String getDiscountPrefix(Language language) {
        return switch (language) {
            case UZBEK, CYRILLIC -> "Chegirma";
            case RUSSIAN -> "Скидка";
            case ENGLISH -> "Discount";
            default -> "Chegirma";
        };
    }
// Qo'shimcha yordamchi metodlar

    private String getCurrentTime() {
        // Joriy vaqtni olish
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }

    // Alternativ: Eng oddiy va chiroyli format
    public String getSimpleProductInfo(Language language, Product product,
                                       ProductType productType, int quantity) {

        String productName = getProductNameByLanguage(product, language);
        String productTypeName = getProductTypeNameByLanguage(productType, language);
        String description = getProductDescriptionByLanguage(product, language);

        BigDecimal price = productType.getPrice();
        Discount discount = product.getDiscount();
        boolean hasDiscount = discount != null && discount.getType() != DiscountType.NONE;

        if (hasDiscount) {
            price = calculateDiscountedPrice(price, discount);
        }

        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));

        StringBuilder sb = new StringBuilder();

        // 1. Mahsulot nomi va turi
        sb.append("<b>").append(escapeHtml(productName)).append("</b>\n");
        sb.append("<i>").append(escapeHtml(productTypeName)).append("</i>\n\n");

        // 2. Tavsif (agar bor bo'lsa)
        if (description != null && !description.trim().isEmpty()) {
            sb.append(escapeHtml(description)).append("\n\n");
        }

        // 3. Birlik narxi
        sb.append("<b>Narxi:</b> ").append(formatPrice(price, language)).append("\n");

        // 4. Umumiy hisob-kitob
        sb.append("\n<b>").append(escapeHtml(productTypeName))
                .append(" * ").append(quantity)
                .append(" = ").append(formatPrice(totalPrice, language))
                .append("</b>\n\n");

        // 5. Umumiy narx
        sb.append("<b>Umumiy narxi:</b> ").append(formatPrice(totalPrice, language)).append("\n");

        // 6. Vaqt
        sb.append("\n").append(getCurrentTime());

        return sb.toString();
    }

    // Yana bir variant: Rasmda ko'rsatilgan formatga yaqinroq
    public String getProductInfoLikeImage(Language language, Product product,
                                          ProductType productType, int quantity) {

        String productName = getProductNameByLanguage(product, language);
        String productTypeName = getProductTypeNameByLanguage(productType, language);
        String description = getProductDescriptionByLanguage(product, language);

        BigDecimal price = productType.getPrice();
        Discount discount = product.getDiscount();

        // Chegirma mavjudligini tekshirish
        if (discount != null && discount.getType() != DiscountType.NONE) {
            price = calculateDiscountedPrice(price, discount);
        }

        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));

        StringBuilder sb = new StringBuilder();

        // 1. Mahsulot nomi - Variant
        sb.append("<b>").append(escapeHtml(productName))
                .append(" - ").append(escapeHtml(productTypeName))
                .append("</b>\n\n");

        // 2. Tavsif punktlari
        if (description != null && !description.trim().isEmpty()) {
            String[] points = description.split("\n");
            for (String point : points) {
                if (!point.trim().isEmpty()) {
                    sb.append("• ").append(escapeHtml(point.trim())).append("\n");
                }
            }
            sb.append("\n");
        }

        // 3. Narx
        sb.append("<b>Narxi:</b> ").append(formatPrice(price, language)).append("\n\n");

        // 4. Hisob-kitob
        sb.append("<b>")
                .append(escapeHtml(productName))
                .append("(").append(escapeHtml(productTypeName)).append(")")
                .append(" * ").append(quantity)
                .append(" = ").append(formatPrice(totalPrice, language))
                .append("</b>\n\n");

        // 5. Umumiy narx
        sb.append("<b>Umumiy narxi:</b> ").append(formatPrice(totalPrice, language)).append("\n\n");

        // 6. Vaqt va miqdor
        sb.append(getCurrentTime()).append("\n")
                .append(quantity);

        return sb.toString();
    }


    // Helper metodlar (oldingi kodlardan)
    private String getProductNameByLanguage(Product product, Language language) {
        return switch (language) {
            case CYRILLIC -> product.getNameCyr();
            case RUSSIAN -> product.getNameRu();
            case ENGLISH -> product.getNameEn();
            default -> product.getNameUz();
        };
    }

    private String getProductDescriptionByLanguage(Product product, Language language) {
        return switch (language) {
            case UZBEK -> product.getDescriptionUz();
            case CYRILLIC -> product.getDescriptionCyr();
            case RUSSIAN -> product.getDescriptionRu();
            case ENGLISH -> product.getDescriptionEn();
            default -> product.getDescriptionUz();
        };
    }

    private String getProductTypeNameByLanguage(ProductType productType, Language language) {
        return switch (language) {
            case UZBEK -> productType.getNameUz();
            case CYRILLIC -> productType.getNameCyr();
            case RUSSIAN -> productType.getNameRu();
            case ENGLISH -> productType.getNameEn();
            default -> productType.getNameUz();
        };
    }


    /*private BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, Discount discount) {
        if (discount == null || discount.getType() == DiscountType.NONE) {
            return originalPrice;
        }

        if (discount.getType() == DiscountType.PERCENT) {
            BigDecimal discountAmount = originalPrice.multiply(discount.getValue())
                    .divide(BigDecimal.valueOf(100));
            return originalPrice.subtract(discountAmount);
        } else if (discount.getType() == DiscountType.FIXED) {
            return originalPrice.subtract(discount.getValue());
        }

        return originalPrice;
    }*/
    private BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, Discount discount) {
        if (discount == null || discount.getType() == DiscountType.NONE) {
            return originalPrice;
        }

        if (discount.getType() == DiscountType.PERCENT) {
            // Foizni butun songa aylantirish
            BigDecimal discountPercentage = discount.getValue();

            // Agar foiz o'nli kasr bo'lsa (masalan 23.00), uni butun songa aylantiramiz
            if (discountPercentage.stripTrailingZeros().scale() <= 0) {
                // Zaten butun son
            } else {
                // O'nli kasrni butun songa yaxlitlash
                discountPercentage = discountPercentage.setScale(0, RoundingMode.HALF_UP);
            }

            BigDecimal discountAmount = originalPrice.multiply(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            return originalPrice.subtract(discountAmount);

        } else if (discount.getType() == DiscountType.FIXED) {
            return originalPrice.subtract(discount.getValue());
        }

        return originalPrice;
    }

    /*private String formatPrice(BigDecimal price, Language language) {
        return switch (language) {
            case UZBEK -> String.format("%,d so'm", price.intValue());
            case CYRILLIC -> String.format("%,d сўм", price.intValue());
            case RUSSIAN -> String.format("%,d сум", price.intValue());
            case ENGLISH -> String.format("%,d UZS", price.intValue());
            default -> String.format("%,d so'm", price.intValue());
        };
    }*/

    private String escapeHtml(String text) {
        if (text == null) return "";

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String getShopName(Shop shop, Language language) {
        if (language == Language.UZBEK) return shop.getNameUz();
        if (language == Language.RUSSIAN) return shop.getNameRu();
        if (language == Language.ENGLISH) return shop.getNameEn();
        return shop.getNameCyr();
    }

    public String productText(Language language) {
        return "\uD83D\uDECD️ " + (language == Language.UZBEK ? "Mahsulotlar: " :
                language == Language.RUSSIAN ? "Продукты: " :
                        language == CYRILLIC ? "Продукты:" :
                                "Products: "); // default
    }

    public String minimumCountOfProducts(Language language) {
        return switch (language) {
            case UZBEK -> "1 tadan kam bo'lishi mumkin emas";
            case CYRILLIC -> "1 тадан кам бўлиши мумкин эмас";
            case RUSSIAN -> "Нельзя меньше 1 штуки";
            case ENGLISH -> "Cannot be less than 1 item";
        };
    }

    public String productIsFinished(Language language, Integer stock, int requestedAmount) {
        if (stock == null) stock = 0;

        if (stock == 0) {
            return switch (language) {
                case UZBEK ->
                        "❌ Afsuski, bu mahsulot hozirda omborda yo‘q. Tez orada yana mavjud bo‘ladi. Iltimos, biroz kuting yoki boshqa ajoyib mahsulotlarimizni tanlang! 🌟";
                case CYRILLIC ->
                        "❌ Афсуски, бу махсулот ҳозирда омборда йўқ. Тез орада яна мавжуд бўлади. Илтимос, бироз кутиб туринг ёки бошқа ажойиб махсулотларимизни танланг! 🌟";
                case RUSSIAN ->
                        "❌ К сожалению, этот товар временно отсутствует на складе. Скоро он снова появится. Пожалуйста, подождите немного или выберите другой замечательный товар! 🌟";
                case ENGLISH ->
                        "❌ Unfortunately, this product is currently out of stock. It will be available again soon. Please wait a little or explore our other wonderful products! 🌟";
                default -> "❌ This product is currently out of stock. Please check back soon! 🌟";
            };
        } else if (requestedAmount > stock) {
            return switch (language) {
                case UZBEK ->
                        "⚠️ Diqqat! Siz %d ta mahsulotni tanladingiz, lekin omborda faqat %d ta mavjud. Iltimos, miqdorni o‘zgartiring 🛒".formatted(requestedAmount, stock);
                case CYRILLIC ->
                        "⚠️ Диққат! Сиз %d та махсулотни танладингиз, лекин омборда фақат %d та мавжуд. Илтимос, миқдорни ўзгартиринг 🛒".formatted(requestedAmount, stock);
                case RUSSIAN ->
                        "⚠️ Внимание! Вы выбрали %d товаров, но на складе доступно только %d. Пожалуйста, измените количество 🛒".formatted(requestedAmount, stock);
                case ENGLISH ->
                        "⚠️ Attention! You requested %d items, but only %d are available in stock. Please adjust the quantity 🛒".formatted(requestedAmount, stock);
                default ->
                        "⚠️ Only %d items are available in stock, you requested %d 🛒".formatted(stock, requestedAmount);
            };
        } else {
            return switch (language) {
                case UZBEK ->
                        "✅ Hozirda ushbu mahsulotdan omborda %d dona qolgan. Tez harakat qiling, imkoniyatni qo‘ldan boy bermang! ✨".formatted(stock);
                case CYRILLIC ->
                        "✅ Ҳозирда ушбу махсулотдан омборда %d дона қолган. Тез ҳаракат қилинг, имкониятни қўлдан бой берманг! ✨".formatted(stock);
                case RUSSIAN ->
                        "✅ На складе осталось всего %d шт. Поторопитесь, чтобы не упустить шанс! ✨".formatted(stock);
                case ENGLISH ->
                        "✅ Only %d items left in stock! Hurry up and don’t miss your chance! ✨".formatted(stock);
            };
        }
    }

    public String addedToBasket(Language language) {
        return switch (language) {
            case UZBEK -> "✅ Mahsulot savatga qo‘shildi!";
            case CYRILLIC -> "✅ Маҳсулот саватга қўшилди!";
            case RUSSIAN -> "✅ Товар добавлен в корзину!";
            default -> "✅ Product added to basket!";
        };
    }

    private String formatMoney(BigDecimal amount, Language language) {
        NumberFormat format;

        switch (language) {
            case UZBEK -> {
                format = NumberFormat.getInstance(new Locale("uz", "UZ"));
                format.setGroupingUsed(true);
                return format.format(amount) + " so'm";
            }
            case RUSSIAN, CYRILLIC -> {
                format = NumberFormat.getInstance(new Locale("ru", "RU"));
                format.setGroupingUsed(true);
                return format.format(amount) + " сум";
            }
            case ENGLISH -> {
                format = NumberFormat.getInstance(Locale.US);
                format.setGroupingUsed(true);
                return format.format(amount) + " UZS";
            }
            default -> {
                format = NumberFormat.getInstance();
                return format.format(amount);
            }
        }
    }

    public String myBaskets(Language language, List<Basket> baskets) {
        // Agar savat bo'sh bo'lsa
        if (baskets == null || baskets.isEmpty()) {
            return switch (language) {
                case UZBEK -> "Savat bo'sh";
                case CYRILLIC -> "Сават бўш";
                case RUSSIAN -> "Корзина пуста";
                case ENGLISH -> "Basket is empty";
            };
        }

        // Boshlang'ich matn
        StringBuilder sb = new StringBuilder();
        switch (language) {
            case UZBEK -> sb.append("Sizning savatingiz:\n\n");
            case CYRILLIC -> sb.append("Сизнинг саватингиз:\n\n");
            case RUSSIAN -> sb.append("Ваша корзина:\n\n");
            case ENGLISH -> sb.append("Your basket:\n\n");
        }

        int index = 1;
        for (Basket basket : baskets) {
            // Mahsulot nomini til bo'yicha olish
            String productName = getProductNameByLanguage(basket.getProductType().getProduct(), language);
            int quantity = basket.getQuantity();
            BigDecimal price = basket.getPrice();
            BigDecimal total = basket.getTotalPrice();

            // Har bir mahsulotni satrga qo'shish
            sb.append(index++)
                    .append(". ")
                    .append(productName)
                    .append(" | ")
                    .append(switch (language) {
                        case UZBEK -> "Miqdor";
                        case CYRILLIC -> "Миқдор";
                        case RUSSIAN -> "Кол-во";
                        case ENGLISH -> "Qty";
                    }).append(": ").append(quantity)
                    .append(" | ")
                    .append(switch (language) {
                        case UZBEK -> "Narx";
                        case CYRILLIC -> "Нарҳ";
                        case RUSSIAN -> "Цена";
                        case ENGLISH -> "Price";
                    }).append(": ").append(formatMoney(price, language))
                    .append(" | ")
                    .append(switch (language) {
                        case UZBEK -> "Jami";
                        case CYRILLIC -> "Жами";
                        case RUSSIAN -> "Итого";
                        case ENGLISH -> "Total";
                    }).append(": ").append(formatMoney(total, language))
                    .append("\n");
        }

        // Umumiy summa
        BigDecimal grandTotal = baskets.stream()
                .map(Basket::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        sb.append("\n").append(switch (language) {
            case UZBEK -> "Jami: ";
            case CYRILLIC -> "Жами: ";
            case RUSSIAN -> "Итого: ";
            case ENGLISH -> "Total: ";
        }).append(formatMoney(grandTotal, language));

        return sb.toString();
    }

    public String clearBasket(Language language) {
        return switch (language) {
            case UZBEK -> "🧹 Savat tozalandi";
            case CYRILLIC -> "🧹 Сават тозаланди";
            case RUSSIAN -> "🧹 Корзина очищена";
            case ENGLISH -> "🧹 Basket cleared";
        };
    }

    public String emptyProducts(Language language, Category category) {

        String categoryName = switch (language) {
            case UZBEK -> category.getNameUz();
            case CYRILLIC -> category.getNameCyr();
            case RUSSIAN -> category.getNameRu();
            case ENGLISH -> category.getNameEn();
        };

        return switch (language) {
            case UZBEK -> "📦 <b>" + categoryName + "</b> kategoriyasida hozircha mahsulotlar mavjud emas.\n\n" +
                    "🛍️ Siz boshqa kategoriyalardagi mahsulotlarni tanlashingiz mumkin.";

            case CYRILLIC -> "📦 <b>" + categoryName + "</b> категориясида ҳозирча маҳсулотлар мавжуд эмас.\n\n" +
                    "🛍️ Сиз бошқа категориялардаги маҳсулотларни танлашингиз мумкин.";

            case RUSSIAN -> "📦 В категории <b>" + categoryName + "</b> пока нет товаров.\n\n" +
                    "🛍️ Вы можете выбрать товары из других категорий.";

            case ENGLISH -> "📦 There are no products in the <b>" + categoryName + "</b> category yet.\n\n" +
                    "🛍️ You can choose products from other categories.";
        };
    }

    public String notActiveProduct(Product product, Language language) {
        return switch (language) {
            case UZBEK -> """
                    ⛔ Mahsulot vaqtincha faol emas
                    
                    🛍 Mahsulot: %s
                    ℹ️ Ushbu mahsulot hozirda sotuvda mavjud emas.
                    Iltimos, boshqa mahsulotlarni tanlashingiz mumkin.
                    """.formatted(product.getNameUz());

            case CYRILLIC -> """
                    ⛔ Маҳсулот вақтинча фаол эмас
                    
                    🛍 Маҳсулот: %s
                    ℹ️ Ушбу маҳсулот ҳозирда сотувда мавжуд эмас.
                    Илтимос, бошқа маҳсулотларни танлашингиз мумкин.
                    """.formatted(product.getNameCyr());

            case RUSSIAN -> """
                    ⛔ Товар временно недоступен
                    
                    🛍 Товар: %s
                    ℹ️ Данный товар сейчас недоступен для продажи.
                    Пожалуйста, выберите другой товар.
                    """.formatted(product.getNameRu());

            case ENGLISH -> """
                    ⛔ Product is temporarily unavailable
                    
                    🛍 Product: %s
                    ℹ️ This product is currently not available for sale.
                    Please feel free to choose another product.
                    """.formatted(product.getNameEn());
        };
    }

    public String chooseDeliveryType(Language language) {
        return switch (language) {
            case UZBEK -> "🚚 Yetkazib berish turini tanlang ⬇️";
            case CYRILLIC -> "🚚 Етказиб бериш турини танланг ⬇️";
            case RUSSIAN -> "🚚 Выберите способ доставки ⬇️";
            case ENGLISH -> "🚚 Choose delivery type ⬇️";
        };
    }

    public String enterPhoneNumberForOrder(Language language) {
        return switch (language) {
            case UZBEK -> """
                    📞 Iltimos, buyurtma uchun telefon raqamingizni kiriting
                    
                    Masalan: +998901234567""";
            case CYRILLIC -> """
                    📞 Илтимос, буюртма учун телефон рақамингизни киритинг
                    
                    Масалан: +998901234567""";
            case RUSSIAN -> """
                    📞 Пожалуйста, введите номер телефона для заказа
                    
                    Например: +998901234567""";
            case ENGLISH -> """
                    📞 Please enter your phone number for the order
                    
                    Example: +998901234567""";
        };
    }

    private String getShopAddressLabel(Language language) {
        return switch (language) {
            case UZBEK -> "Manzil";
            case CYRILLIC -> "Манзил";
            case RUSSIAN -> "Адрес";
            case ENGLISH -> "Address";
        };
    }

    /*public String isSuccessCreateOrder(
            BotCustomer user,
            List<CreateShopOrderRequestDto> orders
    ) {
        String deliveryType = user.getDeliveryType();
//        deliveryType =  PICKUP,DELIVERY



        Language language = user.getLanguage();
        Double latitude = user.getLatitude();
        Double longitude = user.getLongitude();
        StringBuilder text = new StringBuilder();
        BigDecimal grandTotal = BigDecimal.ZERO;
        int shopIndex = 1;

        for (CreateShopOrderRequestDto order : orders) {

            Shop shop = shopRepository.findById(order.getShopId())
                    .orElseThrow(() -> new RuntimeException("Shop not found"));

            BigDecimal shopTotal = BigDecimal.ZERO;

            // ===== SHOP TITLE =====
            text.append("\n")
                    .append(shopIndex).append(") <b>")
                    .append(getShopName(shop, language))
                    .append("</b>\n");

// ===== SHOP ADDRESS =====
            if (shop.getHasLocation() != null && shop.getHasLocation()
                    && shop.getAddress() != null && !shop.getAddress().isBlank()) {

                text.append("  📍 <b>")
                        .append(getShopAddressLabel(language))
                        .append(":</b> ")
                        .append(shop.getAddress())
                        .append("\n");
            }

// ===== DISTANCE =====
            if (shop.getLatitude() != null && shop.getLongitude() != null) {

                double distanceKm = calculateDistanceKm(
                        latitude,
                        longitude,
                        shop.getLatitude(),
                        shop.getLongitude()
                );

                text.append("  📏 <b>")
                        .append(getDistanceLabel(language))
                        .append(":</b> ")
                        .append(String.format("%.1f km", distanceKm))
                        .append("\n");
            }
            int itemIndex = 1;

            for (CreateOrderItemRequestDto item : order.getItems()) {

                ProductType productType = productTypeRepository.findById(item.getProductTypeId())
                        .orElseThrow(() -> new RuntimeException("ProductType not found"));

                Product product = productType.getProduct();
                Discount discount = product.getDiscount();

                BigDecimal basePrice = productType.getPrice();
                BigDecimal finalPrice = basePrice;

                // ===== DISCOUNT =====
                if (discount != null) {
                    if (discount.getType().name().equals("PERCENT")) {
                        BigDecimal percent = discount.getValue();
                        finalPrice = basePrice.subtract(
                                basePrice.multiply(percent).divide(BigDecimal.valueOf(100))
                        );
                    } else {
                        finalPrice = basePrice.subtract(discount.getValue());
                    }
                }

                BigDecimal lineTotal = finalPrice.multiply(
                        BigDecimal.valueOf(item.getQuantity())
                );

                shopTotal = shopTotal.add(lineTotal);

                // ===== PRODUCT LINE =====
                text.append("  ")
                        .append(itemIndex).append(") ")
                        .append(getProductName(product, language))
                        .append(" (")
                        .append(getProductTypeName(productType, language))
                        .append(") ");

                if (discount != null) {
                    text.append("<s>")
                            .append(formatPrice(basePrice, language))
                            .append("</s> ")
                            .append(formatPrice(finalPrice, language));
                } else {
                    text.append(formatPrice(basePrice, language));
                }

                text.append(" × ")
                        .append(item.getQuantity())
                        .append(" = <b>")
                        .append(formatPrice(lineTotal, language))
                        .append("</b>\n");

                itemIndex++;
            }

            // ===== DELIVERY =====
            if (order.getDeliveryType() != DeliveryType.PICKUP) {

                BigDecimal deliveryPrice =
                        order.getDeliveryType() == DeliveryType.DELIVERY_OUTSIDE
                                ? shop.getDeliveryOutsidePrice()
                                : shop.getDeliveryPrice();

                shopTotal = shopTotal.add(deliveryPrice);

                text.append("  🚚 ")
                        .append(getDeliveryText(order.getDeliveryType(), language))
                        .append(": <b>")
                        .append(formatPrice(deliveryPrice, language))
                        .append("</b>\n");
            }

            // ===== SHOP TOTAL =====
            text.append("  🧾 <b>")
                    .append(getShopTotalText(language))
                    .append(": ")
                    .append(formatPrice(shopTotal, language))
                    .append("</b>\n");

            grandTotal = grandTotal.add(shopTotal);
            shopIndex++;
        }

        // ===== GRAND TOTAL =====
        text.append("\n💰 <b>")
                .append(getGrandTotalText(language))
                .append(": ")
                .append(formatPrice(grandTotal, language))
                .append("</b>");

        return text.toString();
    }*/
    public String isSuccessCreateOrder(
            BotCustomer user,
            List<CreateShopOrderRequestDto> orders
    ) {
        String deliveryType = user.getDeliveryType(); // "PICKUP" yoki "DELIVERY"
        Language language = user.getLanguage();
        Double latitude = user.getLatitude();
        Double longitude = user.getLongitude();
        StringBuilder text = new StringBuilder();
        BigDecimal grandTotal = BigDecimal.ZERO;
        int shopIndex = 1;

        for (CreateShopOrderRequestDto order : orders) {
            Shop shop = shopRepository.findById(order.getShopId())
                    .orElseThrow(() -> new RuntimeException("Shop not found"));

            BigDecimal shopTotal = BigDecimal.ZERO;

            // ===== SHOP TITLE =====
            text.append("\n")
                    .append(shopIndex).append(") <b>")
                    .append(getShopName(shop, language))
                    .append("</b>\n");

            // ===== SHOP ADDRESS =====
            if (shop.getHasLocation() != null && shop.getHasLocation()
                    && shop.getAddress() != null && !shop.getAddress().isBlank()) {
                text.append("  📍 <b>")
                        .append(getShopAddressLabel(language))
                        .append(":</b> ")
                        .append(shop.getAddress())
                        .append("\n");
            }

            // ===== DISTANCE =====
            if (shop.getLatitude() != null && shop.getLongitude() != null
                    && latitude != null && longitude != null) {
                double distanceKm = calculateDistanceKm(
                        latitude,
                        longitude,
                        shop.getLatitude(),
                        shop.getLongitude()
                );

                text.append("  📏 <b>")
                        .append(getDistanceLabel(language))
                        .append(":</b> ")
                        .append(String.format("%.1f km", distanceKm))
                        .append("\n");
            }

            // ===== DELIVERY TYPE VALIDATION =====
            // Agar user delivery type'ni DELIVERY tanlagan, lekin shop delivery qilmasa
            if ("DELIVERY".equals(deliveryType)) {
                if (order.getDeliveryType() == null) {
                    // Shop delivery qilmaydi - PICKUP ga o'tkazish
                    order.setDeliveryType(DeliveryType.PICKUP);

                    String message = switch (language) {
                        case UZBEK -> "Kechirasiz, bu do'kon sizning manzilingizga yetkazib bera olmaydi. " +
                                "Buyurtmani do'kondan o'zingiz olib ketishingiz kerak.";
                        case CYRILLIC -> "Кечирасиз, бу дўкон сизнинг манзилингизга етказиб бера олмайди. " +
                                "Буюртмани дўкондан ўзингиз олиб кетишингиз керак.";
                        case RUSSIAN -> "Извините, этот магазин не может доставить на ваш адрес. " +
                                "Вам нужно забрать заказ из магазина самостоятельно.";
                        case ENGLISH -> "Sorry, this shop cannot deliver to your address. " +
                                "You need to pick up the order from the shop yourself.";
                    };

                    text.append("  ⚠️ <i>").append(message).append("</i>\n");
                }
            }

            // ===== DELIVERY AVAILABILITY CHECK =====
            // Shop delivery qilish imkoniyatini tekshirish
            if (order.getDeliveryType() != null && order.getDeliveryType() != DeliveryType.PICKUP) {
                boolean canDeliver = false;

                if (order.getDeliveryType() == DeliveryType.DELIVERY_INSIDE) {
                    canDeliver = shop.getHasDelivery() != null && shop.getHasDelivery();
                } else if (order.getDeliveryType() == DeliveryType.DELIVERY_OUTSIDE) {
                    canDeliver = shop.getHasOutsideDelivery() != null && shop.getHasOutsideDelivery();
                }

                if (!canDeliver) {
                    // Yetkazib berish mumkin emas - PICKUP ga o'tkazish
                    order.setDeliveryType(DeliveryType.PICKUP);

                    String message = switch (language) {
                        case UZBEK -> "⚠️ Eslatma: Bu do'kon yetkazib berish xizmatini ko'rsatmaydi. " +
                                "Buyurtmani do'kondan olib ketishingiz kerak.";
                        case CYRILLIC -> "⚠️ Эслатма: Бу дўкон етказиб бериш хизматини кўрсатмайди. " +
                                "Буюртмани дўкондан олиб кетишингиз керак.";
                        case RUSSIAN -> "⚠️ Напоминание: Этот магазин не предоставляет услугу доставки. " +
                                "Вам нужно забрать заказ из магазина.";
                        case ENGLISH -> "⚠️ Note: This shop does not offer delivery service. " +
                                "You need to pick up the order from the shop.";
                    };

                    text.append("  ").append(message).append("\n");
                }
            }

            int itemIndex = 1;
            for (CreateOrderItemRequestDto item : order.getItems()) {
                ProductType productType = productTypeRepository.findById(item.getProductTypeId())
                        .orElseThrow(() -> new RuntimeException("ProductType not found"));

                Product product = productType.getProduct();
                Discount discount = product.getDiscount();

                BigDecimal basePrice = productType.getPrice();
                BigDecimal finalPrice = basePrice;

                // ===== DISCOUNT =====
                if (discount != null) {
                    if (discount.getType().name().equals("PERCENT")) {
                        BigDecimal percent = discount.getValue();
                        finalPrice = basePrice.subtract(
                                basePrice.multiply(percent).divide(BigDecimal.valueOf(100))
                        );
                    } else {
                        finalPrice = basePrice.subtract(discount.getValue());
                    }
                }

                BigDecimal lineTotal = finalPrice.multiply(
                        BigDecimal.valueOf(item.getQuantity())
                );

                shopTotal = shopTotal.add(lineTotal);

                // ===== PRODUCT LINE =====
                text.append("  ")
                        .append(itemIndex).append(") ")
                        .append(getProductName(product, language))
                        .append(" (")
                        .append(getProductTypeName(productType, language))
                        .append(") ");

                if (discount != null) {
                    text.append("<s>")
                            .append(formatPrice(basePrice, language))
                            .append("</s> ")
                            .append(formatPrice(finalPrice, language));
                } else {
                    text.append(formatPrice(basePrice, language));
                }

                text.append(" × ")
                        .append(item.getQuantity())
                        .append(" = <b>")
                        .append(formatPrice(lineTotal, language))
                        .append("</b>\n");

                itemIndex++;
            }

            // ===== DELIVERY COST (faqat yetkazib berish mumkin bo'lsa) =====
            if (order.getDeliveryType() != null && order.getDeliveryType() != DeliveryType.PICKUP) {
                BigDecimal deliveryPrice = BigDecimal.ZERO;
                boolean hasDelivery = false;

                if (order.getDeliveryType() == DeliveryType.DELIVERY_INSIDE
                        && shop.getHasDelivery() != null && shop.getHasDelivery()) {
                    deliveryPrice = shop.getDeliveryPrice() != null ? shop.getDeliveryPrice() : BigDecimal.ZERO;
                    hasDelivery = true;
                } else if (order.getDeliveryType() == DeliveryType.DELIVERY_OUTSIDE
                        && shop.getHasOutsideDelivery() != null && shop.getHasOutsideDelivery()) {
                    deliveryPrice = shop.getDeliveryOutsidePrice() != null ? shop.getDeliveryOutsidePrice() : BigDecimal.ZERO;
                    hasDelivery = true;
                }

                if (hasDelivery && deliveryPrice.compareTo(BigDecimal.ZERO) > 0) {
                    shopTotal = shopTotal.add(deliveryPrice);

                    text.append("  🚚 ")
                            .append(getDeliveryText(order.getDeliveryType(), language))
                            .append(": <b>")
                            .append(formatPrice(deliveryPrice, language))
                            .append("</b>\n");

                    // ===== DELIVERY INFO DETAILS =====
                    String deliveryInfo = getDeliveryInfo(shop, language);
                    if (!deliveryInfo.isEmpty()) {
                        text.append("  ℹ️ <i>")
                                .append(deliveryInfo)
                                .append("</i>\n");
                    }
                }
            }

            // ===== SHOP TOTAL =====
            text.append("  🧾 <b>")
                    .append(getShopTotalText(language))
                    .append(": ")
                    .append(formatPrice(shopTotal, language))
                    .append("</b>\n");

            grandTotal = grandTotal.add(shopTotal);
            shopIndex++;
        }

        // ===== GRAND TOTAL =====
        text.append("\n💰 <b>")
                .append(getGrandTotalText(language))
                .append(": ")
                .append(formatPrice(grandTotal, language))
                .append("</b>");

        // ===== FINAL DELIVERY SUMMARY =====
        boolean hasAnyDelivery = orders.stream()
                .anyMatch(order -> order.getDeliveryType() != null
                        && order.getDeliveryType() != DeliveryType.PICKUP);

        if (!hasAnyDelivery) {
            text.append("\n\n");
            String pickupNote = switch (language) {
                case UZBEK -> "📦 <b>Eslatma:</b> Barcha buyurtmalar do'konlardan o'zingiz olib ketishingiz kerak.";
                case CYRILLIC -> "📦 <b>Эслатма:</b> Барча буюртмалар дўконлардан ўзингиз олиб кетишингиз керак.";
                case RUSSIAN -> "📦 <b>Напоминание:</b> Все заказы нужно забрать из магазинов самостоятельно.";
                case ENGLISH -> "📦 <b>Note:</b> All orders need to be picked up from the shops yourself.";
            };
            text.append(pickupNote);
        }

        return text.toString();
    }

    private String getDeliveryInfo(Shop shop, Language language) {
        String deliveryInfo = switch (language) {
            case UZBEK -> shop.getDeliveryInfoUz();
            case CYRILLIC -> shop.getDeliveryInfoCyr();
            case RUSSIAN -> shop.getDeliveryInfoRu();
            case ENGLISH -> shop.getDeliveryInfoEn();
        };

        if (deliveryInfo == null || deliveryInfo.trim().isEmpty()) {
            // Agar maxsus delivery info bo'lmasa, default message
            return switch (language) {
                case UZBEK -> "Yetkazib berish shartlari va vaqtlari do'kon tomonidan belgilanadi";
                case CYRILLIC -> "Етказиб бериш шартлари ва вақтлари дўкон томонидан белгиланади";
                case RUSSIAN -> "Условия и сроки доставки определяются магазином";
                case ENGLISH -> "Delivery conditions and times are determined by the shop";
            };
        }

        return deliveryInfo;
    }

    private String getDistanceLabel(Language language) {
        return switch (language) {
            case UZBEK -> "Masofa";
            case CYRILLIC -> "Масофа";
            case RUSSIAN -> "Расстояние";
            case ENGLISH -> "Distance";
        };
    }

    private double calculateDistanceKm(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        final int EARTH_RADIUS = 6371; // km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }


    private String getProductName(Product product, Language language) {
        return switch (language) {
            case UZBEK -> product.getNameUz();
            case CYRILLIC -> product.getNameCyr();
            case RUSSIAN -> product.getNameRu();
            case ENGLISH -> product.getNameEn();
        };
    }

    private String getProductTypeName(ProductType pt, Language language) {
        return switch (language) {
            case UZBEK -> pt.getNameUz();
            case CYRILLIC -> pt.getNameCyr();
            case RUSSIAN -> pt.getNameRu();
            case ENGLISH -> pt.getNameEn();
        };
    }

    private String getDeliveryText(DeliveryType type, Language language) {
        return switch (language) {
            case UZBEK -> type.getDescriptionUz();
            case CYRILLIC -> type.getDescriptionCyr();
            case RUSSIAN -> type.getDescriptionRu();
            case ENGLISH -> type.getDescriptionEn();
        };
    }

    private String getShopTotalText(Language language) {
        return switch (language) {
            case UZBEK -> "Do'kon bo'yicha jami";
            case CYRILLIC -> "Дўкон бўйича жами";
            case RUSSIAN -> "Итого по магазину";
            case ENGLISH -> "Shop total";
        };
    }

    private String getGrandTotalText(Language language) {
        return switch (language) {
            case UZBEK -> "Umumiy summa";
            case CYRILLIC -> "Умумий сумма";
            case RUSSIAN -> "Общая сумма";
            case ENGLISH -> "Grand total";
        };
    }

    public String cancelOrder(Language language) {
        if (language == null) language = Language.UZBEK; // default til
        return switch (language) {
            case UZBEK -> "Buyurtmangiz bekor qilindi ❌";
            case CYRILLIC -> "Буюртмангиз бекор қилинди ❌";
            case RUSSIAN -> "Ваш заказ отменён ❌";
            case ENGLISH -> "Your order has been cancelled ❌";
            default -> "Buyurtmangiz bekor qilingan ❌";
        };
    }

    public String emptyOrders(Language language) {
        return switch (language) {
            case UZBEK -> "📦 Sizda hozircha hech qanday buyurtma mavjud emas!\n\n" +
                    "🛒 Yangilarini qo'shishni xohlaysizmi?\n" +
                    "💡 Mahsulotlarimizni ko‘rib chiqish uchun menyudan tanlang va birinchi buyurtmangizni bering!";
            case CYRILLIC -> "📦 Сизда ҳозирча ҳеч қандай буюртма мавжуд эмас!\n\n" +
                    "🛒 Яниларини қўшмоқчимисиз?\n" +
                    "💡 Маҳсулотларимизни кўриб чиқиш учун менюдан танланг ва биринчи буюртмангизни беринг!";
            case RUSSIAN -> "📦 У вас пока нет заказов!\n\n" +
                    "🛒 Хотите оформить новый заказ?\n" +
                    "💡 Просмотрите наши товары в меню и сделайте первый заказ!";
            case ENGLISH -> "📦 You currently have no orders!\n\n" +
                    "🛒 Want to make a new one?\n" +
                    "💡 Browse our products from the menu and place your first order!";
            default -> "📦 No orders yet!\n\n🛒 Check out our products and place your first order!";
        };
    }

    public String orderInformation(Language language, ShopOrder order) {
        if (order == null) return emptyOrders(language);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            List<OrderItem> orderItems = orderItemRepository.findByShopOrder(order);

            // Null tekshiruvlari
            if (order.getSeller() == null) {
                return getErrorMessage(language, "Foydalanuvchi topilmadi", "Пользователь не найден",
                        "Пользователь не найден", "User not found");
            }

            User user = userRepository.findById(order.getSeller().getUserid()).orElse(null);
            if (user == null) {
                return getErrorMessage(language, "Foydalanuvchi topilmadi", "Пользователь не найден",
                        "Пользователь не найден", "User not found");
            }

            // Shop ma'lumotlari
            Shop shop = order.getShop();
            if (shop == null) {
                return getErrorMessage(language, "Do'kon topilmadi", "Магазин не найден",
                        "Магазин не найден", "Shop not found");
            }

            // Status va delivery ma'lumotlari
            String status = getStatusText(language, order.getStatus(), order.getDeliveryType());
            String statusColor = getStatusColor(order.getStatus());
            String delivery = getDeliveryText(language, order.getDeliveryType());

            return switch (language) {
                case UZBEK -> buildOrderHTML(language, order, orderItems, user, shop,
                        status, statusColor, delivery, formatter);
                case CYRILLIC -> buildOrderHTML(language, order, orderItems, user, shop,
                        status, statusColor, delivery, formatter);
                case RUSSIAN -> buildOrderHTML(language, order, orderItems, user, shop,
                        status, statusColor, delivery, formatter);
                case ENGLISH -> buildOrderHTML(language, order, orderItems, user, shop,
                        status, statusColor, delivery, formatter);
                default -> "<b>❌ " + getErrorMessageByLanguage(language) + "</b>";
            };
        } catch (Exception e) {
            return getErrorMessage(language,
                    "Ma'lumotlarni olishda xatolik: " + e.getMessage(),
                    "Ошибка при получении данных: " + e.getMessage(),
                    "Ошибка при получении данных: " + e.getMessage(),
                    "Error retrieving data: " + e.getMessage());
        }
    }

// ==================== YORDAMCHI METODLAR ====================

    private String getErrorMessage(Language lang, String uz, String cyr, String ru, String en) {
        return switch (lang) {
            case UZBEK -> "❌ <b>Xatolik:</b> " + uz;
            case CYRILLIC -> "❌ <b>Хатолик:</b> " + cyr;
            case RUSSIAN -> "❌ <b>Ошибка:</b> " + ru;
            case ENGLISH -> "❌ <b>Error:</b> " + en;
        };
    }

    private String getErrorMessageByLanguage(Language lang) {
        return switch (lang) {
            case UZBEK -> "Buyurtma ma'lumotlari mavjud emas";
            case CYRILLIC -> "Буюртма маълумотлари мавжуд эмас";
            case RUSSIAN -> "Информация о заказе недоступна";
            case ENGLISH -> "No order information available";
        };
    }

    private String getStatusColor(ShopOrderStatus status) {
        if (status == null) return "⚪";

        return switch (status) {
            case NEW -> "🟡";
            case PAYMENT_COMPLETED -> "🟢";
            case ACCEPTED -> "🔵";
            case PREPARING -> "🟣";
            case SENT -> "🟠";
            case COMPLETED -> "✅";
            case CANCELLED -> "❌";
            default -> "⚪";
        };
    }

    private String getStatusText(Language lang, ShopOrderStatus status, DeliveryType type) {
        if (status == null) return "";

        return switch (lang) {
            case UZBEK -> status.getDescriptionUz(type);
            case CYRILLIC -> status.getDescriptionCyr(type);
            case RUSSIAN -> status.getDescriptionRu(type);
            case ENGLISH -> status.getDescriptionEn(type);
        };
    }

    private String getDeliveryText(Language lang, DeliveryType deliveryType) {
        if (deliveryType == null) return "";

        return switch (lang) {
            case UZBEK -> deliveryType.getDescriptionUz();
            case CYRILLIC -> deliveryType.getDescriptionCyr();
            case RUSSIAN -> deliveryType.getDescriptionRu();
            case ENGLISH -> deliveryType.getDescriptionEn();
        };
    }

// ==================== ASOSIY BUILD METODI ====================

    private String buildOrderHTML(
            Language language,
            ShopOrder order,
            List<OrderItem> orderItems,
            User user,
            Shop shop,
            String status,
            String statusColor,
            String delivery,
            DateTimeFormatter formatter
    ) {
        StringBuilder html = new StringBuilder();

        // Sarlavha
        html.append(getTitle(language))
                .append("\n═══════════════════════\n\n");

        // Asosiy ma'lumotlar
        html.append(buildBasicInfo(language, order, shop, user, status, statusColor, delivery));

        // PICKUP va SENT holati uchun xabar (MUHIM QISM)
        if (order.getDeliveryType() == DeliveryType.PICKUP &&
                order.getStatus() == ShopOrderStatus.SENT) {
            html.append("\n")
                    .append(pickupReadyMessage(language, shop))
                    .append("\n");
        }

        // Manzil (faqat delivery uchun)
        if (isDelivery(order.getDeliveryType()) &&
                order.getAddress() != null &&
                !order.getAddress().isEmpty()) {
            html.append(getAddressLabel(language))
                    .append(order.getAddress())
                    .append("\n");
        }

        // Mahsulotlar ro'yxati
        html.append("\n")
                .append(getProductsLabel(language))
                .append("\n────────────────\n");

        if (orderItems.isEmpty()) {
            html.append(getNoProductsMessage(language))
                    .append("\n");
        } else {
            html.append(buildProductsList(language, orderItems));
        }

        // To'lov ma'lumotlari
        html.append(getPaymentLabel(language))
                .append("\n────────────────\n");

        html.append(buildPaymentInfo(language, order));

        // Vaqt ma'lumotlari
        html.append(getTimeLabel(language))
                .append("\n────────────────\n");

        html.append(buildTimeInfo(language, order, formatter));

        return html.toString();
    }

// ==================== BUILD KOMPONENTLARI ====================

    private String getTitle(Language lang) {
        return switch (lang) {
            case UZBEK -> "<b>📄 BUYURTMA TAFSILOTLARI</b>";
            case CYRILLIC -> "<b>📄 БУЮРТМА ТАФСИЛОТЛАРИ</b>";
            case RUSSIAN -> "<b>📄 ДЕТАЛИ ЗАКАЗА</b>";
            case ENGLISH -> "<b>📄 ORDER DETAILS</b>";
        };
    }

    private String buildBasicInfo(Language lang, ShopOrder order, Shop shop,
                                  User user, String status, String statusColor, String delivery) {
        StringBuilder info = new StringBuilder();

        // Buyurtma ID
        info.append("<b>🆔 ").append(getOrderIdLabel(lang)).append(":</b> <code>")
                .append(order.getPkey() != null ? order.getPkey() : "N/A")
                .append("</code>\n");

        // Mijoz
        String customerName = (order.getCustomer() != null && order.getCustomer().getFirstName() != null)
                ? order.getCustomer().getFirstName()
                : getUnknownText(lang);
        info.append("<b>👤 ").append(getCustomerLabel(lang)).append(":</b> ")
                .append(customerName).append("\n");

        // Telefon
        info.append("<b>📞 ").append(getPhoneLabel(lang)).append(":</b> <code>")
                .append(order.getPhone() != null ? order.getPhone() : "N/A")
                .append("</code>\n");

        // Do'kon
        String shopName = getShopName(lang, shop);
        info.append("<b>🏪 ").append(getShopLabel(lang)).append(":</b> ")
                .append(shopName).append("\n");

        // Sotuvchi
        info.append("<b>🛍 ").append(getSellerLabel(lang)).append(":</b> ")
                .append(user.getFullName() != null ? user.getFullName() : getUnknownText(lang))
                .append("\n");

        // Status
        info.append("<b>📦 ").append(getStatusLabel(lang)).append(":</b> ")
                .append(statusColor).append(" ").append(status).append("\n");

        // Yetkazib berish turi
        info.append("<b>🚚 ").append(getDeliveryLabel(lang)).append(":</b> ")
                .append(delivery).append("\n");

        return info.toString();
    }

    private String buildProductsList(Language lang, List<OrderItem> orderItems) {
        StringBuilder products = new StringBuilder();

        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            ProductType pt = item.getProductType();

            // Mahsulot nomi
            String productName = getProductName(lang, pt);

            products.append("<b>")
                    .append(i + 1)
                    .append(".</b> ")
                    .append(productName)
                    .append(" - ")
                    .append(item.getQuantity())
                    .append(" ").append(getUnitLabel(lang)).append("\n");

            // Narx
            products.append("   ").append(getPriceLabel(lang)).append(": ")
                    .append(formatPrice(item.getPrice(), lang))
                    .append("\n");

            // Summa
            products.append("   ").append(getTotalLabel(lang)).append(": <b>")
                    .append(formatPrice(item.getTotalPrice(), lang))
                    .append("</b>\n\n");
        }

        return products.toString();
    }

    private String buildPaymentInfo(Language lang, ShopOrder order) {
        StringBuilder payment = new StringBuilder();

        BigDecimal subtotal = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal deliveryPrice = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(deliveryPrice);

        // Mahsulotlar summasi
        payment.append(getProductsTotalLabel(lang)).append(": ")
                .append(formatPrice(subtotal, lang)).append("\n");

        // Yetkazib berish narxi (faqat delivery bo'lsa)
        if (isDelivery(order.getDeliveryType()) && deliveryPrice.compareTo(BigDecimal.ZERO) > 0) {
            payment.append(getDeliveryPriceLabel(lang)).append(": ")
                    .append(formatPrice(deliveryPrice, lang)).append("\n");
        }

        // Jami
        payment.append("<b>").append(getTotalAmountLabel(lang)).append(":</b> <b>")
                .append(formatPrice(total, lang))
                .append("</b>\n\n");

        return payment.toString();
    }

    private String buildTimeInfo(Language lang, ShopOrder order, DateTimeFormatter formatter) {
        StringBuilder time = new StringBuilder();

        time.append(getCreatedLabel(lang)).append(": ")
                .append(order.getCreatedAt().format(formatter))
                .append("\n");

        if (order.getUpdatedAt() != null) {
            time.append(getUpdatedLabel(lang)).append(": ")
                    .append(order.getUpdatedAt().format(formatter))
                    .append("\n");
        }

        return time.toString();
    }

// ==================== PICKUP READY MESSAGE ====================

    private String pickupReadyMessage(Language language, Shop shop) {
        return switch (language) {
            case UZBEK -> "✅ <b>‼️ E'TIBORLI BO'LING ‼️</b>\n" +
                    "📦 <b>Buyurtmangiz olib ketish uchun tayyor!</b>\n" +
                    "🏪 Do'konimizga kelib buyurtmangizni olib ketishingiz mumkin.\n" +
                    "⏰ Ish vaqti: " + getShopWorkingHours(language, shop);

            case CYRILLIC -> "✅ <b>‼️ ЭЪТИБОРЛИ БЎЛИНГ ‼️</b>\n" +
                    "📦 <b>Буюртмангиз олиб кетиш учун тайёр!</b>\n" +
                    "🏪 Дўконимизга келиб буюртмангизни олиб кетишингиз мумкин.\n" +
                    "⏰ Иш вақти: " + getShopWorkingHours(language, shop);

            case RUSSIAN -> "✅ <b>‼️ ВНИМАНИЕ ‼️</b>\n" +
                    "📦 <b>Ваш заказ готов к самовывозу!</b>\n" +
                    "🏪 Вы можете забрать заказ в нашем магазине.\n" +
                    "⏰ Время работы: " + getShopWorkingHours(language, shop);

            case ENGLISH -> "✅ <b>‼️ ATTENTION ‼️</b>\n" +
                    "📦 <b>Your order is ready for pickup!</b>\n" +
                    "🏪 You can collect your order from our shop.\n" +
                    "⏰ Working hours: " + getShopWorkingHours(language, shop);
        };
    }

    private String getShopWorkingHours(Language language, Shop shop) {
        if (shop == null) {
            return getDefaultWorkingHours(language);
        }

        LocalTime startTime = shop.getWorkStartTime();
        LocalTime endTime = shop.getWorkEndTime();
        List<WorkDay> workDays = shop.getWorkDays();

        // Agar ish vaqtlari yoki ish kunlari bo'lmasa
        if (startTime == null || endTime == null || workDays == null || workDays.isEmpty()) {
            return getDefaultWorkingHours(language);
        }

        // Ish vaqtlarini formatlash
        String timeRange = formatTime(startTime, endTime);

        // Ish kunlarini formatlash
        String daysRange = formatWorkDays(workDays, language);

        // Tilga mos matnni qaytarish
        return switch (language) {
            case UZBEK -> daysRange + " " + timeRange;
            case CYRILLIC -> daysRange + " " + timeRange;
            case RUSSIAN -> daysRange + " " + timeRange;
            case ENGLISH -> timeRange + " on " + daysRange;
        };
    }

    private String getDefaultWorkingHours(Language language) {
        return switch (language) {
            case UZBEK -> "09:00 - 18:00 (har kuni)";
            case CYRILLIC -> "09:00 - 18:00 (хар куни)";
            case RUSSIAN -> "09:00 - 18:00 (ежедневно)";
            case ENGLISH -> "09:00 - 18:00 (daily)";
        };
    }

    private String formatTime(LocalTime startTime, LocalTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String start = startTime.format(formatter);
        String end = endTime.format(formatter);
        return start + " - " + end;
    }

    private String formatWorkDays(List<WorkDay> workDays, Language language) {
        if (workDays == null || workDays.isEmpty()) {
            return getEveryDayText(language);
        }

        // Agar barcha 7 kun ish kuni bo'lsa
        if (workDays.size() == 7) {
            return getEveryDayText(language);
        }

        // Agar hafta oxiri ish kuni bo'lmasa
        boolean hasWeekend = workDays.contains(WorkDay.SATURDAY) || workDays.contains(WorkDay.SUNDAY);
        if (workDays.size() == 5 && !hasWeekend) {
            return switch (language) {
                case UZBEK -> "dushanbadan jumagacha";
                case CYRILLIC -> "душанбадан жумагача";
                case RUSSIAN -> "с понедельника по пятницу";
                case ENGLISH -> "Monday to Friday";
            };
        }

        // Alohida kunlarni formatlash
        List<String> dayNames = workDays.stream()
                .map(day -> getDayName(day, language))
                .collect(Collectors.toList());

        if (dayNames.size() == 1) {
            return dayNames.get(0);
        } else if (dayNames.size() == 2) {
            return dayNames.get(0) + " va " + dayNames.get(1);
        } else {
            String lastDay = dayNames.remove(dayNames.size() - 1);
            return String.join(", ", dayNames) + " va " + lastDay;
        }
    }

    private String getEveryDayText(Language language) {
        return switch (language) {
            case UZBEK -> "har kuni";
            case CYRILLIC -> "хар куни";
            case RUSSIAN -> "ежедневно";
            case ENGLISH -> "daily";
        };
    }

    private String getDayName(WorkDay day, Language language) {
        return switch (language) {
            case UZBEK -> switch (day) {
                case MONDAY -> "dushanba";
                case TUESDAY -> "seshanba";
                case WEDNESDAY -> "chorshanba";
                case THURSDAY -> "payshanba";
                case FRIDAY -> "juma";
                case SATURDAY -> "shanba";
                case SUNDAY -> "yakshanba";
            };
            case CYRILLIC -> switch (day) {
                case MONDAY -> "душанба";
                case TUESDAY -> "сешанба";
                case WEDNESDAY -> "чоршанба";
                case THURSDAY -> "пайшанба";
                case FRIDAY -> "жума";
                case SATURDAY -> "шанба";
                case SUNDAY -> "якшанба";
            };
            case RUSSIAN -> switch (day) {
                case MONDAY -> "понедельник";
                case TUESDAY -> "вторник";
                case WEDNESDAY -> "среда";
                case THURSDAY -> "четверг";
                case FRIDAY -> "пятница";
                case SATURDAY -> "суббота";
                case SUNDAY -> "воскресенье";
            };
            case ENGLISH -> day.name().charAt(0) + day.name().substring(1).toLowerCase();
        };
    }

// ==================== LABEL GETTERLARI ====================

    private String getOrderIdLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Buyurtma ID";
            case CYRILLIC -> "Буюртма ID";
            case RUSSIAN -> "ID заказа";
            case ENGLISH -> "Order ID";
        };
    }

    private String getCustomerLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Mijoz";
            case CYRILLIC -> "Мижоз";
            case RUSSIAN -> "Клиент";
            case ENGLISH -> "Customer";
        };
    }

    private String getPhoneLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Telefon";
            case CYRILLIC -> "Телефон";
            case RUSSIAN -> "Телефон";
            case ENGLISH -> "Phone";
        };
    }

    private String getShopLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Do'kon";
            case CYRILLIC -> "Дўкон";
            case RUSSIAN -> "Магазин";
            case ENGLISH -> "Shop";
        };
    }

    private String getSellerLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Sotuvchi";
            case CYRILLIC -> "Сотувчи";
            case RUSSIAN -> "Продавец";
            case ENGLISH -> "Seller";
        };
    }

    private String getStatusLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Holati";
            case CYRILLIC -> "Ҳолати";
            case RUSSIAN -> "Статус";
            case ENGLISH -> "Status";
        };
    }

    private String getDeliveryLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Yetkazib berish";
            case CYRILLIC -> "Етказиб бериш";
            case RUSSIAN -> "Доставка";
            case ENGLISH -> "Delivery";
        };
    }

    private String getAddressLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "<b>📍 Manzil:</b> ";
            case CYRILLIC -> "<b>📍 Манзил:</b> ";
            case RUSSIAN -> "<b>📍 Адрес:</b> ";
            case ENGLISH -> "<b>📍 Address:</b> ";
        };
    }

    private String getProductsLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "<b>🛒 Mahsulotlar:</b>";
            case CYRILLIC -> "<b>🛒 Маҳсулотлар:</b>";
            case RUSSIAN -> "<b>🛒 Товары:</b>";
            case ENGLISH -> "<b>🛒 Products:</b>";
        };
    }

    private String getNoProductsMessage(Language lang) {
        return switch (lang) {
            case UZBEK -> "❌ Mahsulotlar topilmadi";
            case CYRILLIC -> "❌ Маҳсулотлар топилмади";
            case RUSSIAN -> "❌ Товары не найдены";
            case ENGLISH -> "❌ Products not found";
        };
    }

    private String getUnitLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "ta";
            case CYRILLIC -> "та";
            case RUSSIAN -> "шт";
            case ENGLISH -> "pcs";
        };
    }

    private String getPriceLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Narxi";
            case CYRILLIC -> "Нархи";
            case RUSSIAN -> "Цена";
            case ENGLISH -> "Price";
        };
    }

    private String getTotalLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Summa";
            case CYRILLIC -> "Сумма";
            case RUSSIAN -> "Сумма";
            case ENGLISH -> "Total";
        };
    }

    private String getPaymentLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "<b>💰 TO'LOV MA'LUMOTLARI</b>";
            case CYRILLIC -> "<b>💰 ТЎЛОВ МАЪЛУМОТЛАРИ</b>";
            case RUSSIAN -> "<b>💰 ИНФОРМАЦИЯ ОБ ОПЛАТЕ</b>";
            case ENGLISH -> "<b>💰 PAYMENT INFORMATION</b>";
        };
    }

    private String getProductsTotalLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Mahsulotlar";
            case CYRILLIC -> "Маҳсулотлар";
            case RUSSIAN -> "Товары";
            case ENGLISH -> "Products";
        };
    }

    private String getDeliveryPriceLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Yetkazib berish";
            case CYRILLIC -> "Етказиб бериш";
            case RUSSIAN -> "Доставка";
            case ENGLISH -> "Delivery";
        };
    }

    private String getTotalAmountLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Jami";
            case CYRILLIC -> "Жами";
            case RUSSIAN -> "Итого";
            case ENGLISH -> "Total";
        };
    }

    private String getTimeLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "<b>🕒 VAQT MA'LUMOTLARI</b>";
            case CYRILLIC -> "<b>🕒 ВАҚТ МАЪЛУМОТЛАРИ</b>";
            case RUSSIAN -> "<b>🕒 ИНФОРМАЦИЯ О ВРЕМЕНИ</b>";
            case ENGLISH -> "<b>🕒 TIME INFORMATION</b>";
        };
    }

    private String getCreatedLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Yaratilgan";
            case CYRILLIC -> "Яратилган";
            case RUSSIAN -> "Создан";
            case ENGLISH -> "Created";
        };
    }

    private String getUpdatedLabel(Language lang) {
        return switch (lang) {
            case UZBEK -> "Yangilangan";
            case CYRILLIC -> "Янгиланган";
            case RUSSIAN -> "Обновлён";
            case ENGLISH -> "Updated";
        };
    }

    private String getUnknownText(Language lang) {
        return switch (lang) {
            case UZBEK -> "Noma'lum";
            case CYRILLIC -> "Номаълум";
            case RUSSIAN -> "Неизвестно";
            case ENGLISH -> "Unknown";
        };
    }

// ==================== UTILITY METODLARI ====================

    private String getShopName(Language lang, Shop shop) {
        if (shop == null) return getUnknownText(lang);

        return switch (lang) {
            case UZBEK -> shop.getNameUz() != null ? shop.getNameUz() : getUnknownText(lang);
            case CYRILLIC -> shop.getNameCyr() != null ? shop.getNameCyr() : getUnknownText(lang);
            case RUSSIAN -> shop.getNameRu() != null ? shop.getNameRu() : getUnknownText(lang);
            case ENGLISH -> shop.getNameEn() != null ? shop.getNameEn() : getUnknownText(lang);
        };
    }

    private String getProductName(Language lang, ProductType productType) {
        if (productType == null || productType.getProduct() == null) {
            return getUnknownText(lang);
        }

        String productName = switch (lang) {
            case UZBEK -> productType.getProduct().getNameUz();
            case CYRILLIC -> productType.getProduct().getNameCyr();
            case RUSSIAN -> productType.getProduct().getNameRu();
            case ENGLISH -> productType.getProduct().getNameEn();
        };

        String typeName = switch (lang) {
            case UZBEK -> productType.getNameUz();
            case CYRILLIC -> productType.getNameCyr();
            case RUSSIAN -> productType.getNameRu();
            case ENGLISH -> productType.getNameEn();
        };

        if (productName == null) productName = getUnknownText(lang);
        if (typeName == null) typeName = "";

        return productName + (typeName.isEmpty() ? "" : " (" + typeName + ")");
    }

    private boolean isDelivery(DeliveryType deliveryType) {
        return deliveryType == DeliveryType.DELIVERY_INSIDE ||
                deliveryType == DeliveryType.DELIVERY_OUTSIDE;
    }

// ==================== FORMAT PRICE ====================

    private String formatPrice(BigDecimal price, Language language) {
        if (price == null) {
            price = BigDecimal.ZERO;
        }

        // Narxni formatlash (masalan, 1000 -> "1 000")
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

// ==================== EMPTY ORDERS METHOD ====================


// ==================== SEND MESSAGE LONG TEXT WITH HTML FIX ====================


    private List<String> splitHtmlMessageForTelegram(String html, int maxLength) {
        List<String> parts = new ArrayList<>();

        if (html == null || html.isEmpty()) {
            return parts;
        }

        int index = 0;
        while (index < html.length()) {
            int endIndex = Math.min(index + maxLength, html.length());

            // Agar biz tag ichida bo'lsak
            if (endIndex < html.length()) {
                // Oxirgi ochuvchi tag ni topish
                int lastOpenTag = html.lastIndexOf('<', endIndex);
                int tagClose = html.indexOf('>', lastOpenTag);

                // Agar tag to'liq emas bo'lsa
                if (lastOpenTag > index && tagClose > endIndex) {
                    endIndex = tagClose + 1;
                }

                // Yopuvchi tag larni tekshirish
                String[] htmlTags = {"b", "strong", "i", "em", "u", "ins", "s", "strike", "del", "a", "code", "pre"};
                for (String tag : htmlTags) {
                    String closeTag = "</" + tag + ">";
                    int closeTagStart = html.lastIndexOf(closeTag, endIndex);

                    // Agar yopuvchi tag qismda to'liq bo'lmasa
                    if (closeTagStart > index && closeTagStart + closeTag.length() > endIndex) {
                        endIndex = closeTagStart + closeTag.length();
                    }
                }
            }

            String part = html.substring(index, endIndex);

            // Bo'sh yoki faqat tag lardan iborat bo'lmasligini tekshirish
            String textOnly = part.replaceAll("<[^>]+>", "").replaceAll("\\s+", "");
            if (!textOnly.isEmpty()) {
                parts.add(part);
            }

            index = endIndex;
        }

        return parts;
    }


    /*private String pickupReadyMessage(Language language) {
        return switch (language) {
            case UZBEK -> "🟢 <b>Buyurtma olib ketishga tayyor!</b>\n" +
                    "📦 Do‘konga kelib buyurtmangizni olib ketishingiz mumkin.\n\n";

            case CYRILLIC -> "🟢 <b>Буюртма олиб кетишга тайёр!</b>\n" +
                    "📦 Дўконга келиб буюртмангизни олиб кетишингиз мумкин.\n\n";

            case RUSSIAN -> "🟢 <b>Заказ готов к самовывозу!</b>\n" +
                    "📦 Вы можете забрать заказ в магазине.\n\n";

            case ENGLISH -> "🟢 <b>Your order is ready for pickup!</b>\n" +
                    "📦 You can collect your order from the shop.\n\n";
        };
    }*/

    public String newOrder(Language language, UUID shopId) {
        String orderIdText = "🆔 ID: <code>" + shopId + "</code>";

        return switch (language) {
            case UZBEK ->
                    "📦 Sizga yangi buyurtma kelib tushdi!\n" + orderIdText + "\n✅ Iltimos, buyurtmani tekshiring.";
            case CYRILLIC -> "📦 Сизга янги буюртма келиб тушди!\n" + orderIdText + "\n✅ Илтимос, буюртмани текширинг.";
            case RUSSIAN -> "📦 У вас новый заказ!\n" + orderIdText + "\n✅ Пожалуйста, проверьте заказ.";
            case ENGLISH -> "📦 You have a new order!\n" + orderIdText + "\n✅ Please check the order.";
        };
    }

    private String cardInfo(Language language, Seller seller) {
        String cardNumber = seller.getCardNumber();
        String maskedCard = cardNumber == null || cardNumber.length() < 4
                ? ""
                : "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);

        return switch (language) {
            case UZBEK -> "💳 *To‘lov kartasi:*\n" +
                    "🏦 Turi: " + seller.getCardType() + "\n" +
                    "🔢 Raqam: " + maskedCard + "\n" +
                    "👤 Egasi: " + seller.getCardOwner() + "\n";

            case CYRILLIC -> "💳 *Тўлов картаси:*\n" +
                    "🏦 Тур: " + seller.getCardType() + "\n" +
                    "🔢 Рақам: " + maskedCard + "\n" +
                    "👤 Эгаси: " + seller.getCardOwner() + "\n";

            case RUSSIAN -> "💳 *Платёжная карта:*\n" +
                    "🏦 Тип: " + seller.getCardType() + "\n" +
                    "🔢 Номер: " + maskedCard + "\n" +
                    "👤 Владелец: " + seller.getCardOwner() + "\n";

            case ENGLISH -> "💳 *Payment card:*\n" +
                    "🏦 Type: " + seller.getCardType() + "\n" +
                    "🔢 Number: " + maskedCard + "\n" +
                    "👤 Owner: " + seller.getCardOwner() + "\n";
        };
    }

    /* public String payPrice(Language language, ShopOrder order, Seller seller) {

         DeliveryType type = order.getDeliveryType();
         BigDecimal total = order.getTotalAmount();
         BigDecimal deliveryPrice = order.getDeliveryPrice() == null
                 ? BigDecimal.ZERO
                 : order.getDeliveryPrice();

         return switch (language) {

             case UZBEK -> switch (type) {
                 case PICKUP -> "💳 *To‘lov:* " + total + " so‘m\n\n" +
                         "📦 Buyurtmani o‘zingiz olib ketasiz.\n" +
                         "Iltimos, to‘lovni amalga oshirib 🧾 chekni rasm ko‘rinishida yuboring.";

                 case DELIVERY_INSIDE -> "💳 *To‘lov:* " + total + " so‘m\n" +
                         "🚚 Yetkazib berish: " + deliveryPrice + " so‘m\n\n" +
                         "📍 Buyurtma manzilga yetkaziladi.\n" +
                         "To‘lovni amalga oshirib 🧾 chekni rasm ko‘rinishida yuboring.";

                 case DELIVERY_OUTSIDE -> "💳 *To‘lov:* " + total + " so‘m\n" +
                         "🚛 Shahar tashqarisiga yetkazib berish: " + deliveryPrice + " so‘m\n\n" +
                         "📍 Operator siz bilan bog‘lanadi.\n" +
                         "To‘lovni amalga oshirib 🧾 chekni rasm ko‘rinishida yuboring.";
             };

             case CYRILLIC -> switch (type) {
                 case PICKUP -> "💳 *Тўлов:* " + total + " сўм\n\n" +
                         "📦 Ўзингиз олиб кетасиз.\n" +
                         "Илтимос, тўловни қилиб 🧾 чекни расмда юборинг.";

                 case DELIVERY_INSIDE -> "💳 *Тўлов:* " + total + " сўм\n" +
                         "🚚 Етказиб бериш: " + deliveryPrice + " сўм\n\n" +
                         "📍 Манзилга етказилади.\n" +
                         "🧾 Чекни расмда юборинг.";

                 case DELIVERY_OUTSIDE -> "💳 *Тўлов:* " + total + " сўм\n" +
                         "🚛 Шаҳар ташқариси: " + deliveryPrice + " сўм\n\n" +
                         "📍 Оператор сиз билан боғланади.";
             };

             case RUSSIAN -> switch (type) {
                 case PICKUP -> "💳 *Оплата:* " + total + " сум\n\n" +
                         "📦 Самовывоз.\n" +
                         "Пожалуйста, отправьте 🧾 чек после оплаты.";

                 case DELIVERY_INSIDE -> "💳 *Оплата:* " + total + " сум\n" +
                         "🚚 Доставка: " + deliveryPrice + " сум\n\n" +
                         "📍 Доставка по городу.\n" +
                         "Отправьте 🧾 чек.";

                 case DELIVERY_OUTSIDE -> "💳 *Оплата:* " + total + " сум\n" +
                         "🚛 За город: " + deliveryPrice + " сум\n\n" +
                         "📍 С вами свяжется оператор.";
             };

             case ENGLISH -> switch (type) {
                 case PICKUP -> "💳 *Payment:* " + total + " UZS\n\n" +
                         "📦 Pickup.\n" +
                         "Please send the 🧾 receipt after payment.";

                 case DELIVERY_INSIDE -> "💳 *Payment:* " + total + " UZS\n" +
                         "🚚 Delivery: " + deliveryPrice + " UZS\n\n" +
                         "📍 Delivered within the city.";

                 case DELIVERY_OUTSIDE -> "💳 *Payment:* " + total + " UZS\n" +
                         "🚛 Outside city delivery: " + deliveryPrice + " UZS\n\n" +
                         "📍 Our operator will contact you.";
             };
         };
     }
     */
    public String payPrice(Language language, ShopOrder order, Seller seller) {

        DeliveryType type = order.getDeliveryType();

        BigDecimal productsPrice = order.getTotalAmount();
        BigDecimal deliveryPrice = order.getDeliveryPrice() == null
                ? BigDecimal.ZERO
                : order.getDeliveryPrice();

        BigDecimal totalPrice = productsPrice.add(deliveryPrice);

        String maskedCard = seller.getCardNumber() == null || seller.getCardNumber().length() < 4
                ? ""
                : seller.getCardNumber();

        return switch (language) {

            case UZBEK -> switch (type) {

                case PICKUP -> "💳 <b>Mahsulotlar uchun to‘lov:</b> "
                        + formatPrice(productsPrice, language) + "\n\n" +

                        "💳 <b>To‘lov kartasi:</b>\n" +
                        "🏦 Turi: " + seller.getCardType() + "\n" +
                        "🔢 Raqam: <code>" + maskedCard + "</code>\n" +
                        "👤 Egasi: " + seller.getCardOwner() + "\n\n" +
                        "📦 Buyurtmani o‘zingiz olib ketasiz.\n" +
                        "🧾 Chekni rasm ko‘rinishida yuboring.";

                default -> "💳 <b>Mahsulotlar:</b> "
                        + formatPrice(productsPrice, language) + "\n" +
                        "🚚 <b>Yetkazib berish:</b> "
                        + formatPrice(deliveryPrice, language) + "\n" +
                        "💰 <b>Jami:</b> "
                        + formatPrice(totalPrice, language) + "\n\n" +

                        "💳 <b>To‘lov kartasi:</b>\n" +
                        "🏦 Turi: " + seller.getCardType() + "\n" +
                        "🔢 Raqam: <code>" + maskedCard + "</code>\n" +
                        "👤 Egasi: " + seller.getCardOwner() + "\n\n" +
                        "🧾 Chekni rasm ko‘rinishida yuboring.";
            };

            case CYRILLIC -> switch (type) {

                case PICKUP -> "💳 <b>Маҳсулотлар учун тўлов:</b> "
                        + formatPrice(productsPrice, language) + "\n\n" +

                        "💳 <b>Тўлов картаси:</b>\n" +
                        "🏦 Тур: " + seller.getCardType() + "\n" +
                        "🔢 Рақам: <code>" + maskedCard + "</code>\n" +
                        "👤 Эгаси: " + seller.getCardOwner() + "\n\n" +
                        "📦 Ўзингиз олиб кетасиз.\n" +
                        "🧾 Чекни расмда юборинг.";

                default -> "💳 <b>Маҳсулотлар:</b> "
                        + formatPrice(productsPrice, language) + "\n" +
                        "🚚 <b>Етказиб бериш:</b> "
                        + formatPrice(deliveryPrice, language) + "\n" +
                        "💰 <b>Жами:</b> "
                        + formatPrice(totalPrice, language) + "\n\n" +

                        "💳 <b>Тўлов картаси:</b>\n" +
                        "🏦 Тур: " + seller.getCardType() + "\n" +
                        "🔢 Рақам: <code>" + maskedCard + "</code>\n" +
                        "👤 Эгаси: " + seller.getCardOwner();
            };

            case RUSSIAN -> switch (type) {

                case PICKUP -> "💳 <b>Оплата за товары:</b> "
                        + formatPrice(productsPrice, language) + "\n\n" +

                        "💳 <b>Платёжная карта:</b>\n" +
                        "🏦 Тип: " + seller.getCardType() + "\n" +
                        "🔢 Номер: <code>" + maskedCard + "</code>\n" +
                        "👤 Владелец: " + seller.getCardOwner() + "\n\n" +
                        "📦 Самовывоз.\n" +
                        "🧾 Отправьте чек.";

                default -> "💳 <b>Товары:</b> "
                        + formatPrice(productsPrice, language) + "\n" +
                        "🚚 <b>Доставка:</b> "
                        + formatPrice(deliveryPrice, language) + "\n" +
                        "💰 <b>Итого:</b> "
                        + formatPrice(totalPrice, language) + "\n\n" +

                        "💳 <b>Платёжная карта:</b>\n" +
                        "🏦 Тип: " + seller.getCardType() + "\n" +
                        "🔢 Номер: <code>" + maskedCard + "</code>\n" +
                        "👤 Владелец: " + seller.getCardOwner();
            };

            case ENGLISH -> switch (type) {

                case PICKUP -> "💳 <b>Products payment:</b> "
                        + formatPrice(productsPrice, language) + "\n\n" +

                        "💳 <b>Payment card:</b>\n" +
                        "🏦 Type: " + seller.getCardType() + "\n" +
                        "🔢 Number: <code>" + maskedCard + "</code>\n" +
                        "👤 Owner: " + seller.getCardOwner() + "\n\n" +
                        "📦 Pickup.\n" +
                        "🧾 Send receipt.";

                default -> "💳 <b>Products:</b> "
                        + formatPrice(productsPrice, language) + "\n" +
                        "🚚 <b>Delivery:</b> "
                        + formatPrice(deliveryPrice, language) + "\n" +
                        "💰 <b>Total:</b> "
                        + formatPrice(totalPrice, language) + "\n\n" +

                        "💳 <b>Payment card:</b>\n" +
                        "🏦 Type: " + seller.getCardType() + "\n" +
                        "🔢 Number: <code>" + maskedCard + "</code>\n" +
                        "👤 Owner: " + seller.getCardOwner();
            };
        };
    }


    public String notUploadedImage(Language language) {
        return switch (language) {
            case UZBEK -> """
                    ❗️To‘lov cheki yuklanmadi.
                    
                    📸 Iltimos, to‘lov chekini qaytadan rasm ko‘rinishida yuboring.""";

            case CYRILLIC -> """
                    ❗️Тўлов чеки юкланмади.
                    
                    📸 Илтимос, тўлов чекни қайтадан расм кўринишида юборинг.""";

            case RUSSIAN -> """
                    ❗️Чек об оплате не был загружен.
                    
                    📸 Пожалуйста, отправьте чек повторно в виде изображения.""";

            case ENGLISH -> """
                    ❗️Payment receipt was not uploaded.
                    
                    📸 Please resend the payment receipt as an image.""";
        };
    }

    public String savedImage(Language language) {
        return switch (language) {

            case UZBEK -> "✅ Chekingiz muvaffaqiyatli qabul qilindi.\n\n" +
                    "⏳ Chek tasdiqlangandan so‘ng buyurtmangiz bo‘yicha mahsulotlaringiz tayyorlanadi.";

            case CYRILLIC -> "✅ Чекингиз муваффақиятли қабул қилинди.\n\n" +
                    "⏳ Чек тасдиқлангандан сўнг буюртмангиз бўйича маҳсулотларингиз тайёрланади.";

            case RUSSIAN -> "✅ Ваш чек успешно принят.\n\n" +
                    "⏳ После подтверждения чека ваши товары будут подготовлены.";

            case ENGLISH -> "✅ Your receipt has been successfully received.\n\n" +
                    "⏳ Once the receipt is confirmed, your order items will be prepared.";
        };
    }

    public String requiredImage(Language language) {
        return switch (language) {

            case UZBEK -> """
                    📸 To‘lov chekini yuborish majburiy.
                    
                    Iltimos, davom etish uchun chekni rasm ko‘rinishida yuboring.""";

            case CYRILLIC -> """
                    📸 Тўлов чекни юбориш мажбурий.
                    
                    Илтимос, давом этиш учун чекни расм кўринишида юборинг.""";

            case RUSSIAN -> """
                    📸 Отправка чека обязательна.
                    
                    Пожалуйста, отправьте чек в виде изображения, чтобы продолжить.""";

            case ENGLISH -> """
                    📸 Sending a payment receipt is required.
                    
                    Please send the receipt as an image to continue.""";
        };
    }

    /*public String confirmFish(Language language, ShopOrder order) {

        DeliveryType type = order.getDeliveryType();

        return switch (language) {

            case UZBEK -> switch (type) {
                case PICKUP -> """
                        ✅ To‘lovingiz muvaffaqiyatli tasdiqlandi.
                        
                        📦 Buyurtmangiz tayyorlanmoqda.
                        🏪 Tayyor bo‘lgach, o‘zingiz olib ketishingiz mumkin.
                        📞 Tez orada siz bilan bog‘lanamiz.""";

                case DELIVERY_INSIDE -> """
                        ✅ To‘lovingiz muvaffaqiyatli tasdiqlandi.
                        
                        📦 Buyurtmangiz tayyorlanmoqda.
                        🚚 Buyurtma ko‘rsatilgan manzilga yetkazib beriladi.
                        📞 Tez orada siz bilan bog‘lanamiz.""";

                case DELIVERY_OUTSIDE -> """
                        ✅ To‘lovingiz muvaffaqiyatli tasdiqlandi.
                        
                        📦 Buyurtmangiz tayyorlanmoqda.
                        🚛 Shahar tashqarisiga yetkazib berish rejalashtirilmoqda.
                        📞 Operator siz bilan bog‘lanadi.""";
            };

            case CYRILLIC -> switch (type) {
                case PICKUP -> """
                        ✅ Тўловингиз муваффақиятли тасдиқланди.
                        
                        📦 Буюртмангиз тайёрланмоқда.
                        🏪 Тайёр бўлгач, ўзингиз олиб кетишингиз мумкин.
                        📞 Тез орада сиз билан боғланамиз.""";

                case DELIVERY_INSIDE -> """
                        ✅ Тўловингиз муваффақиятли тасдиқланди.
                        
                        📦 Буюртмангиз тайёрланмоқда.
                        🚚 Буюртма кўрсатилган манзилга етказиб берилади.
                        📞 Тез орада сиз билан боғланамиз.""";

                case DELIVERY_OUTSIDE -> """
                        ✅ Тўловингиз муваффақиятли тасдиқланди.
                        
                        📦 Буюртмангиз тайёрланмоқда.
                        🚛 Шаҳар ташқарисига етказиб бериш режалаштирилмоқда.
                        📞 Оператор сиз билан боғланади.""";
            };

            case RUSSIAN -> switch (type) {
                case PICKUP -> """
                        ✅ Ваш платёж успешно подтверждён.
                        
                        📦 Ваш заказ готовится.
                        🏪 Вы сможете забрать заказ самостоятельно.
                        📞 Мы скоро свяжемся с вами.""";

                case DELIVERY_INSIDE -> """
                        ✅ Ваш платёж успешно подтверждён.
                        
                        📦 Ваш заказ готовится.
                        🚚 Заказ будет доставлен по указанному адресу.
                        📞 Мы скоро свяжемся с вами.""";

                case DELIVERY_OUTSIDE -> """
                        ✅ Ваш платёж успешно подтверждён.
                        
                        📦 Ваш заказ готовится.
                        🚛 Доставка за город планируется.
                        📞 С вами свяжется оператор.""";
            };

            case ENGLISH -> switch (type) {
                case PICKUP -> """
                        ✅ Your payment has been successfully confirmed.
                        
                        📦 Your order is being prepared.
                        🏪 You will be able to pick it up yourself.
                        📞 We will contact you shortly.""";

                case DELIVERY_INSIDE -> """
                        ✅ Your payment has been successfully confirmed.
                        
                        📦 Your order is being prepared.
                        🚚 The order will be delivered to the provided address.
                        📞 We will contact you shortly.""";

                case DELIVERY_OUTSIDE -> """
                        ✅ Your payment has been successfully confirmed.
                        
                        📦 Your order is being prepared.
                        🚛 Outside city delivery is being arranged.
                        📞 Our operator will contact you.""";
            };
        };
    }*/

    private String buildProductsText(ShopOrder order, Language language) {

        List<OrderItem> items = orderItemRepository.findByShopOrder(order);

        if (items == null || items.isEmpty()) {
            return "-";
        }

        StringBuilder sb = new StringBuilder();
        int i = 1;

        for (OrderItem item : items) {

            String productName = switch (language) {
                case UZBEK ->
                        (item.getProductType().getProduct().getNameUz()) + "(" + item.getProductType().getNameUz() + ")";
                case RUSSIAN ->
                        (item.getProductType().getProduct().getNameRu()) + "(" + item.getProductType().getNameRu() + ")";
                case ENGLISH ->
                        (item.getProductType().getProduct().getNameEn()) + "(" + item.getProductType().getNameEn() + ")";
                case CYRILLIC ->
                        (item.getProductType().getProduct().getNameCyr()) + "(" + item.getProductType().getNameCyr() + ")";
            };

            sb.append(i++)
                    .append(". ")
                    .append(productName)
                    .append(" x")
                    .append(item.getQuantity())
                    .append("\n");
        }

        return sb.toString();
    }

    public String confirmFish(Language language, ShopOrder order) {

        DeliveryType type = order.getDeliveryType();
        String products = buildProductsText(order, language);
        String orderId = order.getPkey().toString();

        return switch (language) {

            case UZBEK -> switch (type) {
                case PICKUP -> """
                        ✅ To‘lovingiz muvaffaqiyatli tasdiqlandi.
                        
                        🆔 Buyurtma ID: %s
                        
                        🛒 Mahsulotlar:
                        %s
                        
                        📦 Buyurtmangiz tayyorlanmoqda.
                        🏪 Tayyor bo‘lgach, o‘zingiz olib ketishingiz mumkin.
                        📞 Tez orada siz bilan bog‘lanamiz.
                        """.formatted(orderId, products);

                case DELIVERY_INSIDE -> """
                        ✅ To‘lovingiz muvaffaqiyatli tasdiqlandi.
                        
                        🆔 Buyurtma ID: %s
                        
                        🛒 Mahsulotlar:
                        %s
                        
                        📦 Buyurtmangiz tayyorlanmoqda.
                        🚚 Buyurtma ko‘rsatilgan manzilga yetkazib beriladi.
                        📞 Tez orada siz bilan bog‘lanamiz.
                        """.formatted(orderId, products);

                case DELIVERY_OUTSIDE -> """
                        ✅ To‘lovingiz muvaffaqiyatli tasdiqlandi.
                        
                        🆔 Buyurtma ID: %s
                        
                        🛒 Mahsulotlar:
                        %s
                        
                        📦 Buyurtmangiz tayyorlanmoqda.
                        🚛 Shahar tashqarisiga yetkazib berish rejalashtirilmoqda.
                        📞 Operator siz bilan bog‘lanadi.
                        """.formatted(orderId, products);
            };

            case CYRILLIC -> """
                    ✅ Тўловингиз муваффақиятли тасдиқланди.
                    
                    🆔 Буюртма ID: %s
                    
                    🛒 Маҳсулотлар:
                    %s
                    
                    📦 Буюртмангиз тайёрланмоқда.
                    📞 Тез орада сиз билан боғланамиз.
                    """.formatted(orderId, products);

            case RUSSIAN -> """
                    ✅ Ваш платёж успешно подтверждён.
                    
                    🆔 ID заказа: %s
                    
                    🛒 Товары:
                    %s
                    
                    📦 Ваш заказ готовится.
                    📞 Мы скоро свяжемся с вами.
                    """.formatted(orderId, products);

            case ENGLISH -> """
                    ✅ Your payment has been successfully confirmed.
                    
                    🆔 Order ID: %s
                    
                    🛒 Products:
                    %s
                    
                    📦 Your order is being prepared.
                    📞 We will contact you shortly.
                    """.formatted(orderId, products);
        };
    }

    public String cancelOrder(Language language, ShopOrder order) {

        String orderId = order.getPkey().toString();

        return switch (language) {

            case UZBEK -> """
                    ❌ Buyurtmangiz bekor qilindi.
                    
                    🆔 Buyurtma ID: %s
                    
                    ℹ️ Agar savollaringiz bo‘lsa yoki buyurtmani qayta rasmiylashtirmoqchi bo‘lsangiz,
                    iltimos, biz bilan bog‘laning.
                    """.formatted(orderId);

            case CYRILLIC -> """
                    ❌ Буюртмангиз бекор қилинди.
                    
                    🆔 Буюртма ID: %s
                    
                    ℹ️ Агар саволларингиз бўлса ёки буюртмани қайта расмийлаштирмоқчи бўлсангиз,
                    илтимос, биз билан боғланинг.
                    """.formatted(orderId);

            case RUSSIAN -> """
                    ❌ Ваш заказ был отменён.
                    
                    🆔 ID заказа: %s
                    
                    ℹ️ Если у вас есть вопросы или вы хотите оформить заказ повторно,
                    пожалуйста, свяжитесь с нами.
                    """.formatted(orderId);

            case ENGLISH -> """
                    ❌ Your order has been cancelled.
                    
                    🆔 Order ID: %s
                    
                    ℹ️ If you have any questions or would like to place a new order,
                    please contact us.
                    """.formatted(orderId);
        };
    }

    public String cancelOrderToSeller(Language language, ShopOrder order) {

        String orderId = order.getPkey().toString();

        return switch (language) {

            case UZBEK -> """
                    ❌ <b>Buyurtma bekor qilindi.</b>
                    
                    🆔 Buyurtma ID: <code>%s</code>
                    
                    📦 <i>Iltimos, buyurtmalar ro‘yxatini tekshirib ko‘ring.</i>
                    """.formatted(orderId);

            case CYRILLIC -> """
                    ❌ <b>Буюртма бекор қилинди.</b>
                    
                    🆔 Буюртма ID: <code>%s</code>
                    
                    📦 <i>Илтимос, буюртмалар рўйхатини текшириб кўринг.</i>
                    """.formatted(orderId);

            case RUSSIAN -> """
                    ❌ <b>Заказ был отменён.</b>
                    
                    🆔 ID заказа: <code>%s</code>
                    
                    📦 <i>Пожалуйста, проверьте список заказов.</i>
                    """.formatted(orderId);

            case ENGLISH -> """
                    ❌ <b>The order has been cancelled.</b>
                    
                    🆔 Order ID: <code>%s</code>
                    
                    📦 <i>Please check your orders list.</i>
                    """.formatted(orderId);
        };
    }


    public String categoryIsProductsIsNullOrEmpty(Language language) {
        return switch (language) {
            case UZBEK -> "📦 Ushbu kategoriyada hozircha mahsulotlar mavjud emas.";
            case CYRILLIC -> "📦 Ушбу категорияда ҳозирча маҳсулотлар мавжуд эмас.";
            case RUSSIAN -> "📦 В данной категории пока нет товаров.";
            case ENGLISH -> "📦 There are no products in this category yet.";
        };
    }

    public String isCancelOrderRequest(Language language, ShopOrder order) {
        String msg = orderInformation(language, order);
        String confirmText = switch (language) {
            case UZBEK -> "\n\n⚠️ <b>Haqiqatdan ham ushbu buyurtmani bekor qilmoqchimisiz?</b>";
            case CYRILLIC -> "\n\n⚠️ <b>Ҳақиқатдан ҳам ушбу буюртмани бекор қилмоқчимисиз?</b>";
            case RUSSIAN -> "\n\n⚠️ <b>Вы действительно хотите отменить этот заказ?</b>";
            case ENGLISH -> "\n\n⚠️ <b>Are you sure you want to cancel this order?</b>";
        };

        return msg + confirmText;
    }

    public String cancelledOrder(Language language) {
        return switch (language) {

            case UZBEK -> "❌ Buyurtmangiz muvaffaqiyatli bekor qilindi";

            case CYRILLIC -> "❌ Буюртмангиз муваффақиятли бекор қилинди";

            case RUSSIAN -> "❌ Ваш заказ успешно отменён";

            case ENGLISH -> "❌ Your order has been successfully cancelled";
        };
    }

    public String shopInfo(Language language, Shop shop) {

        String name;
        String description;
        String deliveryInfo;

        switch (language) {
            case UZBEK -> {
                name = shop.getNameUz();
                description = shop.getDescriptionUz();
                deliveryInfo = shop.getDeliveryInfoUz();
            }
            case CYRILLIC -> {
                name = shop.getNameCyr();
                description = shop.getDescriptionCyr();
                deliveryInfo = shop.getDeliveryInfoCyr();
            }
            case RUSSIAN -> {
                name = shop.getNameRu();
                description = shop.getDescriptionRu();
                deliveryInfo = shop.getDeliveryInfoRu();
            }
            case ENGLISH -> {
                name = shop.getNameEn();
                description = shop.getDescriptionEn();
                deliveryInfo = shop.getDeliveryInfoEn();
            }
            default -> throw new IllegalStateException("Unexpected value: " + language);
        }

        StringBuilder sb = new StringBuilder();

        sb.append("🏪 ").append(name).append("\n\n");

        if (description != null && !description.isBlank()) {
            sb.append("📝 ").append(description).append("\n\n");
        }

        if (shop.getHasDelivery()) {
            sb.append("🚚 ")
                    .append(getDeliveryText(language))
                    .append(": ")
                    .append(formatPrice(shop.getDeliveryPrice(), language))
                    .append("\n");
        }

        if (shop.getHasOutsideDelivery()) {
            sb.append("🚛 ")
                    .append(getOutsideDeliveryText(language))
                    .append(": ")
                    .append(formatPrice(shop.getDeliveryOutsidePrice(), language))
                    .append("\n");
        }

        if (deliveryInfo != null && !deliveryInfo.isBlank()) {
            sb.append("\nℹ️ ").append(deliveryInfo);
        }

        if (shop.getHasPhone()) {
            sb.append("\n\n📞 ").append(shop.getPhone());
        }

        if (shop.getHasTelegram()) {
            sb.append("\n💬 ").append(shop.getTelegram());
        }

        if (shop.getHasInstagram()) {
            sb.append("\n📸 ").append(shop.getInstagram());
        }

        if (shop.getHasWebsite()) {
            sb.append("\n🌐 ").append(shop.getWebsite());
        }

        if (shop.getHasLocation() && shop.getAddress() != null) {
            sb.append("\n📍 ").append(shop.getAddress());
        }

        return sb.toString();
    }

    private String getDeliveryText(Language language) {
        return switch (language) {
            case UZBEK -> "Yetkazib berish";
            case CYRILLIC -> "Етказиб бериш";
            case RUSSIAN -> "Доставка";
            case ENGLISH -> "Delivery";
        };
    }

    private String getOutsideDeliveryText(Language language) {
        return switch (language) {
            case UZBEK -> "Shahar tashqarisiga yetkazib berish";
            case CYRILLIC -> "Шаҳар ташқарисига етказиб бериш";
            case RUSSIAN -> "Доставка за город";
            case ENGLISH -> "Outside city delivery";
        };
    }

    public String changeLanguage(Language language) {
        return switch (language) {
            case UZBEK -> """
                    🌐 Tilni o‘zgartirish
                    
                    Iltimos, quyidagilardan birini tanlang 👇
                    """;
            case CYRILLIC -> """
                    🌐 Тилни ўзгартириш
                    
                    Илтимос, қуйидагилардан бирини танланг 👇
                    """;
            case RUSSIAN -> """
                    🌐 Смена языка
                    
                    Пожалуйста, выберите один из вариантов ниже 👇
                    """;
            case ENGLISH -> """
                    🌐 Change language
                    
                    Please choose one of the options below 👇
                    """;
        };
    }

    public String contactUsMsg(Language language) {
        return switch (language) {

            case UZBEK -> """
                    📞 Biz bilan bog‘lanish
                    
                    💬 Savol va takliflaringiz bo‘lsa, biz bilan bog‘lanish uchun quyidagi kontaktlardan foydalanishingiz mumkin.
                    🤝 Sizning murojaatingiz biz uchun muhim.
                    """;

            case CYRILLIC -> """
                    📞 Биз билан боғланиш
                    
                    💬 Савол ва таклифларингиз бўлса, биз билан боғланиш учун қуйидаги контактлардан фойдаланишингиз мумкин.
                    🤝 Сизнинг мурожаатингиз биз учун муҳим.
                    """;

            case RUSSIAN -> """
                    📞 Связаться с нами
                    
                    💬 Если у вас есть вопросы или предложения, вы можете связаться с нами по следующим контактам.
                    🤝 Ваше обращение для нас важно.
                    """;

            case ENGLISH -> """
                    📞 Contact Us
                    
                    💬 If you have any questions or suggestions, you can contact us using the details below.
                    🤝 Your message is important to us.
                    """;
        };
    }

    public String changeOrderStatus(Language language, ShopOrder order) {
        return null;
    }

    public String handleTarget(Long chatId, BotCustomer user) {
        Language lang;
        if (user != null) {
            if (user.getLanguage() == null) {
                lang = UZBEK;
            } else {
                lang = user.getLanguage();
            }
        } else {
            lang = UZBEK;
        }
        return switch (lang) {

            case UZBEK -> """
                    😊 Bizni ijtimoiy tarmoqlarda kuzatib boring!
                    
                    📱 Eng so‘ngi yangiliklar
                    🎁 Maxsus chegirmalar
                    🔥 Ajoyib takliflar
                    
                    Barchasini birinchilardan bo‘lib bilib oling! 🚀
                    """;

            case CYRILLIC -> """
                    😊 Бизни ижтимоий тармоқларда кузатиб боринг!
                    
                    📱 Энг сўнгги янгиликлар
                    🎁 Махсус чегирмалар
                    🔥 Ажойиб таклифлар
                    
                    Барчасини биринчи бўлиб билиб олинг! 🚀
                    """;

            case RUSSIAN -> """
                    😊 Подписывайтесь на нас в социальных сетях!
                    
                    📱 Последние новости
                    🎁 Специальные скидки
                    🔥 Отличные предложения
                    
                    Узнавайте обо всём первыми! 🚀
                    """;

            case ENGLISH -> """
                    😊 Follow us on social networks!
                    
                    📱 Latest news
                    🎁 Special discounts
                    🔥 Great offers
                    
                    Be the first to know everything! 🚀
                    """;
        };

    }

    private String getAddressFromCoords(Double lat, Double lon) {
        String url = String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%s&lon=%s&addressdetails=1", lat, lon);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "TelegramBot/1.0") // O'zingizning botingiz nomini yozing
                    .header("Accept-Language", "uz") // Manzil o'zbek tilida kelishi uchun
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());

                // Agar xato qaytsa yoki manzil topilmasa
                if (jsonResponse.has("display_name")) {
                    return jsonResponse.getString("display_name");
                } else {
                    return "Manzil topilmadi";
                }
            } else {
                return "Xatolik: API javob bermadi (" + response.statusCode() + ")";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Manzilni aniqlashda texnik xatolik yuz berdi";
        }
    }
    public String savedLocation(Language language, Double latitude, Double longitude) {
        // Koordinatalar yordamida manzilni aniqlash (Reverse Geocoding)
        String address = getAddressFromCoords(latitude, longitude);

        switch (language) {
            case UZBEK:
                return String.format("✅ Joylashuvingiz muvaffaqiyatli saqlandi!\n\n📍 **Manzil:** %s", address);

            case CYRILLIC:
                return String.format("✅ Жойлашувингиз муваффақиятли сақланди!\n\n📍 **Манзил:** %s", address);

            case RUSSIAN:
                return String.format("✅ Ваше местоположение успешно сохранено!\n\n📍 **Адрес:** %s", address);

            case ENGLISH:
                return String.format("✅ Your location has been successfully saved!\n\n📍 **Address:** %s", address);

            default:
                return "📍 Address: " + address;
        }
    }
}
