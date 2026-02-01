package uz.zafar.onlineshoptelegrambot.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

public class TelegramCallbackExtractor {

    private static final Logger log = LoggerFactory.getLogger(TelegramCallbackExtractor.class);
    private static final Gson GSON = new Gson();

    @SuppressWarnings("unchecked")
    public static CallbackQuery extractCallback(Map<String, Object> update) {

        Object callbackObj = update.get("callback_query");
        if (callbackObj == null) {
            return null;
        }

        try {
            // callback_query → JsonObject
            JsonObject cbJson = GSON.toJsonTree(callbackObj).getAsJsonObject();

            CallbackQuery callbackQuery = new CallbackQuery();

            // 🔹 callback id
            if (cbJson.has("id")) {
                callbackQuery.setId(cbJson.get("id").getAsString());
            }

            // 🔹 data
            if (cbJson.has("data")) {
                callbackQuery.setData(cbJson.get("data").getAsString());
            }

            // 🔹 message
            if (cbJson.has("message") && cbJson.get("message").isJsonObject()) {

                JsonObject msgJson = cbJson.getAsJsonObject("message");

                // Message obyektini parse qilamiz
                Message message = GSON.fromJson(msgJson, Message.class);

                // 🔴 MUHIM: messageId majburan set qilinadi
                if (msgJson.has("message_id")) {
                    message.setMessageId(msgJson.get("message_id").getAsInt());
                }
                callbackQuery.setMessage(message);
            }

            return callbackQuery;

        } catch (Exception e) {
            log.error("Callback extract error", e);
            return null;
        }
    }



}
