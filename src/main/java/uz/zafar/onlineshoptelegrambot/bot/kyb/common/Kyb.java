package uz.zafar.onlineshoptelegrambot.bot.kyb.common;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import uz.zafar.onlineshoptelegrambot.dto.bot.kyb.Button;
import uz.zafar.onlineshoptelegrambot.dto.bot.kyb.ButtonType;

import java.util.ArrayList;
import java.util.List;

/**
 * Bazaviy keyboard klassi
 */
public class Kyb {


    public ReplyKeyboardMarkup setKeyboard(List<Button> list, int size) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow currentRow = new KeyboardRow();
        int count = 0;

        for (Button b : list) {
            KeyboardButton kb = new KeyboardButton();
            kb.setText(b.getText());

            if (b.getType() == ButtonType.CONTACT) kb.setRequestContact(true);
            if (b.getType() == ButtonType.LOCATION) kb.setRequestLocation(true);
            if (b.getType() == ButtonType.WEBAPP) kb.setWebApp(new WebAppInfo(b.getWebappUrl()));

            currentRow.add(kb);
            count++;

            if (count % size == 0) {
                rows.add(currentRow);
                currentRow = new KeyboardRow();
            }
        }

        if (!currentRow.isEmpty()) rows.add(currentRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public InlineKeyboardMarkup setKeyboardInline(List<Button> list, int size) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();
        int count = 0;

        for (Button b : list) {
            InlineKeyboardButton ib = new InlineKeyboardButton();
            ib.setText(b.getText());

            switch (b.getType()) {
                case INLINE:
                    ib.setCallbackData(b.getData());
                    break;
                case URL:
                    ib.setUrl(b.getUrl());
                    break;
                case WEBAPP:
                    ib.setWebApp(new org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo(b.getWebappUrl()));
                    break;
                default:
                    break;
            }

            currentRow.add(ib);
            count++;

            if (count % size == 0) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }

        if (!currentRow.isEmpty()) rows.add(currentRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }


    public Button inlineButton(String text, String callbackData) {
        Button b = new Button();
        b.setText(text);
        b.setType(ButtonType.INLINE);
        b.setData(callbackData);
        return b;
    }

    /**
     * Tez URL tugma yaratish
     */
    public Button urlButton(String text, String url) {
        Button b = new Button();
        b.setText(text);
        b.setType(ButtonType.URL);
        b.setUrl(url);
        return b;
    }

    /**
     * Tez WebApp tugma yaratish
     */
    public Button webAppButton(String text, String webappUrl) {
        Button b = new Button();
        b.setText(text);
        b.setType(ButtonType.WEBAPP);
        b.setWebappUrl(webappUrl);
        return b;
    }

    /**
     * Tez Contact tugma yaratish
     */
    public Button contactButton(String text) {
        Button b = new Button();
        b.setText(text);
        b.setType(ButtonType.CONTACT);
        return b;
    }

    /**
     * Tez Location tugma yaratish
     */
    public Button locationButton(String text) {
        Button b = new Button();
        b.setText(text);
        b.setType(ButtonType.LOCATION);
        return b;
    }
}
