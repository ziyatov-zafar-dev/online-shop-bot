package uz.zafar.onlineshoptelegrambot.botservice.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.enums.CustomerEventCode;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;
import uz.zafar.onlineshoptelegrambot.dto.TelegramUpdateData;
import uz.zafar.onlineshoptelegrambot.botservice.TelegramUsersService;
import uz.zafar.onlineshoptelegrambot.botservice.UsersTelegramBotFunction;
import uz.zafar.onlineshoptelegrambot.util.TelegramCallbackExtractor;
import uz.zafar.onlineshoptelegrambot.util.TelegramUpdateExtractor;

import java.util.Map;
import java.util.UUID;

@Service
public class TelegramUsersServiceImpl implements TelegramUsersService {


    private final UsersTelegramBotFunction functions;

    public TelegramUsersServiceImpl(UsersTelegramBotFunction functions) {
        this.functions = functions;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleUpdate(Map<String, Object> update, HttpServletRequest request) {
        TelegramUpdateData updateData = TelegramUpdateExtractor.extract(update);
        /* ================= CALLBACK QUERY ================= */
        if (updateData.getType() == TelegramUpdateData.MessageType.CALLBACK) {
            CallbackQuery callback = TelegramCallbackExtractor.extractCallback(update);

            if (callback != null) {
                String data = callback.getData();
                Long chatId = callback.getMessage().getChatId();
                Integer messageId = callback.getMessage().getMessageId();
                updateData.setCallbackQuery(callback);
                updateData.setCallbackData(data);
                updateData.setChatId(chatId);
                updateData.setMessageId(messageId);
                BotCustomer user = functions.getUser(chatId);
                if (user == null) {
                    functions.start(chatId, updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());
                    return;
                }
                if (user.getEventCode() == CustomerEventCode.MENU) {
                    functions.menu(user, data, messageId, updateData.getCallbackQueryId());
                    return;
                }
                if (user.getEventCode() == CustomerEventCode.ORDER) {
                    functions.order(user, data, messageId, updateData.getCallbackQueryId());
                    return;
                }
            }

            return;
        }
        if (updateData.getType() == TelegramUpdateData.MessageType.LOCATION) {
            BotCustomer user = functions.getUser(updateData.getChatId());
            if (user.getEventCode() == CustomerEventCode.MENU) {
                functions.chooseLocation(user, updateData.getLatitude(), updateData.getLongitude());
            }
            return;
        }
        if (updateData.getType() == TelegramUpdateData.MessageType.PHOTO) {
            BotCustomer user = functions.getUser(updateData.getChatId());
            if (user.getEventCode() == CustomerEventCode.ORDER) {
                functions.order(user, updateData.getPhotoFileIds());
            }
            return;
        }
        if (updateData.getType() == TelegramUpdateData.MessageType.CONTACT) {
            BotCustomer user = functions.getUser(updateData.getChatId());
            if (user.getEventCode() == CustomerEventCode.MENU) {
                functions.requestContact(user, updateData.getPhoneNumber(), updateData.getContactUserId());
            }
            return;
        }
        /* ================= MESSAGE ================= */
        if (!update.containsKey("message")) return;

        Map<String, Object> message =
                (Map<String, Object>) update.get("message");

        String text = (String) message.get("text");
        if (text == null) return;

        Map<String, Object> chat =
                (Map<String, Object>) message.get("chat");

        Long chatId = Long.valueOf(chat.get("id").toString());

        if (text.equals("/start")) {
            functions.start(chatId, updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());
        } else {
            BotCustomer user = functions.refreshUser(chatId, updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());
            if (text.startsWith("/start")) {
                try {
                    if (text.split(" ")[1].equals("basket")){
                        functions.basket(user);
                        return;
                    }
                    if (user == null) {
                        user = functions.refreshUser(chatId, updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());
                    }
                    if (user.getLanguage() == null) {
                        user.setLanguage(Language.UZBEK);
                    }
                    String[] parts = text.split(" ");
                    UUID productId = UUID.fromString(parts[1].split("_")[1]);
                    functions.viewProduct(user, productId);
                } catch (Exception e) {
                    functions.exception(chatId, e.getMessage());
                }
                return;
            }
            if (user == null || user.getEventCode() == null) {
                functions.start(chatId, updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());
                return;
            }
            switch (user.getEventCode()) {
                case REQUEST_LANG -> functions.requestLang(user, text, updateData.getMessageId());
                case MENU -> functions.menu(user, text, 1 + updateData.getMessageId());
                case ORDER -> functions.order(user, text);
            }
        }
    }
}
