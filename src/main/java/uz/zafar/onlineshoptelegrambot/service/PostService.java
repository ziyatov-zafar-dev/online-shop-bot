package uz.zafar.onlineshoptelegrambot.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class PostService {

    // Premium emoji ID'lari
    private static final String INSTAGRAM_EMOJI_ID = "5319160079465857105"; // 📱
    private static final String TELEGRAM_EMOJI_ID = "5399929490677708779"; // 🌐
    private static final String FIRE_EMOJI_ID = "5424972470023104089"; // 🔥
    private static final String STAR_EMOJI_ID = "5316702099747123698"; // ✨
    private static final String GIFT_EMOJI_ID = "5438240568893260274"; // 🎁
    private static final String ROCKET_EMOJI_ID = "5382021607357162402"; // 🚀
    private static final String SMILEY_EMOJI_ID = "5379732256644405206"; // 😊

    private final String instagramUrl;
    private final String telegramChannelUrl;

    public PostService(
                       @Value("${target.customer.instagram}") String instagramUrl,
                       @Value("${target.customer.telegram-channel}") String telegramChannelUrl) {
        this.instagramUrl = instagramUrl;
        this.telegramChannelUrl = telegramChannelUrl;
    }

    public void handleTarget(Long chatId, BotCustomer user,String token) {
        String messageText = getMessageText(user);

        // Inline keyboard yaratish
        JsonArray keyboardRows = new JsonArray();

        // 1-qatorda Instagram button (style="primary" - ko'k rang)
        JsonArray row1 = new JsonArray();
        JsonObject instagramBtn = new JsonObject();
        instagramBtn.addProperty("text", "Instagram");
        instagramBtn.addProperty("url", instagramUrl);
        instagramBtn.addProperty("style", "success"); // primary style - ko'k rang
        instagramBtn.addProperty("icon_custom_emoji_id", INSTAGRAM_EMOJI_ID);
        row1.add(instagramBtn);

        // 2-qatorda Telegram channel button (style="success" - yashil rang)
        JsonArray row2 = new JsonArray();
        JsonObject telegramBtn = new JsonObject();
        telegramBtn.addProperty("text", "Telegram");
        telegramBtn.addProperty("url", telegramChannelUrl);
        telegramBtn.addProperty("style", "primary"); // success style - yashil rang
        telegramBtn.addProperty("icon_custom_emoji_id", TELEGRAM_EMOJI_ID);
        row2.add(telegramBtn);

        keyboardRows.add(row1);
        keyboardRows.add(row2);

        JsonObject replyMarkup = new JsonObject();
        replyMarkup.add("inline_keyboard", keyboardRows);

        // Xabarni yuborish
        sendPost(chatId, messageText, replyMarkup.toString(),token);
    }

    private String getMessageText(BotCustomer user) {
        Language lang = user != null && user.getLanguage() != null ? user.getLanguage() : Language.UZBEK;

        // Premium emojilarni HTML formatda qo'shish
        String smileyEmoji = String.format("<tg-emoji emoji-id=\"%s\">😊</tg-emoji>", SMILEY_EMOJI_ID);
        String instagramEmoji = String.format("<tg-emoji emoji-id=\"%s\">📱</tg-emoji>", INSTAGRAM_EMOJI_ID);
        String giftEmoji = String.format("<tg-emoji emoji-id=\"%s\">🎁</tg-emoji>", GIFT_EMOJI_ID);
        String fireEmoji = String.format("<tg-emoji emoji-id=\"%s\">🔥</tg-emoji>", FIRE_EMOJI_ID);
        String rocketEmoji = String.format("<tg-emoji emoji-id=\"%s\">🚀</tg-emoji>", ROCKET_EMOJI_ID);
        String starEmoji = String.format("<tg-emoji emoji-id=\"%s\">✨</tg-emoji>", STAR_EMOJI_ID);

        return switch (lang) {
            case UZBEK -> String.format("""
                    %s Bizni ijtimoiy tarmoqlarda kuzatib boring!
                    
                    %s Eng so'nggi yangiliklar
                    %s Maxsus chegirmalar
                    %s Ajoyib takliflar
                    
                    %s Barchasini birinchilardan bo'lib bilib oling! %s
                    """, smileyEmoji, instagramEmoji, giftEmoji, fireEmoji, rocketEmoji, starEmoji);

            case CYRILLIC -> String.format("""
                    %s Бизни ижтимоий тармоқларда кузатиб боринг!
                    
                    %s Энг сўнгги янгиликлар
                    %s Махсус чегирмалар
                    %s Ажойиб таклифлар
                    
                    %s Барчасини биринчилардан бўлиб билиб олинг! %s
                    """, smileyEmoji, instagramEmoji, giftEmoji, fireEmoji, rocketEmoji, starEmoji);

            case RUSSIAN -> String.format("""
                    %s Следите за нами в социальных сетях!
                    
                    %s Последние новости
                    %s Специальные скидки
                    %s Отличные предложения
                    
                    %s Узнавайте всё первыми! %s
                    """, smileyEmoji, instagramEmoji, giftEmoji, fireEmoji, rocketEmoji, starEmoji);

            case ENGLISH -> String.format("""
                    %s Follow us on social media!
                    
                    %s Latest news
                    %s Special discounts
                    %s Great offers
                    
                    %s Be the first to know everything! %s
                    """, smileyEmoji, instagramEmoji, giftEmoji, fireEmoji, rocketEmoji, starEmoji);
        };
    }

    private void sendPost(Long chatId, String text, String inlineKeyboardJson,String botToken) {
        try {
            URL url = new URL("https://api.telegram.org/bot" + botToken + "/sendMessage");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            JsonObject body = new JsonObject();
            body.addProperty("chat_id", chatId);
            body.addProperty("text", text);
            body.addProperty("parse_mode", "HTML");

            // reply_markup ni to'g'ri parse qilish
            JsonObject replyMarkup = JsonParser.parseString(inlineKeyboardJson).getAsJsonObject();
            body.add("reply_markup", replyMarkup);

            String requestBody = body.toString();
            System.out.println("Sending request: " + requestBody); // Debug uchun

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Telegram xatolik! Response code: " + responseCode);
                // Error response ni o'qish
                if (conn.getErrorStream() != null) {
                    String errorResponse = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                    System.err.println("Error details: " + errorResponse);
                }
            } else {
                System.out.println("Message sent successfully to chat: " + chatId);
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void editPost(Long chatId, Integer messageId, String text, String inlineKeyboardJson,String botToken) {
        try {
            URL url = new URL("https://api.telegram.org/bot" + botToken + "/editMessageText");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            JsonObject body = new JsonObject();
            body.addProperty("chat_id", chatId);
            body.addProperty("message_id", messageId);
            body.addProperty("text", text);
            body.addProperty("parse_mode", "HTML");

            JsonObject replyMarkup = JsonParser.parseString(inlineKeyboardJson).getAsJsonObject();
            body.add("reply_markup", replyMarkup);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Edit message xatolik! Response code: " + responseCode);
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}