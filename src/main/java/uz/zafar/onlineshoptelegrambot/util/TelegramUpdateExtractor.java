package uz.zafar.onlineshoptelegrambot.util;

import uz.zafar.onlineshoptelegrambot.dto.TelegramUpdateData;
import uz.zafar.onlineshoptelegrambot.dto.TelegramUpdateData.MessageType;

import java.util.*;

@SuppressWarnings("unchecked")
public class TelegramUpdateExtractor {

    public static TelegramUpdateData extract(Map<String, Object> update) {

        TelegramUpdateData data = new TelegramUpdateData();
        /* ================= CALLBACK QUERY ================= */
        if (update.containsKey("callback_query")) {


            Map<String, Object> callback =
                    (Map<String, Object>) update.get("callback_query");
            String callbackQueryId = callback.get("id").toString();
            Map<String, Object> from =
                    (Map<String, Object>) callback.get("from");

            Map<String, Object> message =
                    (Map<String, Object>) callback.get("message");

            if (message != null) {
                Map<String, Object> chat =
                        (Map<String, Object>) message.get("chat");

                data.setChatId(chat != null
                        ? Long.valueOf(chat.get("id").toString())
                        : null);

                data.setMessageId(message.get("message_id") != null
                        ? Integer.valueOf(message.get("message_id").toString())
                        : null);
            }

            data.setType(MessageType.CALLBACK);
            data.setCallbackData((String) callback.get("data"));
            data.setCallbackQueryId(callbackQueryId);
            fillUser(data, from);
            return data;
        }

        /* ================= MESSAGE ================= */
        if (update.containsKey("message")) {

            Map<String, Object> message =
                    (Map<String, Object>) update.get("message");

            Map<String, Object> chat =
                    (Map<String, Object>) message.get("chat");

            Map<String, Object> from =
                    (Map<String, Object>) message.get("from");

            data.setChatId(chat != null
                    ? Long.valueOf(chat.get("id").toString())
                    : null);

            data.setMessageId(message.get("message_id") != null
                    ? Integer.valueOf(message.get("message_id").toString())
                    : null);

            /* ===== TEXT ===== */
            if (message.containsKey("text")) {
                data.setType(MessageType.TEXT);
                data.setText((String) message.get("text"));
            }

            /* ===== PHOTO ===== */
            else if (message.containsKey("photo")) {

                List<Map<String, Object>> photos =
                        (List<Map<String, Object>>) message.get("photo");

                List<String> fileIds = new ArrayList<>();

                for (Map<String, Object> photo : photos) {
                    if (photo.get("file_id") != null) {
                        fileIds.add(photo.get("file_id").toString());
                    }
                }

                data.setType(MessageType.PHOTO);
                data.setPhotoFileIds(fileIds);
            }

            /* ===== VIDEO ===== */
            else if (message.containsKey("video")) {

                Map<String, Object> video =
                        (Map<String, Object>) message.get("video");

                data.setType(MessageType.VIDEO);
                data.setVideoFileId(
                        video.get("file_id") != null
                                ? video.get("file_id").toString()
                                : null
                );
            }

            /* ===== DOCUMENT ===== */
            else if (message.containsKey("document")) {

                Map<String, Object> document =
                        (Map<String, Object>) message.get("document");

                data.setType(MessageType.DOCUMENT);
                data.setDocumentFIleId(
                        document.get("file_id") != null
                                ? document.get("file_id").toString()
                                : null
                );
            }

            /* ===== CONTACT ===== */
            else if (message.containsKey("contact")) {

                Map<String, Object> contact =
                        (Map<String, Object>) message.get("contact");

                data.setType(MessageType.CONTACT);
                data.setContactUserId(
                        contact.get("user_id") != null
                                ? Long.valueOf(contact.get("user_id").toString())
                                : null
                );
                data.setPhoneNumber((String) contact.get("phone_number"));
            }

            /* ===== LOCATION ===== */
            else if (message.containsKey("location")) {

                Map<String, Object> location =
                        (Map<String, Object>) message.get("location");

                data.setType(MessageType.LOCATION);
                data.setLatitude(
                        location.get("latitude") != null
                                ? Double.valueOf(location.get("latitude").toString())
                                : null
                );
                data.setLongitude(
                        location.get("longitude") != null
                                ? Double.valueOf(location.get("longitude").toString())
                                : null
                );
            } else {
                data.setType(MessageType.UNKNOWN);
            }

            fillUser(data, from);
            return data;
        }

        data.setType(MessageType.UNKNOWN);
        return data;
    }

    private static void fillUser(TelegramUpdateData data, Map<String, Object> from) {

        if (from == null) return;

        data.setUserId(
                from.get("id") != null
                        ? Long.valueOf(from.get("id").toString())
                        : null
        );
        data.setUsername((String) from.get("username"));
        data.setFirstName((String) from.get("first_name"));
        data.setLastName((String) from.get("last_name"));
    }
}
