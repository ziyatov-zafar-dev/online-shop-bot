package uz.zafar.onlineshoptelegrambot.bot.kyb.seller;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import uz.zafar.onlineshoptelegrambot.bot.kyb.common.Kyb;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;
import uz.zafar.onlineshoptelegrambot.dto.bot.kyb.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SellerKyb extends Kyb {
    private final SellerButton sellerButton;

    public SellerKyb(SellerButton sellerButton) {
        super();
        this.sellerButton = sellerButton;
    }

    public ReplyKeyboardMarkup requestLang() {
        return setKeyboard(sellerButton.requestLang(), 2);
    }

    public ReplyKeyboardMarkup shareContact(Language language) {
        List<Button> buttons = sellerButton.changeLanguageAndShareContact(language);
        return setKeyboard(buttons, 1);
    }

    public ReplyKeyboardMarkup requestSellerLoginAndChangeLanguage(Language language, String confirmSellerWebbAppUrl) {
        return setKeyboard(sellerButton.requestSellerLoginAndChangeLanguage(language, confirmSellerWebbAppUrl), 1);
    }

    public ReplyKeyboardMarkup requestSellerLoginAndChangeLanguage1(Language language, String confirmSellerWebbAppUrl) {
        return setKeyboard(sellerButton.requestSellerLoginAndChangeLanguage1(language, confirmSellerWebbAppUrl), 1);
    }

    public ReplyKeyboardMarkup menu(Language language, String shopWebAppUrl, Long chatId) {
        List<Button> list = sellerButton.menu(language, shopWebAppUrl, chatId);
        System.out.println();
        return setKeyboard(list, 2);
    }

    public InlineKeyboardMarkup notShops(Language language, String webappUrl) {
        return setKeyboardInline(sellerButton.notShops(language, webappUrl), 1);
    }

    public InlineKeyboardMarkup yourProductLists(
            Language language,
            List<Product> allProducts,
            String webapp,
            String data
    ) {

        final int PAGE_SIZE = 8;
        int currentPage = data == null ? 0 : Integer.parseInt(data.split("_")[1]);

        int fromIndex = currentPage * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, allProducts.size());

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Product product : allProducts.subList(fromIndex, toIndex)) {

            String name = switch (language) {
                case UZBEK -> product.getNameUz();
                case CYRILLIC -> product.getNameCyr();
                case RUSSIAN -> product.getNameRu();
                case ENGLISH -> product.getNameEn();
            };

            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(name);
            btn.setCallbackData("PRODUCT_" + product.getPkey());
            rows.add(List.of(btn));
        }
        int totalPages = (int) Math.ceil((double) allProducts.size() / PAGE_SIZE);
        if (totalPages > 1) {
            List<InlineKeyboardButton> paginationRow = new ArrayList<>();
            if (currentPage > 0) {
                InlineKeyboardButton prev = new InlineKeyboardButton();
                prev.setText("⬅️");
                prev.setCallbackData("PAGE_" + (currentPage - 1));
                paginationRow.add(prev);
            }

            if (currentPage < totalPages - 1) {
                InlineKeyboardButton next = new InlineKeyboardButton();
                next.setText("➡️");
                next.setCallbackData("PAGE_" + (currentPage + 1));
                paginationRow.add(next);
            }

            rows.add(paginationRow);
        }
        String manageText = switch (language) {
            case UZBEK -> "\uD83C\uDFEA Mahsulotlarni boshqarish";
            case CYRILLIC -> "\uD83C\uDFEA Маҳсулотларни бошқариш";
            case RUSSIAN -> "\uD83C\uDFEA Управление товарами";
            case ENGLISH -> "\uD83C\uDFEA Manage products";
        };


        InlineKeyboardButton manageBtn = new InlineKeyboardButton();
        manageBtn.setText(manageText);
        manageBtn.setWebApp(new WebAppInfo(webapp));

        rows.add(List.of(manageBtn));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        return markup;
    }


    public InlineKeyboardMarkup aboutProduct(
            Language language,
            List<ProductType> allProductTypes,
            ProductType currentType,
            ProductTypeImage currentImage
    ) {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // ===== TYPE LIST =====
        for (ProductType type : allProductTypes) {
            if (Boolean.TRUE.equals(type.getDeleted())) continue;

            String name = switch (language) {
                case UZBEK -> type.getNameUz();
                case CYRILLIC -> type.getNameCyr();
                case RUSSIAN -> type.getNameRu();
                case ENGLISH -> type.getNameEn();
            };

            String text = type.getPkey().equals(currentType.getPkey())
                    ? "✅ " + name + " (" + type.getPrice() + ")"
                    : name + " (" + type.getPrice() + ")";

            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(text);
            btn.setCallbackData("PRODUCTTYPE_" + type.getPkey());

            rows.add(List.of(btn));
        }

        List<ProductTypeImage> images = currentType.getImages()
                .stream()
                .filter(img -> !Boolean.TRUE.equals(img.getDeleted()))
                .toList();

        if (!images.isEmpty()) {
            int currentIndex = findImageIndex(images, currentImage);

            List<InlineKeyboardButton> imageNav = new ArrayList<>();

            if (currentIndex > 0) {
                InlineKeyboardButton prev = new InlineKeyboardButton();
                prev.setText("⬅️");
                prev.setCallbackData(
                        "IMG_" + images.get(currentIndex - 1).getPkey()
                );
                imageNav.add(prev);
            }

            if (currentIndex < images.size() - 1) {
                InlineKeyboardButton next = new InlineKeyboardButton();
                next.setText("➡️");
                next.setCallbackData(
                        "IMG_" + images.get(currentIndex + 1).getPkey()
                );
                imageNav.add(next);
            }

            if (!imageNav.isEmpty()) {
                rows.add(imageNav);
            }
        }

        InlineKeyboardButton backBtn = new InlineKeyboardButton();
        backBtn.setText("⬅️ Orqaga");
        backBtn.setCallbackData("BACK_TO_PRODUCTS");
        rows.add(List.of(backBtn));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }


    private int findImageIndex(List<ProductTypeImage> images, ProductTypeImage currentImage) {
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getPkey().equals(currentImage.getPkey())) {
                return i;
            }
        }
        return 0;
    }

    public ReplyKeyboardMarkup logoutAllBtn(Language language) {
        return setKeyboard(sellerButton.logoutAllBtn(language), 1);
    }

    public ReplyKeyboardMarkup subscriptionExpiredBtn(Language language) {
        return setKeyboard(sellerButton.subscriptionExpiredBtn(language), 2);
    }

    public ReplyKeyboardMarkup paymentBtn(Language language) {
        return setKeyboard(sellerButton.payment(language), 1);
    }


    public ReplyKeyboardMarkup selectSubscriptionPlans(Language language, List<SubscriptionPlanType> planNames) {
        return setKeyboard(sellerButton.selectSubscriptionPlans(language, planNames), 2);
    }

    public InlineKeyboardMarkup planSubscription(Language language, Long planSubscriptionId) {

        InlineKeyboardButton activateButton = new InlineKeyboardButton();
        String buttonText = switch (language) {
            case UZBEK -> "✅ Faollashtirish";
            case RUSSIAN -> "✅ Активировать";
            case ENGLISH -> "✅ Activate";
            case CYRILLIC -> "✅ Фаоллаштириш";
        };

        activateButton.setText(buttonText);
        // Callback data orqali botga qaysi planni faollashtirishni bildiramiz
        activateButton.setCallbackData("activate_plan:" + planSubscriptionId);

        // Inline keyboard satrini yaratish
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        keyboardRow.add(activateButton);

        // InlineKeyboardMarkupga satr qo‘shish
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(keyboardRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }


    public InlineKeyboardMarkup topUpAccount(Language language) {
        String buttonText = switch (language) {
            case UZBEK -> "💳 Hisobni to‘ldirish";
            case CYRILLIC -> "💳 Ҳисобни тўлдириш";
            case RUSSIAN -> "💳 Пополнить счёт";
            case ENGLISH -> "💳 Top Up Account";
        };

        InlineKeyboardButton topUpButton = new InlineKeyboardButton();
        topUpButton.setText(buttonText);
        topUpButton.setCallbackData("TOP_UP");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(Collections.singletonList(Collections.singletonList(topUpButton)));

        return markup;
    }

    public ReplyKeyboardMarkup backBtn(Language language) {
        return setKeyboard(sellerButton.backBtn(language), 1);
    }

    public ReplyKeyboardMarkup settings(Language language) {
        return setKeyboard(sellerButton.settings(language), 2);
    }

    public ReplyKeyboardMarkup changeProfile(Language language) {
        return setKeyboard(sellerButton.changeProfile(language), 2);
    }

    public ReplyKeyboardMarkup back(Language language) {
        return setKeyboard(sellerButton.back(language), 1);
    }

    public ReplyKeyboardMarkup cardEdit(Language language) {
        KeyboardButton button;
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        List<Button> buttons = sellerButton.editCard(language);
        row.add(buttons.get(0).getText());
        row.add(buttons.get(1).getText());
        rows.add(row);
        row = new KeyboardRow();
        row.add(buttons.get(2).getText());
        rows.add(row);
        row = new KeyboardRow();
        row.add(buttons.get(3).getText());
        rows.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setKeyboard(rows);
        return markup;

    }



    public ReplyKeyboardMarkup confirmLogout(Language language) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        // Tilga qarab tugmalar matni
        String yesText;
        String noText;
        switch (language) {
            case UZBEK -> {
                yesText = "✅ Ha";
                noText = "❌ Yo‘q";
            }
            case CYRILLIC -> {
                yesText = "✅ Ҳа";
                noText = "❌ Йўқ";
            }
            case RUSSIAN -> {
                yesText = "✅ Да";
                noText = "❌ Нет";
            }
            default -> {
                yesText = "✅ Yes";
                noText = "❌ No";
            }
        }

        row.add(new KeyboardButton(yesText));
        rows.add(row);
        row = new KeyboardRow();

        row.add(new KeyboardButton(noText));
        rows.add(row);


        keyboard.setKeyboard(rows);

        return keyboard;
    }

}
