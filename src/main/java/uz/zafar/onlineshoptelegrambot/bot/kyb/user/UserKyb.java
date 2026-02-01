package uz.zafar.onlineshoptelegrambot.bot.kyb.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import uz.zafar.onlineshoptelegrambot.bot.kyb.common.Kyb;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.CustomerLocation;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;
import uz.zafar.onlineshoptelegrambot.db.entity.order.Basket;
import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus;
import uz.zafar.onlineshoptelegrambot.dto.bot.kyb.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class UserKyb extends Kyb {
    private final UserButton userButton;
    private final String productWebappUrl;

    public UserKyb(UserButton userButton, @Value("${telegram.app.customer.products.webb-app.url}") String productWebappUrl) {
        super();
        this.userButton = userButton;
        this.productWebappUrl = productWebappUrl;
    }

    public ReplyKeyboardMarkup menu(Language l) {
        List<Button> buttons = userButton.menu(l);
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(buttons.get(0).getText()));
        rows.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(buttons.get(1).getText()));
        row2.add(new KeyboardButton(buttons.get(2).getText()));
        rows.add(row2);
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(buttons.get(3).getText()));
        rows.add(row3);
        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton(buttons.get(4).getText()));
        row4.add(new KeyboardButton(buttons.get(5).getText()));
        rows.add(row4);
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public ReplyKeyboardMarkup requestLang() {
        return setKeyboard(userButton.requestLang(), 2);
    }

    public ReplyKeyboardMarkup requestContact(Language language) {
        return setKeyboard(userButton.contactAndBackButtons(language), 1);
    }

    public ReplyKeyboardMarkup myLocations(Language language, List<CustomerLocation> locations) {

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();

        // 📍 1-qator: Manzilni yuborish (LOCATION)
        KeyboardButton sendLocationBtn = new KeyboardButton();
        sendLocationBtn.setText(language == Language.UZBEK ? "\uD83D\uDCCD Manzilni yuborish" : (language == Language.CYRILLIC ? "\uD83D\uDCCD Манзилни юбориш" : (language == Language.RUSSIAN ? "\uD83D\uDCCD Отправить локацию" : "\uD83D\uDCCD Send location")));
        sendLocationBtn.setRequestLocation(true);

        KeyboardRow locationRow = new KeyboardRow();
        locationRow.add(sendLocationBtn);
        rows.add(locationRow);

        if (!locations.isEmpty()) {
            for (CustomerLocation location : locations) {
                if (Boolean.TRUE.equals(location.getDeleted())) {
                    continue;
                }

                KeyboardButton button = new KeyboardButton();
                button.setText("🏠 " + location.getAddress());

                KeyboardRow row = new KeyboardRow();
                row.add(button);
                rows.add(row);
            }
        }

        KeyboardButton backBtn = new KeyboardButton();
        backBtn.setText(backText(language));

        KeyboardRow backRow = new KeyboardRow();
        backRow.add(backBtn);
        rows.add(backRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }


    private String backText(Language language) {
        return switch (language) {
            case UZBEK -> "🔙 Orqaga";
            case CYRILLIC -> "🔙 Орқага";
            case RUSSIAN -> "🔙 Назад";
            case ENGLISH -> "🔙 Back";
        };
    }

    public ReplyKeyboardMarkup isAddedToMyLocation(Language language) {
        return setKeyboard(userButton.isAddedToMyLocation(language), 2);
    }

    public ReplyKeyboardMarkup getAllCategories(Long chatId, Language language, List<Category> categories) {

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow webapp = new KeyboardRow();
        KeyboardButton button = new KeyboardButton();
        button.setText("\uD83D\uDCCC " +
                switch (language) {
                    case UZBEK -> "Interaktiv menyu";
                    case CYRILLIC -> "Интерактив меню";
                    case RUSSIAN -> "Интерактивное меню";
                    case ENGLISH -> "Interactive menu";
                }
        );
        button.setWebApp(new WebAppInfo(productWebappUrl + "/" + chatId));
        webapp.add(button);
        rows.add(webapp);
        KeyboardRow topRow = new KeyboardRow();
        topRow.add(new KeyboardButton(backText(language)));
        topRow.add(new KeyboardButton(cartText(language)));
        rows.add(topRow);

        KeyboardRow currentRow = new KeyboardRow();

        for (Category category : categories) {
            if (!Boolean.TRUE.equals(category.getActive())) continue;
            String text = switch (language) {
                case UZBEK -> category.getNameUz();
                case CYRILLIC -> category.getNameCyr();
                case RUSSIAN -> category.getNameRu();
                case ENGLISH -> category.getNameEn();
            };

            currentRow.add(new KeyboardButton(text));

            // 2 ta button bo‘lsa qatorni yopamiz
            if (currentRow.size() == 2) {
                rows.add(currentRow);
                currentRow = new KeyboardRow();
            }
        }

        // Oxirgi qatorda 1 ta qolsa
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private String cartText(Language language) {
        return switch (language) {
            case UZBEK -> "📥 Savat";
            case CYRILLIC -> "📥 Сават";
            case RUSSIAN -> "📥 Корзина";
            case ENGLISH -> "📥 Cart";
        };
    }

    public InlineKeyboardMarkup getProductsAndBasket(Language language, List<ProductType> productTypesFromProduct, ProductType currentProductType, int count) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // 1. Rasm ko'rish tugmasi (agar 1 tadan ko'p rasm bo'lsa)
        if (shouldShowImageButton(currentProductType)) {
            List<InlineKeyboardButton> imageRow = new ArrayList<>();
            InlineKeyboardButton viewImagesBtn = new InlineKeyboardButton();
            viewImagesBtn.setText(getViewImagesText(language, currentProductType));
            viewImagesBtn.setCallbackData("view_images_" + currentProductType.getPkey());
            imageRow.add(viewImagesBtn);
            rows.add(imageRow);
            rows.add(new ArrayList<>());

        }
        if (productTypesFromProduct != null && !productTypesFromProduct.isEmpty()) {
            rows.addAll(createVariantButtons(language, productTypesFromProduct, currentProductType));
            rows.add(new ArrayList<>()); // Bo'sh qator
        }

        rows.add(createQuantityButtons(language, currentProductType, count));

        rows.addAll(createActionButtons(count, language, currentProductType));

        // 5. Navigatsiya tugmalari
        UUID productId = currentProductType.getProduct().getPkey();
        rows.add(createNavigationButtons(language, count, productId));

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup getProductByCategory(Long chatId, List<Product> products, Language language, int page) {
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) products.size() / pageSize);
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        int start = page * pageSize;
        int end = Math.min(start + pageSize, products.size());

        Category category = null;
        List<Product> pageProducts = products.subList(start, end);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> webapp = new ArrayList<>();
        InlineKeyboardButton webappBtn = new InlineKeyboardButton();
        webappBtn.setText("\uD83D\uDCCC " + switch (language) {
            case UZBEK -> "Interaktiv menyu";
            case CYRILLIC -> "Интерактив меню";
            case RUSSIAN -> "Интерактивное меню";
            case ENGLISH -> "Interactive menu";
        });
        webappBtn.setWebApp(new WebAppInfo(productWebappUrl + "/" + chatId));
        webapp.add(webappBtn);
        rows.add(webapp);
        // Har bir mahsulot tugmasi
        for (Product product : pageProducts) {
            String text;
            switch (language) {
                case UZBEK -> text = "🛍️ " + product.getNameUz();
                case CYRILLIC -> text = "🛍️ " + product.getNameCyr();
                case RUSSIAN -> text = "🛍️ " + product.getNameRu();
                case ENGLISH -> text = "🛍️ " + product.getNameEn();
                default -> text = product.getNameEn();
            }
            InlineKeyboardButton button = new InlineKeyboardButton(text);
            button.setCallbackData("PRODUCT_" + product.getPkey());
            rows.add(List.of(button));
            category = product.getCategory();
        }

        // Pagination tugmalari
        if (totalPages > 1) {
            List<InlineKeyboardButton> pageButtons = new ArrayList<>();
            assert category != null;
            if (page > 0) { // Oldingi sahifa
                InlineKeyboardButton prev = new InlineKeyboardButton("⬅️");
                prev.setCallbackData("PAGE_" + (page - 1) + "_" + category.getPkey());
                pageButtons.add(prev);
            }
            if (page < totalPages - 1) { // Keyingi sahifa
                InlineKeyboardButton next = new InlineKeyboardButton("➡️");
                next.setCallbackData("PAGE_" + (page + 1) + "_" + category.getPkey());
                pageButtons.add(next);
            }
            rows.add(pageButtons); // Pagination tugmalari alohida qator
        }

        String cartText = cartText(language);

        InlineKeyboardButton cartButton = new InlineKeyboardButton(cartText);
        cartButton.setCallbackData("CART");
        rows.add(List.of(cartButton)); // Savat har doim alohida qator

        markup.setKeyboard(rows);
        return markup;
    }

    // Rasm tugmasini ko'rsatish kerakligini aniqlash
    private boolean shouldShowImageButton(ProductType productType) {
        if (productType == null || productType.getImages() == null) {
            return false;
        }

        int imageCount = productType.getImages().stream().filter(productTypeImage -> !productTypeImage.getDeleted()).toList().size();
        return imageCount > 1;
    }

    // Rasm ko'rish tugmasi matnini tilga qarab olish
    private String getViewImagesText(Language language, ProductType productType) {
        int imageCount = productType.getImages().stream().filter(productTypeImage -> !productTypeImage.getDeleted()).toList().size();
        return switch (language) {
            case UZBEK -> "🖼️ Rasmlarni ko'rish (" + imageCount + ")";
            case CYRILLIC -> "🖼️ Расмларни кўриш (" + imageCount + ")";
            case RUSSIAN -> "🖼️ Посмотреть фото (" + imageCount + ")";
            case ENGLISH -> "🖼️ View Images (" + imageCount + ")";
        };
    }

    // Variant tugmalarini yaratish
    private List<List<InlineKeyboardButton>> createVariantButtons(Language language, List<ProductType> productTypes, ProductType currentProductType) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Variantlarni tilga qarab nomini olish
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (ProductType productType : productTypes) {
            // Har 3 tadan keyin yangi qator
            if (currentRow.size() == 3) {
                rows.add(new ArrayList<>(currentRow));
                currentRow.clear();
            }

            InlineKeyboardButton button = new InlineKeyboardButton();
            String buttonText = getProductTypeNameByLanguage(productType, language);

            // Tanlangan variantni belgilash
            if (productType.getPkey().equals(currentProductType.getPkey())) {
                buttonText = "✅ " + buttonText;
            }

            // Agar zaxira tugagan bo'lsa
            if (productType.getStock() <= 0) {
                buttonText = "❌ " + buttonText;
                button.setCallbackData("outOf_stock_" + productType.getPkey());
            } else {
                button.setCallbackData("select_variant_" + productType.getPkey());
            }

            button.setText(buttonText);
            currentRow.add(button);
        }

        // Oxirgi qatorni qo'shish
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        return rows;
    }

    // ProductType nomini tilga qarab olish
    private String getProductTypeNameByLanguage(ProductType productType, Language language) {
        return switch (language) {
            case UZBEK -> productType.getNameUz();
            case CYRILLIC -> productType.getNameCyr();
            case RUSSIAN -> productType.getNameRu();
            case ENGLISH -> productType.getNameEn();
            default -> productType.getNameUz();
        };
    }

    private List<InlineKeyboardButton> createQuantityButtons(Language language, ProductType currentProductType, int count) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton decreaseBtn = new InlineKeyboardButton();
        decreaseBtn.setText("➖");
        decreaseBtn.setCallbackData("decrease_quantity_" + (count - 1) + "_" + currentProductType.getPkey());
        row.add(decreaseBtn);

        InlineKeyboardButton quantityBtn = new InlineKeyboardButton();
        quantityBtn.setText(getQuantityText(language, count));
        quantityBtn.setCallbackData("show_quantity_" + currentProductType.getPkey());
        row.add(quantityBtn);
        InlineKeyboardButton increaseBtn = new InlineKeyboardButton();
        increaseBtn.setText("➕");
        increaseBtn.setCallbackData("increase_quantity_" + (count + 1) + "_" + currentProductType.getPkey());
        row.add(increaseBtn);
        return row;
    }

    // Miqdor matnini tilga qarab olish
    private String getQuantityText(Language language, int quantity) {
        return switch (language) {
            case UZBEK, CYRILLIC -> quantity + " тa";
            case RUSSIAN -> quantity + " шт";
            case ENGLISH -> quantity + " pcs";
            default -> quantity + " ta";
        };
    }

    // Savatga qo'shish va boshqa amallar tugmalari
    private List<List<InlineKeyboardButton>> createActionButtons(int count, Language language, ProductType currentProductType) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Asosiy amallar qatori
        List<InlineKeyboardButton> mainActions = new ArrayList<>();

        // Savatga qo'shish
        InlineKeyboardButton addToBasketBtn = new InlineKeyboardButton();
        addToBasketBtn.setText(getAddToBasketText(language));
        addToBasketBtn.setCallbackData("add_current_basket_" + count + "_" + currentProductType.getPkey());
        mainActions.add(addToBasketBtn);
        rows.add(mainActions);

        return rows;
    }

    // Savatga qo'shish matni
    private String getAddToBasketText(Language language) {
        return switch (language) {
            case UZBEK -> "📥 Savatga qo'shish";
            case CYRILLIC -> "📥 Саватга қўшиш";
            case RUSSIAN -> "📥 В корзину";
            case ENGLISH -> "📥 Add to Cart";
            default -> "📥 Savatga qo'shish";
        };
    }

    // Navigatsiya tugmalari
    private List<InlineKeyboardButton> createNavigationButtons(Language language, int count, UUID productId) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        // Ortga qaytish
        InlineKeyboardButton backBtn = new InlineKeyboardButton();
        backBtn.setText(getBackText(language));
        backBtn.setCallbackData("back_product_" + productId);
        row.add(backBtn);
        InlineKeyboardButton viewBasketBtn = new InlineKeyboardButton();
        int basketCount = getBasketItemCount(count);
        viewBasketBtn.setText(getBasketText(language, basketCount));
        viewBasketBtn.setCallbackData("CART");
        row.add(viewBasketBtn);

        return row;
    }

    // Ortga matni
    private String getBackText(Language language) {
        return switch (language) {
            case UZBEK -> "⬅️ Ortga";
            case CYRILLIC -> "⬅️ Ортга";
            case RUSSIAN -> "⬅️ Назад";
            case ENGLISH -> "⬅️ Back";
        };
    }

    private String getBasketText(Language language, int count) {
        String basketText = cartText(language);

        if (count > 0) {
            basketText += " (" + count + ")";
        }

        return basketText;
    }

    // Savatdagi mahsulotlar sonini olish (asl logikangiz bilan almashtiring)
    private int getBasketItemCount(int count) {
        // Bu yerda sizning savat xizmatingizdan foydalaning
        // return basketService.getBasketItemCount(currentUserId);
        return count; // Hozircha 0 qaytaradi
    }

    public InlineKeyboardMarkup backBtn(Language language, UUID productTypeId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(backText(language));
        button.setCallbackData("back_productTypeId_" + productTypeId);
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    public ReplyKeyboardMarkup backBtn(Language language) {
        return setKeyboard(userButton.backBtn(language), 1);
    }


    public InlineKeyboardMarkup myBaskets(Language language, List<Basket> baskets) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (baskets == null || baskets.isEmpty()) {
            // Bo'sh savat uchun faqat "Orqaga" tugma
            InlineKeyboardButton backButton = new InlineKeyboardButton();
            backButton.setText("\uD83D\uDD19 " + switch (language) {
                case UZBEK -> "Orqaga";
                case CYRILLIC -> "Орқага";
                case RUSSIAN -> "Назад";
                case ENGLISH -> "Back";
            });
            backButton.setCallbackData("BACK_TO_MAIN");
            rows.add(List.of(backButton));
            markup.setKeyboard(rows);
            return markup;
        }

        // Har bir mahsulot uchun - soni + tugmalari
        for (Basket basket : baskets) {
            int quantity = basket.getQuantity();
            String productName = getProductNameByLanguage(basket.getProductType().getProduct(), language);
            InlineKeyboardButton decreaseBtn = new InlineKeyboardButton();
            decreaseBtn.setText("➖");
            decreaseBtn.setCallbackData("DECREASE_" + basket.getPkey());

            InlineKeyboardButton quantityBtn = new InlineKeyboardButton();
            quantityBtn.setText(setNumber(quantity));
            quantityBtn.setCallbackData("QUANTITY_" + basket.getQuantity()); // faqat ma'lumot uchun

            InlineKeyboardButton increaseBtn = new InlineKeyboardButton();
            increaseBtn.setText("➕");
            increaseBtn.setCallbackData("INCREASE_" + basket.getPkey());
            rows.add(List.of(decreaseBtn, quantityBtn, increaseBtn));
        }

        // Umumiy tugmalar
        InlineKeyboardButton clearBasketBtn = new InlineKeyboardButton();
        clearBasketBtn.setText("\uD83D\uDDD1️ " + switch (language) {
            case UZBEK -> "Savatni tozalash";
            case CYRILLIC -> "Саватни тозалаш";
            case RUSSIAN -> "Очистить корзину";
            case ENGLISH -> "Clear Basket";
        });
        clearBasketBtn.setCallbackData("CLEAR_BASKET");

        InlineKeyboardButton continueShoppingBtn = new InlineKeyboardButton();
        continueShoppingBtn.setText("▶️ " + switch (language) {
            case UZBEK -> "Buyurtmani davom ettirish";
            case CYRILLIC -> "Буюртмани давом эттириш";
            case RUSSIAN -> "Продолжить покупки";
            case ENGLISH -> "Continue Shopping";
        });
        continueShoppingBtn.setCallbackData("CONTINUE_SHOPPING");

        InlineKeyboardButton placeOrderBtn = new InlineKeyboardButton();
        placeOrderBtn.setText("✅ " + switch (language) {
            case UZBEK -> "Buyurtma berish";
            case CYRILLIC -> "Буюртма бериш";
            case RUSSIAN -> "Оформить заказ";
            case ENGLISH -> "Place Order";
        });
        placeOrderBtn.setCallbackData("PLACE_ORDER");

        rows.add(List.of(clearBasketBtn));
        rows.add(List.of(continueShoppingBtn));
        rows.add(List.of(placeOrderBtn));

        markup.setKeyboard(rows);
        return markup;
    }

    private String setNumber(int quantity) {
        String number = String.valueOf(quantity);
        StringBuilder result = new StringBuilder();
        for (char c : number.toCharArray()) {
            switch (c) {
                case '0' -> result.append("0️⃣");
                case '1' -> result.append("1️⃣");
                case '2' -> result.append("2️⃣");
                case '3' -> result.append("3️⃣");
                case '4' -> result.append("4️⃣");
                case '5' -> result.append("5️⃣");
                case '6' -> result.append("6️⃣");
                case '7' -> result.append("7️⃣");
                case '8' -> result.append("8️⃣");
                case '9' -> result.append("9️⃣");
            }
        }
        return result.toString();
    }


    /**
     * Mahsulot nomini til bo'yicha qaytaradi
     */
    private String getProductNameByLanguage(Product product, Language language) {
        if (product == null) return "";
        return switch (language) {
            case UZBEK -> product.getNameUz();
            case CYRILLIC -> product.getNameCyr();
            case RUSSIAN -> product.getNameRu();
            case ENGLISH -> product.getNameEn();
        };
    }

    public InlineKeyboardMarkup chooseDeliveryType(Language language) {

        String deliveryText;
        String pickupText;

        switch (language) {
            case UZBEK -> {
                deliveryText = "🚚 Yetkazib berish";
                pickupText = "\uD83C\uDFC3 Olib ketish";
            }
            case CYRILLIC -> {
                deliveryText = "🚚 Етказиб бериш";
                pickupText = "\uD83C\uDFC3 Олиб кетиш";
            }
            case RUSSIAN -> {
                deliveryText = "🚚 Доставка";
                pickupText = "\uD83C\uDFC3 Самовывоз";
            }
            default -> {
                deliveryText = "🚚 Delivery";
                pickupText = "🏪 Pickup";
            }
        }

        InlineKeyboardButton deliveryBtn = new InlineKeyboardButton(deliveryText);
        deliveryBtn.setCallbackData("DELIVERY_TYPE_DELIVERY");
        InlineKeyboardButton pickupBtn = new InlineKeyboardButton(pickupText);
        pickupBtn.setCallbackData("DELIVERY_TYPE_PICKUP");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(deliveryBtn, pickupBtn));
        InlineKeyboardButton backBtn = new InlineKeyboardButton(backText(language));
        backBtn.setCallbackData("TO_BACK_BASKET");
        rows.add(List.of(backBtn));
        return new InlineKeyboardMarkup(rows);
    }

    public ReplyKeyboardMarkup enterPhoneNumberForOrderWithBackBtn(Language language, String defaultPhoneNumber) {
        KeyboardButton button = new KeyboardButton();
        button.setText(defaultPhoneNumber);
        KeyboardRow row = new KeyboardRow();
        row.add(button);
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton();
        button.setText(backText(language));
        row.add(button);
        rows.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(
                rows
        );
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public ReplyKeyboardMarkup isSuccessCreateOrder(Language language) {

        String confirm;
        String cancel;

        switch (language) {
            case CYRILLIC -> {
                confirm = "✅ Тасдиқлаш";
                cancel = "❌ Бекор қилиш";
            }
            case RUSSIAN -> {
                confirm = "✅ Подтвердить";
                cancel = "❌ Отменить";
            }
            case ENGLISH -> {
                confirm = "✅ Confirm";
                cancel = "❌ Cancel";
            }
            default -> {
                confirm = "✅ Tasdiqlash";
                cancel = "❌ Bekor qilish";
            }
        }

        KeyboardRow row1 = new KeyboardRow();
        row1.add(confirm);
        row1.add(cancel);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(backText(language));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        markup.setSelective(true);

        return markup;
    }


    public InlineKeyboardMarkup orderInformation(Language language, ShopOrderStatus status, UUID shopOrderId) {
        return switch (status) {
            case NEW -> createNewOrderButtons(language, shopOrderId);
            case PAYMENT_COMPLETED -> createPaymentCompletedButtons(language, shopOrderId);
            case ACCEPTED -> createAcceptedButtons(language, shopOrderId);
            case PREPARING, SENT -> createPreparingSentButtons(language, shopOrderId);
            case COMPLETED -> createCompletedButtons(language, shopOrderId);
            case CANCELLED -> createCancelledButtons(language, shopOrderId);
        };
    }

    private InlineKeyboardMarkup createNewOrderButtons(Language language, UUID shopOrderId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton(language, "pay", "pay_order:" + shopOrderId));
        rows.add(row1);
        row1 = new ArrayList<>();
        row1.add(createButton(language, "cancel", "cancel_order:" + shopOrderId));
        rows.add(row1);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton(language, "shop", "go_to_shop:" + shopOrderId));
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createPaymentCompletedButtons(Language language, UUID shopOrderId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Row 1: New Order and Message Seller
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton(language, "new_order", "new_order:" + shopOrderId));
        rows.add(row1);
        row1 = new ArrayList<>();
        row1.add(createButton(language, "message_seller", "message_seller:" + shopOrderId));
        rows.add(row1);

        // Row 2: Go to Shop
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton(language, "shop", "go_to_shop:" + shopOrderId));
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createAcceptedButtons(Language language, UUID shopOrderId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Row 1: Comment and Go to Shop
        List<InlineKeyboardButton> row1 = new ArrayList<>();
//        row1.add(createButton(language, "comment", "leave_comment:" + shopOrderId));

        row1 = new ArrayList<>();
        row1.add(createButton(language, "shop", "go_to_shop:" + shopOrderId));
        rows.add(row1);

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createPreparingSentButtons(Language language, UUID shopOrderId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Single button: Go to Shop
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton(language, "shop", "go_to_shop:" + shopOrderId));
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createCompletedButtons(Language language, UUID shopOrderId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Row 1: New Order and Message Seller
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton(language, "new_order", "new_order:" + shopOrderId));

        row1 = new ArrayList<>();
        row1.add(createButton(language, "message_seller", "message_seller:" + shopOrderId));
        rows.add(row1);

        // Row 2: Go to Shop
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton(language, "shop", "go_to_shop:" + shopOrderId));
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createCancelledButtons(Language language, UUID shopOrderId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Row 1: New Order
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton(language, "new_order", "new_order:" + shopOrderId));
        rows.add(row1);

        // Row 2: Go to Shop
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton(language, "shop", "go_to_shop:" + shopOrderId));
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardButton createButton(Language language, String buttonKey, String callbackData) {
        Map<String, Map<Language, String>> buttonTexts = Map.of(
                "pay", Map.of(
                        Language.UZBEK, "💳 To'lov qilish",
                        Language.CYRILLIC, "💳 Тўлов қилиш",
                        Language.RUSSIAN, "💳 Оплатить",
                        Language.ENGLISH, "💳 Make Payment"
                ),
                "cancel", Map.of(
                        Language.UZBEK, "❌ Buyurtmani bekor qilish",
                        Language.CYRILLIC, "❌ Буюртмани бекор қилиш",
                        Language.RUSSIAN, "❌ Отменить заказ",
                        Language.ENGLISH, "❌ Cancel Order"
                ),
                "new_order", Map.of(
                        Language.UZBEK, "🛒 Buyurtma berish",
                        Language.CYRILLIC, "🛒 Буюртма бериш",
                        Language.RUSSIAN, "🛒 Сделать заказ",
                        Language.ENGLISH, "🛒 Place Order"
                ),
                "message_seller", Map.of(
                        Language.UZBEK, "📨 Sotuvchiga xabar yuborish",
                        Language.CYRILLIC, "📨 Сотувчига хабар юбориш",
                        Language.RUSSIAN, "📨 Написать продавцу",
                        Language.ENGLISH, "📨 Message Seller"
                ),
                "comment", Map.of(
                        Language.UZBEK, "💬 Izoh qoldirish",
                        Language.CYRILLIC, "💬 Изоҳ қолдириш",
                        Language.RUSSIAN, "💬 Оставить отзыв",
                        Language.ENGLISH, "💬 Leave Comment"
                ),
                "shop", Map.of(
                        Language.UZBEK, "🏪 Do'konga o'tish",
                        Language.CYRILLIC, "🏪 Дўконга ўтиш",
                        Language.RUSSIAN, "🏪 Перейти в магазин",
                        Language.ENGLISH, "🏪 Go to Shop"
                )
        );

        // Tugma matnini olish
        Map<Language, String> texts = buttonTexts.get(buttonKey);
        String text;

        if (texts != null && texts.containsKey(language)) {
            text = texts.get(language);
        } else {
            // Agar til yoki tugma topilmasa, default matn
            text = switch (buttonKey) {
                case "pay" -> "💳 Pay";
                case "cancel" -> "❌ Cancel";
                case "new_order" -> "🛒 Order";
                case "message_seller" -> "📨 Message";
                case "comment" -> "💬 Comment";
                case "shop" -> "🏪 Shop";
                default -> "Button";
            };
        }

        // Tugmani yaratish
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callbackData);
        return button;
    }


    public InlineKeyboardMarkup isCancelOrderRequest(Language language, ShopOrder order) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton confirm = new InlineKeyboardButton();
        InlineKeyboardButton cancel = new InlineKeyboardButton();

        switch (language) {
            case UZBEK -> {
                confirm.setText("✅ Ha, bekor qilaman");
                cancel.setText("❌ Yo‘q, qaytaman");
            }
            case CYRILLIC -> {
                confirm.setText("✅ Ҳа, бекор қиламан");
                cancel.setText("❌ Йўқ, қайтаман");
            }
            case RUSSIAN -> {
                confirm.setText("✅ Да, отменить");
                cancel.setText("❌ Нет, назад");
            }
            case ENGLISH -> {
                confirm.setText("✅ Yes, cancel");
                cancel.setText("❌ No, back");
            }
        }

        confirm.setCallbackData("CONFIRM_CANCEL_ORDER:" + order.getPkey());
        cancel.setCallbackData("BACK_TO_ORDER:" + order.getPkey());

        rows.add(List.of(confirm));
        rows.add(List.of(cancel));
        markup.setKeyboard(rows);

        return markup;
    }

    public InlineKeyboardMarkup backToOrderBtn(Language language, ShopOrder order) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton backBtn = new InlineKeyboardButton();

        switch (language) {
            case UZBEK -> backBtn.setText("⬅️ Orqaga qaytish");
            case CYRILLIC -> backBtn.setText("⬅️ Орқага қайтиш");
            case RUSSIAN -> backBtn.setText("⬅️ Назад");
            case ENGLISH -> backBtn.setText("⬅️ Back");
        }
        backBtn.setCallbackData("BACK_TO_ORDER:" + order.getPkey());
        rows.add(List.of(backBtn));
        markup.setKeyboard(rows);

        return markup;
    }

    public InlineKeyboardMarkup shopInfo(Language language, ShopOrder order) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton locationBtn = new InlineKeyboardButton();
        InlineKeyboardButton backBtn = new InlineKeyboardButton();

        switch (language) {
            case UZBEK -> {
                locationBtn.setText("📍 Manzilni olish");
                backBtn.setText("⬅️ Orqaga qaytish");
            }
            case CYRILLIC -> {
                locationBtn.setText("📍 Манзилни олиш");
                backBtn.setText("⬅️ Орқага қайтиш");
            }
            case RUSSIAN -> {
                locationBtn.setText("📍 Получить адрес");
                backBtn.setText("⬅️ Назад");
            }
            case ENGLISH -> {
                locationBtn.setText("📍 Get location");
                backBtn.setText("⬅️ Back");
            }
        }
        locationBtn.setCallbackData("SHOP_GET_LOCATION:" + order.getPkey());
        backBtn.setCallbackData("BACK_TO_ORDER:" + order.getPkey());

        rows.add(List.of(locationBtn));
        rows.add(List.of(backBtn));

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup deleteBtn(Language language, UUID id) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton deleteBtn = new InlineKeyboardButton();

        switch (language) {
            case UZBEK -> deleteBtn.setText("🗑 O'chirish");
            case CYRILLIC -> deleteBtn.setText("🗑 Ўчириш");
            case RUSSIAN -> deleteBtn.setText("🗑 Удалить");
            case ENGLISH -> deleteBtn.setText("🗑 Delete");
        }

        deleteBtn.setCallbackData("DELETE_ACTION:" + id);

        rows.add(List.of(deleteBtn));
        markup.setKeyboard(rows);

        return markup;
    }

    public InlineKeyboardMarkup becomingSellerKyb(Language language, String username) {

        String text = switch (language) {
            case UZBEK -> "🛍 Sotuvchi bo‘lish";
            case CYRILLIC -> "🛍 Сотувчи бўлиш";
            case RUSSIAN -> "🛍 Стать продавцом";
            case ENGLISH -> "🛍 Become a seller";
        };

        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(text)
                .url(username)
                .build();
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button))
                .build();
    }

}
