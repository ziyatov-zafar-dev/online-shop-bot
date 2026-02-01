

package uz.zafar.onlineshoptelegrambot.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramBot {


    private final String botToken;
    private final ObjectMapper objectMapper;

    @Autowired
    public TelegramBot(ObjectMapper objectMapper) {
        this.botToken = null;
        this.objectMapper = objectMapper;
    }

    public TelegramBot(String botToken) {
        this.botToken = botToken;
        this.objectMapper = new ObjectMapper();
    }

    public TelegramBot(String botToken, ObjectMapper objectMapper) {
        this.botToken = botToken;
        this.objectMapper = objectMapper;
    }


    public void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, null, null);
    }

    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup inlineKeyboard) {
        sendMessage(chatId, text, inlineKeyboard, null);
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboardMarkup replyKeyboard) {
        sendMessage(chatId, text, null, replyKeyboard);
    }

    private static final int MAX_LENGTH = 4096;

    /*public void sendMessageLongText(Long chatId, String text, ReplyKeyboardMarkup replyKeyboard) {
        if (text == null || text.isEmpty()) return;

        int length = text.length();
        int start = 0;
        while (start < length) {
            int end = Math.min(start + MAX_LENGTH, length);
            String part = text.substring(start, end);
            sendMessage(chatId, part, null, replyKeyboard);

            start = end;
        }
    }*/

    public void sendMessageLongText(Long chatId, String text, ReplyKeyboardMarkup replyKeyboard) {
        if (text == null || text.isEmpty()) return;

        // HTML teglarini hisobga olgan holda qirqish
        List<String> parts = splitHtmlMessage(text, MAX_LENGTH);

        for (String part : parts) {
            // Bo'sh bo'lmagan qismlarni yuborish
            if (part != null && !part.trim().isEmpty()) {
                sendMessage(chatId, part, null, replyKeyboard);
            }
        }
    }

    public void sendMessageLongText(Long chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        if (text == null || text.isEmpty()) return;

        // HTML teglarini hisobga olgan holda qirqish
        List<String> parts = splitHtmlMessage(text, MAX_LENGTH);

        for (String part : parts) {
            // Bo'sh bo'lmagan qismlarni yuborish
            if (part != null && !part.trim().isEmpty()) {
                sendMessage(chatId, part, inlineKeyboardMarkup, null);
            }
        }
    }

    private List<String> splitHtmlMessage(String html, int maxLength) {
        List<String> parts = new ArrayList<>();

        if (html == null || html.isEmpty()) {
            return parts;
        }

        // HTML teglarini aniqlash uchun regex - yaxshilangan versiya
        Pattern tagPattern = Pattern.compile("(<[^>]+>|[^<]+)");
        Matcher matcher = tagPattern.matcher(html);

        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            String token = matcher.group();
            // Bo'sh teglarni olib tashlash
            if (!token.equals("<>") && !token.equals("</>")) {
                tokens.add(token);
            }
        }

        // Agar hech qanday token bo'lmasa
        if (tokens.isEmpty()) {
            // Oddiy matnni qirqish
            return splitPlainText(html, maxLength);
        }

        StringBuilder currentPart = new StringBuilder();
        Deque<String> openTags = new ArrayDeque<>();
        // Ruxsat etilgan HTML teglar ro'yxati
        Set<String> allowedTags = Set.of("b", "strong", "i", "em", "u", "ins", "s", "strike", "del",
                "a", "code", "pre", "span", "p", "br");

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            // Agar token tag bo'lsa
            if (token.startsWith("<") && token.endsWith(">")) {
                // Bo'sh tag ni tekshirish
                if (token.length() <= 2) {
                    continue; // Bo'sh tag ni o'tkazib yuborish
                }

                // Yopiluvchi tag
                if (token.startsWith("</")) {
                    String tagName = extractTagName(token.substring(2, token.length() - 1));

                    // Agar bu tegn yopilishi kerak bo'lsa
                    if (!openTags.isEmpty() && openTags.peek().equals(tagName)) {
                        openTags.pop();
                    }
                    currentPart.append(token);
                }
                // Ochiq tag
                else if (!token.startsWith("<!--")) { // Comment emasligini tekshirish
                    String tagName = extractTagName(token.substring(1, token.length() - 1));

                    // Faqat ruxsat etilgan teglarni qo'shish
                    if (allowedTags.contains(tagName.toLowerCase())) {
                        // Self-closing tag ni tekshirish
                        if (!token.endsWith("/>") && !token.matches(".*\\s+/>$")) {
                            openTags.push(tagName);
                        }
                        currentPart.append(token);
                    } else {
                        // Ruxsat etilmagan teg - faqat matn sifatida qo'shish
                        currentPart.append(escapeHtml(token));
                    }
                } else {
                    // Comment - o'tkazib yuborish yoki saqlash
                    // currentPart.append(token); // Comment larni o'tkazib yuborish tavsiya etiladi
                }
            }
            // Agar token matn bo'lsa
            else {
                String textContent = token;

                // Matnni maxLength ga mos qirqish
                while (textContent.length() > 0) {
                    // Qancha joy qolganligini hisoblash
                    int remainingSpace = maxLength - currentPart.length();

                    // Agar hozirgi qismda joy bo'lsa
                    if (remainingSpace > 0) {
                        // Matnning bir qismini qo'shish
                        int chunkSize = Math.min(remainingSpace, textContent.length());
                        String chunk = textContent.substring(0, chunkSize);
                        currentPart.append(chunk);
                        textContent = textContent.substring(chunkSize);
                    }

                    // Agar matn hali ham uzun bo'lsa yoki qism to'ldi
                    if (textContent.length() > 0 || (currentPart.length() >= maxLength && i < tokens.size() - 1)) {
                        // Ochiq teglarni yopish
                        closeHtmlTags(currentPart, openTags);

                        // Qismni saqlash
                        String part = currentPart.toString().trim();
                        if (!part.isEmpty()) {
                            parts.add(part);
                        }

                        // Yangi qismni boshlash, oldingi ochiq teglarni qayta ochish
                        currentPart = new StringBuilder();
                        reopenHtmlTags(currentPart, openTags, allowedTags);
                    }
                }
            }

            // Agar oxirgi token bo'lsa
            if (i == tokens.size() - 1 && currentPart.length() > 0) {
                closeHtmlTags(currentPart, openTags);
                String part = currentPart.toString().trim();
                if (!part.isEmpty()) {
                    parts.add(part);
                }
            }
        }

        // Agar hech qanday qism yaratilmagan bo'lsa
        if (parts.isEmpty() && !html.trim().isEmpty()) {
            parts.add(html.trim());
        }

        return parts;
    }

    private String extractTagName(String tagContent) {
        // Teg nomini ajratib olish
        String[] parts = tagContent.split("\\s+", 2);
        return parts[0].toLowerCase();
    }

    private void closeHtmlTags(StringBuilder sb, Deque<String> openTags) {
        // Stack ni nusxalash (teskari tartibda yopish uchun)
        Deque<String> tagsToClose = new ArrayDeque<>();

        // Stack ni to'g'ri tartibda nusxalash
        for (String tag : openTags) {
            tagsToClose.push(tag);
        }

        while (!tagsToClose.isEmpty()) {
            String tag = tagsToClose.pop();
            // Faqat haqiqiy tag nomlari uchun
            if (tag != null && !tag.trim().isEmpty()) {
                sb.append("</").append(tag).append(">");
            }
        }
    }

    private void reopenHtmlTags(StringBuilder sb, Deque<String> openTags, Set<String> allowedTags) {
        // Taglarni asl tartibda qayta ochish
        List<String> tagsList = new ArrayList<>();

        // Stack ni to'g'ri tartibda o'qish
        for (String tag : openTags) {
            tagsList.add(tag);
        }

        Collections.reverse(tagsList);

        for (String tag : tagsList) {
            // Faqat haqiqiy va ruxsat etilgan taglar
            if (tag != null && !tag.trim().isEmpty() && allowedTags.contains(tag.toLowerCase())) {
                sb.append("<").append(tag).append(">");
            }
        }
    }

    private List<String> splitPlainText(String text, int maxLength) {
        List<String> parts = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + maxLength, text.length());
            String part = text.substring(start, end).trim();

            if (!part.isEmpty()) {
                parts.add(part);
            }

            start = end;
        }

        return parts;
    }

    private String escapeHtml(String text) {
        // HTML belgilarni escape qilish
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // Yoki soddaroq va ishonchli yondashuv:
    public void sendMessageLongTextSimple(Long chatId, String text, ReplyKeyboardMarkup replyKeyboard) {
        if (text == null || text.isEmpty()) return;

        // Telegram HTML parse qilish imkoniyatlari
        // Ruxsat etilgan teglar: <b>, <i>, <u>, <s>, <a href="...">, <code>, <pre>

        List<String> parts = new ArrayList<>();
        int index = 0;

        while (index < text.length()) {
            int endIndex = Math.min(index + MAX_LENGTH, text.length());

            // HTML strukturasini saqlash
            if (endIndex < text.length()) {
                // Tag lar bo'ylab to'liq kesib olish
                int lastTagStart = text.lastIndexOf('<', endIndex);
                int tagEnd = text.indexOf('>', lastTagStart);

                // Agar tag ichida bo'lsak
                if (lastTagStart > index && tagEnd > endIndex) {
                    endIndex = tagEnd + 1;
                }

                // Yopiluvchi tag larni tekshirish
                String[] htmlTags = {"b", "strong", "i", "em", "u", "ins", "s", "strike", "del", "a", "code", "pre"};
                for (String tag : htmlTags) {
                    String closeTag = "</" + tag + ">";
                    int closePos = text.lastIndexOf(closeTag, endIndex);

                    if (closePos > index && closePos + closeTag.length() > endIndex) {
                        endIndex = closePos + closeTag.length();
                        break;
                    }
                }
            }

            String part = text.substring(index, endIndex);

            // Bo'sh yoki faqat tag lardan iborat bo'lmaganligini tekshirish
            String cleanPart = part.replaceAll("\\s+", "").replaceAll("<[^>]+>", "");
            if (!cleanPart.isEmpty()) {
                parts.add(part);
            }

            index = endIndex;
        }

        // Har bir qismni yuborish
        for (String part : parts) {
            if (part != null && !part.trim().isEmpty()) {
                sendMessage(chatId, part, null, replyKeyboard);
            }
        }
    }

    public boolean sendMessage1(
            Long chatId,
            String text,
            InlineKeyboardMarkup inlineKeyboard
    ) {

        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan!");
            return false;
        }

        HttpURLConnection conn = null;

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);

            if (text == null || text.trim().isEmpty()) {
                text = " ";
            }

            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"chat_id\":").append(chatId).append(",");
            json.append("\"text\":\"").append(escapeJson(text.trim())).append("\",");
            json.append("\"parse_mode\":\"HTML\"");

            if (inlineKeyboard != null) {
                json.append(",\"reply_markup\":")
                        .append(objectMapper.writeValueAsString(inlineKeyboard));
            }

            json.append("}");
            return sendRequest(conn, json.toString());
        } catch (Exception e) {
            System.err.println("sendMessage xatosi: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }


    private void sendMessage(Long chatId, String text,
                             InlineKeyboardMarkup inlineKeyboard,
                             ReplyKeyboardMarkup replyKeyboard) {

        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan!");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (text == null || text.trim().isEmpty()) {
                text = " ";
            }

            // JSON ni qo‘lda yig‘amiz
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"chat_id\":").append(chatId).append(",");
            json.append("\"text\":\"").append(escapeJson(text.trim())).append("\",");
            json.append("\"parse_mode\":\"HTML\"");
            if (inlineKeyboard != null) {
                json.append(",\"reply_markup\":")
                        .append(objectMapper.writeValueAsString(inlineKeyboard));
            } else if (replyKeyboard != null) {
                json.append(",\"reply_markup\":")
                        .append(objectMapper.writeValueAsString(replyKeyboard));
            }
            json.append("}");
            sendRequest(conn, json.toString());

        } catch (Exception e) {
            System.err.println("sendMessage xatosi: " + e.getMessage());
        }
    }

    public void sendMediaGroup(Long chatId, List<String> photoUrls, InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan!");
            return;
        }

        if (photoUrls == null || photoUrls.isEmpty()) {
            System.err.println("Hech qanday rasm URL berilmagan!");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMediaGroup";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // media array yaratish
            StringBuilder mediaJson = new StringBuilder();
            mediaJson.append("[");
            for (int i = 0; i < photoUrls.size(); i++) {
                String url = photoUrls.get(i);
                mediaJson.append("{\"type\":\"photo\",\"media\":\"")
                        .append(escapeJson(url))
                        .append("\"}");
                if (i != photoUrls.size() - 1) {
                    mediaJson.append(",");
                }
            }
            mediaJson.append("]");

            // JSONni yig‘ish
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"chat_id\":").append(chatId).append(",");
            json.append("\"media\":").append(mediaJson.toString());
            if (inlineKeyboard != null) {
                json.append(",\"reply_markup\":")
                        .append(objectMapper.writeValueAsString(inlineKeyboard));
            }
            json.append("}");

            sendRequest(conn, json.toString());

        } catch (Exception e) {
            System.err.println("sendMediaGroup xatosi: " + e.getMessage());
        }
    }

    public boolean sendPhoto(Long chatId, String photoUrl, String caption, InlineKeyboardMarkup inlineKeyboard) {

        try {
            if (botToken == null || botToken.isEmpty()) {
                System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
                return false;
            }
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendPhoto";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (caption == null) caption = "";

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }
            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"photo\": \"%s\", \"caption\": \"%s\", \"reply_markup\": %s, \"parse_mode\": \"HTML\"}",
                    chatId, escapeJson(photoUrl), escapeJson(caption), replyMarkupJson
            );

            return sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendPhoto xatosi: " + e.getMessage());
            return false;
        }
    }

    public boolean sendPhoto(Long chatId, String photoUrl, String caption, ReplyKeyboardMarkup inlineKeyboard) {

        try {
            if (botToken == null || botToken.isEmpty()) {
                System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
                return false;
            }
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendPhoto";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (caption == null) caption = "";

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }
            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"photo\": \"%s\", \"caption\": \"%s\", \"reply_markup\": %s, \"parse_mode\": \"HTML\"}",
                    chatId, escapeJson(photoUrl), escapeJson(caption), replyMarkupJson
            );

            return sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendPhoto xatosi: " + e.getMessage());
            return false;
        }
    }

    public void sendPhoto(Long chatId, String photoUrl, String caption) {

        try {
            if (botToken == null || botToken.isEmpty()) {
                System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
                return;
            }
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendPhoto";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (caption == null) caption = "";


            // JSON body
            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"photo\": \"%s\", \"caption\": \"%s\", \"parse_mode\": \"HTML\"}",
                    chatId, escapeJson(photoUrl), escapeJson(caption)
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendPhoto xatosi: " + e.getMessage());
        }
    }

    public void sendPhotoFile(Long chatId, File photoFile, String caption,
                              InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            sendMultipartMedia(chatId, "photo", photoFile, caption, inlineKeyboard);
        } catch (Exception e) {
            System.err.println("sendPhotoFile xatosi: " + e.getMessage());
        }
    }


    public void sendVideo(Long chatId, String videoUrl, String caption,
                          InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendVideo";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (caption == null) caption = "";

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"video\": \"%s\", \"caption\": \"%s\", \"reply_markup\": %s, \"parse_mode\": \"HTML\"}",
                    chatId, escapeJson(videoUrl), escapeJson(caption), replyMarkupJson
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendVideo xatosi: " + e.getMessage());
        }
    }

    // 4. DOKUMENT YUBORISH

    public void sendDocument(Long chatId, String documentUrl, String caption,
                             InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendDocument";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (caption == null) caption = "";

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"document\": \"%s\", \"caption\": \"%s\", \"reply_markup\": %s, \"parse_mode\": \"HTML\"}",
                    chatId, escapeJson(documentUrl), escapeJson(caption), replyMarkupJson
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendDocument xatosi: " + e.getMessage());
        }
    }

    // 5. AUDIO YUBORISH

    public void sendAudio(Long chatId, String audioUrl, String caption,
                          InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendAudio";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (caption == null) caption = "";

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"audio\": \"%s\", \"caption\": \"%s\", \"reply_markup\": %s, \"parse_mode\": \"HTML\"}",
                    chatId, escapeJson(audioUrl), escapeJson(caption), replyMarkupJson
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendAudio xatosi: " + e.getMessage());
        }
    }

    // 6. VOICE YUBORISH

    public void sendVoice(Long chatId, String voiceUrl, InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendVoice";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"voice\": \"%s\", \"reply_markup\": %s}",
                    chatId, escapeJson(voiceUrl), replyMarkupJson
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendVoice xatosi: " + e.getMessage());
        }
    }

    // 7. LOCATION YUBORISH
    public void sendVenue(
            Long chatId,
            Double latitude,
            Double longitude,
            String title,
            String address,
            InlineKeyboardMarkup inlineKeyboard
    ) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan!");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendVenue";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }

            String jsonBody = String.format(
                    "{" +
                            "\"chat_id\": %d," +
                            "\"latitude\": %s," +
                            "\"longitude\": %s," +
                            "\"title\": \"%s\"," +
                            "\"address\": \"%s\"," +
                            "\"reply_markup\": %s" +
                            "}",
                    chatId,
                    latitude.toString().replaceAll(",", "."),
                    longitude.toString().replaceAll(",", "."),
                    escapeJson(title),
                    escapeJson(address),
                    replyMarkupJson
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendVenue xatosi: " + e.getMessage());
        }
    }

    /*public void sendVenue(
            Long chatId,
            Double latitude,
            Double longitude,
            String title,
            String address
    ) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan!");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendVenue";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = String.format(
                    "{" +
                            "\"chat_id\": %d," +
                            "\"latitude\": %f," +
                            "\"longitude\": %f," +
                            "\"title\": \"%s\"," +
                            "\"address\": \"%s\"" +
                            "}",
                    chatId,
                    latitude,
                    longitude,
                    escapeJson(title),
                    escapeJson(address)
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendVenue (no keyboard) xatosi: " + e.getMessage());
        }
    }*/

    public void sendVenue(Long chatId, Double latitude, Double longitude, String title, String address) {
        // Bot tokenini tekshirish
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan!");
            return;
        }

        // Koordinatalarni tekshirish
        if (latitude == null || longitude == null) {
            System.err.println("Koordinatalar berilmagan!");
            return;
        }

        if (Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
            System.err.println("Noto'g'ri koordinatalar: latitude[-90,90], longitude[-180,180]");
            return;
        }

        HttpURLConnection conn = null;
        OutputStream os = null;
        BufferedReader br = null;

        try {
            // 1. URL yaratish
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendVenue";
            URL url = new URL(apiUrl);

            // 2. Bog'lanishni ochish
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000); // 30 soniya
            conn.setReadTimeout(30000);    // 30 soniya

            // 3. JSON tayyorlash (maxsus belgilarni to'g'rilash)
            String safeTitle = title != null ?
                    title.replace("\\", "\\\\")
                            .replace("\"", "\\\"")
                            .replace("\n", "\\n")
                            .replace("\r", "\\r")
                            .replace("\t", "\\t") : "";

            String safeAddress = address != null ?
                    address.replace("\\", "\\\\")
                            .replace("\"", "\\\"")
                            .replace("\n", "\\n")
                            .replace("\r", "\\r")
                            .replace("\t", "\\t") : "";

            // 4. JSON tanasini yaratish
            String jsonBody = String.format(
                    "{" +
                            "\"chat_id\":%d," +
                            "\"latitude\":%s," +
                            "\"longitude\":%s," +
                            "\"title\":\"%s\"," +
                            "\"address\":\"%s\"" +
                            "}",
                    chatId,
                    latitude.toString().replaceAll(",", "."),
                    longitude.toString().replaceAll(",", "."),
                    safeTitle,
                    safeAddress
            );

            System.out.println("Yuborilayotgan JSON: " + jsonBody);

            // 5. JSON ni yuborish
            os = conn.getOutputStream();
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();

            // 6. Javobni olish
            int responseCode = conn.getResponseCode();
            System.out.println("Telegram API javob kodi: " + responseCode);

            // 7. Javobni o'qish
            InputStream stream;
            if (responseCode >= 200 && responseCode < 300) {
                stream = conn.getInputStream();
                System.out.println("Venue muvaffaqiyatli yuborildi!");
            } else {
                stream = conn.getErrorStream();
            }

            // 8. Javobni o'qish va chiqarish
            if (stream != null) {
                br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                if (responseCode >= 200 && responseCode < 300) {
                    System.out.println("API javobi: " + response.toString());
                } else {
                    System.err.println("API xatosi (" + responseCode + "): " + response.toString());
                    return;
                }
            } else {
                System.err.println("API javob oqimi null!");
                return;
            }

        } catch (MalformedURLException e) {
            System.err.println("Noto'g'ri URL: " + e.getMessage());
        } catch (ProtocolException e) {
            System.err.println("Protokol xatosi: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO xatosi: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Kutilmagan xato: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // 9. Resurslarni yopish
            try {
                if (os != null) os.close();
                if (br != null) br.close();
                if (conn != null) conn.disconnect();
            } catch (IOException e) {
                System.err.println("Resurslarni yopishda xato: " + e.getMessage());
            }
        }
    }


    public void sendVenue(
            Long chatId,
            Double latitude,
            Double longitude,
            String title,
            String address,
            ReplyKeyboardMarkup replyKeyboard
    ) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan!");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendVenue";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String replyMarkupJson = "null";

            if (replyKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(replyKeyboard);
            }

            String jsonBody = String.format(
                    "{" +
                            "\"chat_id\": %d," +
                            "\"latitude\": %s," +
                            "\"longitude\": %s," +
                            "\"title\": \"%s\"," +
                            "\"address\": \"%s\"," +
                            "\"reply_markup\": %s" +
                            "}",
                    chatId,
                    latitude.toString().replaceAll(",", "."),
                    longitude.toString().replaceAll(",", "."),
                    escapeJson(title),
                    escapeJson(address),
                    replyMarkupJson
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendVenue xatosi: " + e.getMessage());
        }
    }


    public void sendLocation(Long chatId, Double latitude, Double longitude,
                             InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendLocation";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"latitude\": %s, \"longitude\": %s, \"reply_markup\": %s}",
                    chatId, latitude.toString().replaceAll(",", "."), longitude.toString().replaceAll(",", "."), replyMarkupJson
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendLocation xatosi: " + e.getMessage());
        }
    }

    public void sendLocation(Long chatId, Double latitude, Double longitude) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendLocation";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);


            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"latitude\": %s, \"longitude\": %s}",
                    chatId, latitude.toString().replaceAll(",", "."), longitude.toString().replaceAll(",", ".")
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendLocation xatosi: " + e.getMessage());
        }
    }


    // 8. CONTACT YUBORISH

    public void sendContact(Long chatId, String phoneNumber, String firstName,
                            String lastName, String vcard, InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendContact";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"phone_number\": \"%s\", \"first_name\": \"%s\", \"reply_markup\": %s}",
                    chatId, escapeJson(phoneNumber), escapeJson(firstName), replyMarkupJson
            );

            // Qo'shimcha maydonlar
            if (lastName != null) {
                jsonBody = jsonBody.replace("}", "") +
                        String.format(", \"last_name\": \"%s\"}", escapeJson(lastName));
            }

            if (vcard != null) {
                jsonBody = jsonBody.replace("}", "") +
                        String.format(", \"vcard\": \"%s\"}", escapeJson(vcard));
            }

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendContact xatosi: " + e.getMessage());
        }
    }

    public void sendContact(Long chatId, String phoneNumber, String firstName, String lastName) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendContact";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);


            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"phone_number\": \"%s\", \"first_name\": \"%s\"}",
                    chatId, escapeJson(phoneNumber), escapeJson(firstName)
            );

            // Qo'shimcha maydonlar
            if (lastName != null) {
                jsonBody = jsonBody.replace("}", "") +
                        String.format(", \"last_name\": \"%s\"}", escapeJson(lastName));
            }
            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendContact xatosi: " + e.getMessage());
        }
    }

    // 9. XABARNI O'CHIRISH

    public void deleteMessage(Long chatId, Integer messageId) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/deleteMessage";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"message_id\": %d}",
                    chatId, messageId
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("deleteMessage xatosi: " + e.getMessage());
        }
    }

    // 10. XABARNI TAHRIRLASH

    public void editMessageText(Long chatId, Integer messageId, String newText,
                                InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/editMessageText";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (newText == null || newText.trim().isEmpty()) newText = " ";

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"message_id\": %d, \"text\": \"%s\", \"reply_markup\": %s, \"parse_mode\": \"HTML\"}",
                    chatId, messageId, escapeJson(newText.trim()), replyMarkupJson
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("editMessageText xatosi: " + e.getMessage());
        }
    }

    public void editMessageText(Long chatId, Integer messageId, String newText) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/editMessageText";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (newText == null || newText.trim().isEmpty()) newText = " ";

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"message_id\": %d, \"text\": \"%s\", \"parse_mode\": \"HTML\"}",
                    chatId, messageId, escapeJson(newText.trim())
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("editMessageText xatosi: " + e.getMessage());
        }
    }

    // 11. MEDIANI TAHRIRLASH

    /**
     * Rasmni URL orqali tahrirlash
     */
    public void editMessagePhoto(Long chatId, Integer messageId, String photoUrl,
                                 String caption, InlineKeyboardMarkup inlineKeyboard) {
        editMessageMedia(chatId, messageId, "photo", photoUrl, caption, inlineKeyboard);
    }

    /**
     * Videoni URL orqali tahrirlash
     */
    public void editMessageVideo(Long chatId, Integer messageId, String videoUrl,
                                 String caption, InlineKeyboardMarkup inlineKeyboard) {
        editMessageMedia(chatId, messageId, "video", videoUrl, caption, inlineKeyboard);
    }

    /**
     * Dokumentni URL orqali tahrirlash
     */
    public void editMessageDocument(Long chatId, Integer messageId, String documentUrl,
                                    String caption, InlineKeyboardMarkup inlineKeyboard) {
        editMessageMedia(chatId, messageId, "document", documentUrl, caption, inlineKeyboard);
    }

    /**
     * Audioni URL orqali tahrirlash
     */
    public void editMessageAudio(Long chatId, Integer messageId, String audioUrl,
                                 String caption, InlineKeyboardMarkup inlineKeyboard) {
        editMessageMedia(chatId, messageId, "audio", audioUrl, caption, inlineKeyboard);
    }

    /**
     * Animation (GIF) ni URL orqali tahrirlash
     */
    public void editMessageAnimation(Long chatId, Integer messageId, String animationUrl,
                                     String caption, InlineKeyboardMarkup inlineKeyboard) {
        editMessageMedia(chatId, messageId, "animation", animationUrl, caption, inlineKeyboard);
    }

    public void editMessageMedia(Long chatId, Integer messageId, String mediaType,
                                 String mediaUrl, String caption, InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/editMessageMedia";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (caption == null) caption = "";

            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }

            String mediaJson = String.format(
                    "{\"type\": \"%s\", \"media\": \"%s\", \"caption\": \"%s\", \"parse_mode\": \"HTML\"}",
                    mediaType, escapeJson(mediaUrl), escapeJson(caption)
            );

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"message_id\": %d, \"media\": %s, \"reply_markup\": %s}",
                    chatId, messageId, mediaJson, replyMarkupJson
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("editMessageMedia xatosi: " + e.getMessage());
        }
    }

    // 12. CALLBACK QUERY JAVOBI

    public void answerCallbackQuery(String callbackQueryId, String text, Boolean showAlert) {
        answerCallbackQuery(callbackQueryId, text, showAlert, null, 0);
    }

    public void answerCallbackQuery(String callbackQueryId, String text, Boolean showAlert,
                                    String url, Integer cacheTime) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/answerCallbackQuery";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            if (text == null) text = "";
            if (showAlert == null) showAlert = false;

            String jsonBody = String.format(
                    "{\"callback_query_id\": \"%s\", \"text\": \"%s\", \"show_alert\": %s}",
                    escapeJson(callbackQueryId), escapeJson(text), showAlert
            );

            if (url != null) {
                jsonBody = jsonBody.replace("}", "") +
                        String.format(", \"url\": \"%s\"}", escapeJson(url));
            }

            if (cacheTime > 0) {
                jsonBody = jsonBody.replace("}", "") +
                        String.format(", \"cache_time\": %d}", cacheTime);
            }

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("answerCallbackQuery xatosi: " + e.getMessage());
        }
    }

    // 13. CHAT ACTION (TYPING, UPLOAD_PHOTO, ETC.)

    public void sendChatAction(Long chatId, String action) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendChatAction";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"action\": \"%s\"}",
                    chatId, action
            );

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendChatAction xatosi: " + e.getMessage());
        }
    }

    // 14. USER PROFILINI O'QISH

    public Map<String, Object> getUserProfilePhotos(Long userId, Integer offset, Integer limit) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return null;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/getUserProfilePhotos";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = String.format(
                    "{\"user_id\": %d}",
                    userId
            );

            if (offset > 0) {
                jsonBody = jsonBody.replace("}", "") + String.format(", \"offset\": %d}", offset);
            }

            if (limit > 0) {
                jsonBody = jsonBody.replace("}", "") + String.format(", \"limit\": %d}", limit);
            }

            String response = sendRequestAndGetResponse(conn, jsonBody);
            return objectMapper.readValue(response, Map.class);

        } catch (Exception e) {
            System.err.println("getUserProfilePhotos xatosi: " + e.getMessage());
            return null;
        }
    }

    // 15. FILE MA'LUMOTLARINI O'QISH

    public Map<String, Object> getFile(String fileId) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return null;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/getFile";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = String.format(
                    "{\"file_id\": \"%s\"}",
                    escapeJson(fileId)
            );

            String response = sendRequestAndGetResponse(conn, jsonBody);
            return objectMapper.readValue(response, Map.class);

        } catch (Exception e) {
            System.err.println("getFile xatosi: " + e.getMessage());
            return null;
        }
    }

    // 16. BOT MA'LUMOTLARINI O'QISH

    public Map<String, Object> getMe() {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return null;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/getMe";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("GET");

            String response = sendRequestAndGetResponse(conn, null);
            return objectMapper.readValue(response, Map.class);

        } catch (Exception e) {
            System.err.println("getMe xatosi: " + e.getMessage());
            return null;
        }
    }

    // 17. WEBHOOK SOZLASH

    public void setWebhook(String webhookUrl, String certificate, String ipAddress,
                           Integer maxConnections, List<String> allowedUpdates,
                           Boolean dropPendingUpdates) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/setWebhook";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = String.format(
                    "{\"url\": \"%s\"}",
                    escapeJson(webhookUrl)
            );

            if (maxConnections != null) {
                jsonBody = jsonBody.replace("}", "") + String.format(", \"max_connections\": %d}", maxConnections);
            }

            if (allowedUpdates != null && !allowedUpdates.isEmpty()) {
                String allowedUpdatesJson = objectMapper.writeValueAsString(allowedUpdates);
                jsonBody = jsonBody.replace("}", "") + String.format(", \"allowed_updates\": %s}", allowedUpdatesJson);
            }

            if (dropPendingUpdates != null) {
                jsonBody = jsonBody.replace("}", "") + String.format(", \"drop_pending_updates\": %s}", dropPendingUpdates);
            }

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("setWebhook xatosi: " + e.getMessage());
        }
    }

    // 18. WEBHOOK MA'LUMOTLARINI O'QISH

    public Map<String, Object> getWebhookInfo() {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return null;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/getWebhookInfo";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("GET");

            String response = sendRequestAndGetResponse(conn, null);
            return objectMapper.readValue(response, Map.class);

        } catch (Exception e) {
            System.err.println("getWebhookInfo xatosi: " + e.getMessage());
            return null;
        }
    }

    // 19. WEBHOOK O'CHIRISH

    public void deleteWebhook(Boolean dropPendingUpdates) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/deleteWebhook";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = "{}";
            if (dropPendingUpdates != null) {
                jsonBody = String.format("{\"drop_pending_updates\": %s}", dropPendingUpdates);
            }

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("deleteWebhook xatosi: " + e.getMessage());
        }
    }

    // 20. POLL YUBORISH

    public void sendPoll(Long chatId, String question, List<String> options,
                         Boolean isAnonymous, String type, Boolean allowsMultipleAnswers,
                         Integer correctOptionId, String explanation, String explanationParseMode,
                         Integer openPeriod, Integer closeDate, InlineKeyboardMarkup inlineKeyboard) {
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("Bot tokeni berilmagan! TelegramBot token bilan yaratilishi kerak.");
            return;
        }

        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendPoll";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String optionsJson = objectMapper.writeValueAsString(options);
            String replyMarkupJson = "null";
            if (inlineKeyboard != null) {
                replyMarkupJson = objectMapper.writeValueAsString(inlineKeyboard);
            }

            String jsonBody = String.format(
                    "{\"chat_id\": %d, \"question\": \"%s\", \"options\": %s, \"reply_markup\": %s}",
                    chatId, escapeJson(question), optionsJson, replyMarkupJson
            );

            // Qo'shimcha parametrlar
            if (isAnonymous != null) {
                jsonBody = jsonBody.replace("}", "") + String.format(", \"is_anonymous\": %s}", isAnonymous);
            }

            if (type != null) {
                jsonBody = jsonBody.replace("}", "") + String.format(", \"type\": \"%s\"}", type);
            }

            if (allowsMultipleAnswers != null) {
                jsonBody = jsonBody.replace("}", "") + String.format(", \"allows_multiple_answers\": %s}", allowsMultipleAnswers);
            }

            sendRequest(conn, jsonBody);

        } catch (Exception e) {
            System.err.println("sendPoll xatosi: " + e.getMessage());
        }
    }

    // YORDAMCHI METODLAR

    private void sendMultipartMedia(Long chatId, String mediaType, File mediaFile,
                                    String caption, InlineKeyboardMarkup inlineKeyboard) throws Exception {
        String apiUrl = "https://api.telegram.org/bot" + botToken + "/send" +
                mediaType.substring(0, 1).toUpperCase() + mediaType.substring(1);

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();

        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setConnectTimeout(60000);
        conn.setReadTimeout(60000);
        conn.setDoOutput(true);

        try (OutputStream outputStream = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true)) {

            // Chat ID
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n");
            writer.append(chatId.toString()).append("\r\n").flush();

            // Caption
            if (caption != null && !caption.isEmpty()) {
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"caption\"\r\n\r\n");
                writer.append(caption).append("\r\n").flush();
            }

            // Parse mode
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"parse_mode\"\r\n\r\n");
            writer.append("HTML").append("\r\n").flush();

            // Reply markup
            if (inlineKeyboard != null) {
                String keyboardJson = objectMapper.writeValueAsString(inlineKeyboard);
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"reply_markup\"\r\n\r\n");
                writer.append(keyboardJson).append("\r\n").flush();
            }

            // Media fayli
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"").append(mediaType).append("\"; filename=\"")
                    .append(mediaFile.getName()).append("\"\r\n");
            writer.append("Content-Type: ").append(Files.probeContentType(mediaFile.toPath())).append("\r\n\r\n").flush();

            try (FileInputStream fileInputStream = new FileInputStream(mediaFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            writer.append("\r\n").flush();
            writer.append("--").append(boundary).append("--\r\n").flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP xatolik: " + responseCode);
        }
    }

    /*private boolean sendRequest(HttpURLConnection conn, String jsonBody) throws IOException {
        if (jsonBody != null) {
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
                System.err.println("API xatolik (" + responseCode + "): " + errorResponse.toString());
            }
        }

    }*/
    private boolean sendRequest(HttpURLConnection conn, String jsonBody) {
        try {
            if (jsonBody != null && !jsonBody.isBlank()) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input);
                    os.flush();
                }
            }

            int responseCode = conn.getResponseCode();

            // SUCCESS: 2xx
            if (responseCode >= 200 && responseCode < 300) {
                return true;
            }

            // ERROR RESPONSE
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            conn.getErrorStream() != null
                                    ? conn.getErrorStream()
                                    : conn.getInputStream(),
                            StandardCharsets.UTF_8
                    ))) {

                StringBuilder errorResponse = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }

                System.err.println("API xatolik (" + responseCode + "): " + errorResponse);
            }

        } catch (IOException e) {
            System.err.println("API so‘rov yuborishda xatolik: " + e.getMessage());
        }

        return false;
    }


    private String sendRequestAndGetResponse(HttpURLConnection conn, String jsonBody) throws IOException {
        if (jsonBody != null) {
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"))) {
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
                throw new IOException("API xatolik (" + responseCode + "): " + errorResponse.toString());
            }
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "";

        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f");
    }

    // QO'SHIMCHA YORDAMCHI METODLAR KEYBOARD YARATISH UCHUN

    /**
     * InlineKeyboard yaratish
     */
    public InlineKeyboardMarkup createInlineKeyboard(List<List<InlineKeyboardButton>> keyboard) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    /**
     * Oddiy InlineKeyboard tugmasi yaratish
     */
    public InlineKeyboardButton createInlineButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    /**
     * URL li InlineKeyboard tugmasi yaratish
     */
    public InlineKeyboardButton createInlineButtonWithUrl(String text, String url) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setUrl(url);
        return button;
    }

    /**
     * ReplyKeyboard yaratish
     */
    public ReplyKeyboardMarkup createReplyKeyboard(List<KeyboardRow> keyboard, boolean resizeKeyboard,
                                                   boolean oneTimeKeyboard, boolean selective) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(resizeKeyboard);
        markup.setOneTimeKeyboard(oneTimeKeyboard);
        markup.setSelective(selective);
        return markup;
    }

    /**
     * Oddiy ReplyKeyboard tugmasi yaratish
     */
    public KeyboardButton createReplyButton(String text) {
        KeyboardButton button = new KeyboardButton();
        button.setText(text);
        return button;
    }

    /**
     * Kontakt so'rovli ReplyKeyboard tugmasi yaratish
     */
    public KeyboardButton createContactRequestButton(String text) {
        KeyboardButton button = new KeyboardButton();
        button.setText(text);
        button.setRequestContact(true);
        return button;
    }

    /**
     * Lokatsiya so'rovli ReplyKeyboard tugmasi yaratish
     */
    public KeyboardButton createLocationRequestButton(String text) {
        KeyboardButton button = new KeyboardButton();
        button.setText(text);
        button.setRequestLocation(true);
        return button;
    }

    // GETTER
    public String getBotToken() {
        return botToken;
    }

    /**
     * Reply keyboard ni o'chirish bilan xabar yuborish
     */
    public void sendMessage(Long chatId, String text, boolean remove) {
        if (remove) sendMessageRemoveKeyboard(chatId, text);
        else sendMessage(chatId, text);
    }

    public void sendMessageRemoveKeyboard(Long chatId, String text) {
        try {
            // 1. URL tayyorlash
            String url = "https://api.telegram.org/bot" + this.botToken + "/sendMessage";

            // 2. JSON body yaratish
            String jsonBody = "{" +
                    "\"chat_id\": \"" + chatId + "\"," +
                    "\"parse_mode\": \"" + "HTML" + "\"," +
                    "\"text\": \"" +
                    text.replace("\\", "\\\\")
                            .replace("\"", "\\\"")
                            .replace("\n", "\\n")
                            .replace("\r", "\\r")
                            .replace("\t", "\\t") +
                    "\"," +
                    "\"reply_markup\": {" +
                    "\"remove_keyboard\": true" +
                    "}" +
                    "}";

            // 3. HTTP so'rov tayyorlash
            URL telegramUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) telegramUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // 4. Body ni yozish
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 5. Javobni o'qish (majburiy emas, lekin yaxshi)
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("✅ Xabar yuborildi: " + response.toString());
                }
            } else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                    System.err.println("❌ Xatolik " + responseCode + ": " + errorResponse.toString());
                }
            }

            conn.disconnect();

        } catch (Exception e) {
            System.err.println("🚫 Xatolik yuz berdi: " + e.getMessage());
            e.printStackTrace();
        }
    }

}