package uz.zafar.onlineshoptelegrambot.botservice.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.zafar.onlineshoptelegrambot.dto.TelegramUpdateData;
import uz.zafar.onlineshoptelegrambot.botservice.AdminsTelegramBotFunction;
import uz.zafar.onlineshoptelegrambot.botservice.TelegramAdminsService;
import uz.zafar.onlineshoptelegrambot.util.TelegramCallbackExtractor;
import uz.zafar.onlineshoptelegrambot.util.TelegramUpdateExtractor;

import java.util.Map;

@Service
public class TelegramAdminsServiceImpl implements TelegramAdminsService {

    private final AdminsTelegramBotFunction functions;

    public TelegramAdminsServiceImpl(AdminsTelegramBotFunction functions) {
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
                Object callbackObj = update.get("callback_query");
                Map<String, Object> callbacks =
                        (Map<String, Object>) update.get("callback_query");
                String callbackQueryId = (String) callbacks.get("id");

            }

            return;
        }
        if (updateData.getType() == TelegramUpdateData.MessageType.LOCATION) {
            String location = """
                    latitude: %f
                    longitude: %f
                    """.formatted(
                    updateData.getLatitude(), updateData.getLongitude()
            );
            return;
        }
        if (updateData.getType() == TelegramUpdateData.MessageType.CONTACT) {
            String contact = """
                    telefon raqam: %s
                    user id: %d
                    """.formatted(
                    updateData.getPhoneNumber(),
                    updateData.getContactUserId()
            );
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
        if (text.equals("/start"))functions.start(chatId, updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());
    }
}
