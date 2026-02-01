package uz.zafar.onlineshoptelegrambot.botservice.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.enums.SellerEventCode;
import uz.zafar.onlineshoptelegrambot.dto.TelegramUpdateData;
import uz.zafar.onlineshoptelegrambot.botservice.SellersTelegramBotFunction;
import uz.zafar.onlineshoptelegrambot.botservice.TelegramSellersService;
import uz.zafar.onlineshoptelegrambot.util.TelegramCallbackExtractor;
import uz.zafar.onlineshoptelegrambot.util.TelegramUpdateExtractor;

import java.util.Map;

@Service
public class TelegramSellersServiceImpl implements TelegramSellersService {

    private final SellersTelegramBotFunction functions;


    public TelegramSellersServiceImpl(SellersTelegramBotFunction functions) {
        this.functions = functions;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleUpdate(Map<String, Object> update, HttpServletRequest request) throws Exception {
        try {
            TelegramUpdateData updateData = TelegramUpdateExtractor.extract(update);
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
                }
                BotSeller user = functions.getUser(updateData.getChatId());
                SellerEventCode eventCode = user.getEventCode();
                if (eventCode == SellerEventCode.MENU) {
                    functions.menu(user, updateData);
                    return;
                }
                if (eventCode == SellerEventCode.EXPIRED) {
                    functions.expired(user, updateData);
                    return;
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
                BotSeller user = functions.getUser(updateData.getChatId());
                if (user.getEventCode() == SellerEventCode.VERIFY)
                    functions.verify(user, updateData.getPhoneNumber(), updateData.getContactUserId());
            }
            if (updateData.getType() == TelegramUpdateData.MessageType.PHOTO) {
                BotSeller user = functions.refreshUser(updateData.getChatId(), updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());
                if (user.getEventCode() == SellerEventCode.EXPIRED) {
                    functions.expired(user, updateData.getPhotoFileIds());
                    return;
                }
                if (user.getEventCode() == SellerEventCode.MENU) {
                    functions.menu(user, updateData.getPhotoFileIds());
                    return;
                }
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
//        bot.sendMessage(user().getChatId());
            BotSeller user = functions.refreshUser(chatId, updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());

            if (text.equals("/start")) {
                functions.start(chatId, updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());
            } else {
                if (user == null) return;
                user = functions.refreshUser(chatId, updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());
                if (text.startsWith("/start")) {
                    String[] parts = text.split(" ");
                    if (parts[1].equals("updated") || parts[1].equals("app_success")) {
                        functions.start(chatId, updateData.getFirstName(), updateData.getLastName(), updateData.getUsername());
                        return;
                    }
                    return;
                }
                SellerEventCode eventCode = user.getEventCode();
                if (!functions.checkSeller(user) && eventCode != SellerEventCode.EXPIRED) return;
                if (eventCode == SellerEventCode.VERIFY) {
                    functions.verify(user, text);
                    return;
                }
                if (eventCode == SellerEventCode.MENU) {
                    functions.menu(user, text);
                    return;
                }
                if (eventCode == SellerEventCode.DEVICES) {
                    functions.devices(user, text);
                    return;
                }
                if (eventCode == SellerEventCode.EXPIRED) {
                    functions.expired(user, text);
                    return;
                }
            }
        } catch (Exception e) {
            TelegramUpdateData data = TelegramUpdateExtractor.extract(update);
            functions.start(data.getChatId(), data.getFirstName(), data.getLastName(), data.getUsername());
        }
    }

}
