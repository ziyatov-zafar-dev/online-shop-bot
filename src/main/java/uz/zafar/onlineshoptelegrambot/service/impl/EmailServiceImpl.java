package uz.zafar.onlineshoptelegrambot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.dto.enums.Purpose;
import uz.zafar.onlineshoptelegrambot.service.EmailService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = Logger.getLogger(EmailServiceImpl.class.getName());
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";
    private final String brevoApiKey;

    private static final String SENDER_EMAIL = "codebyzplatform@gmail.com";
    private static final String SENDER_NAME = "SH-Z MARKET";

    private static final String SHOP_COLOR_PRIMARY = "#FF6B35";
    private static final String SHOP_COLOR_SECONDARY = "#2E86AB";
    private static final String SHOP_COLOR_SUCCESS = "#4CAF50";
    private static final String SHOP_COLOR_LIGHT = "#F8F9FA";
    private static final String SHOP_COLOR_DARK = "#2D3047";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmailServiceImpl(@Value("${brevo.api.key}") String brevoApiKey) {
        this.brevoApiKey = brevoApiKey;
    }

    @Override
    public void send(String email, String code, String title, Purpose purpose) {
        if (email == null || email.isBlank()) {
            log.severe("❌ Email manzili berilmagan!");
            return;
        }

        if (code == null || code.isBlank()) {
            log.severe("❌ Kod berilmagan! Email: " + email);
            return;
        }

        log.info("📧 Email yuborilmoqda: " + email + " | Kod: " + code + " | Maqsad: " + purpose);

        try {
            String subject = resolveSubject(title, purpose);
            String html = buildBeautifulHtml(code, purpose);

            Map<String, Object> sender = new HashMap<>();
            sender.put("name", SENDER_NAME);
            sender.put("email", SENDER_EMAIL);

            Map<String, Object> to = new HashMap<>();
            to.put("email", email);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", sender);
            requestBody.put("to", new Map[]{to});
            requestBody.put("subject", subject);
            requestBody.put("htmlContent", html);

            String json = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_URL))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("api-key", brevoApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(java.time.Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            String body = response.body();

            if (status >= 200 && status < 300) {
                log.info("✅ EMAIL YUBORILDI | " + email + " | Kod: " + code);
            } else {
                log.severe("❌ EMAIL YUBORILMADI");
                log.severe("Status: " + status);
                log.severe("Response: " + body);
                if (status == 401 || status == 403) {
                    log.severe("⚠️ API kalit xato yoki ruxsat yo'q");
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "💥 EMAIL EXCEPTION", e);
        }
    }

    private String resolveSubject(String title, Purpose purpose) {
        if (title != null && !title.isBlank()) return title;

        return switch (purpose) {
            case SIGN_IN -> (SENDER_NAME + " - Tizimga kirish kodi");
            case SIGN_UP -> (SENDER_NAME + " - Ro'yxatdan o'tish kodi");
            case FORGOT_PASSWORD -> (SENDER_NAME + " - Parolni tiklash kodi");
            case CHANGE_PASSWORD -> (SENDER_NAME + " - Parol o'zgartirildi");
        };
    }

    private String buildBeautifulHtml(String code, Purpose purpose) {
        String message = getMessageByPurpose(purpose);
        String actionText = getActionTextByPurpose(purpose);

        return ("""
                <!DOCTYPE html>
                <html lang="uz">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>"""
                + SENDER_NAME + """
                    </title>
                    <style>
                        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');
                
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }
                
                        body {
                            font-family: 'Inter', sans-serif;
                            background-color: %s;
                            padding: 20px;
                            color: #333;
                        }
                
                        .email-container {
                            max-width: 600px;
                            margin: 0 auto;
                            background: white;
                            border-radius: 20px;
                            overflow: hidden;
                            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
                        }
                
                        .header {
                            background: linear-gradient(135deg, %s, %s);
                            padding: 40px 30px;
                            text-align: center;
                            color: white;
                        }
                
                        .logo {
                            font-size: 36px;
                            font-weight: 700;
                            margin-bottom: 10px;
                            letter-spacing: 1px;
                        }
                
                        .logo span {
                            color: #FFD700;
                        }
                
                        .tagline {
                            font-size: 16px;
                            opacity: 0.9;
                            font-weight: 300;
                        }
                
                        .content {
                            padding: 40px 30px;
                        }
                
                        .greeting {
                            font-size: 24px;
                            color: %s;
                            margin-bottom: 20px;
                            font-weight: 600;
                        }
                
                        .message {
                            font-size: 16px;
                            line-height: 1.6;
                            color: #555;
                            margin-bottom: 30px;
                        }
                
                        .code-container {
                            background: linear-gradient(135deg, %s15, %s15);
                            border: 2px dashed %s;
                            border-radius: 15px;
                            padding: 30px;
                            text-align: center;
                            margin: 30px 0;
                            transition: all 0.3s ease;
                        }
                
                        .code-container:hover {
                            transform: translateY(-2px);
                            box-shadow: 0 5px 15px rgba(255, 107, 53, 0.2);
                        }
                
                        .code-label {
                            font-size: 14px;
                            color: #666;
                            margin-bottom: 10px;
                            text-transform: uppercase;
                            letter-spacing: 1px;
                        }
                
                        .code {
                            font-size: 48px;
                            font-weight: 700;
                            color: %s;
                            letter-spacing: 8px;
                            font-family: 'Courier New', monospace;
                            text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
                        }
                
                        .action-text {
                            font-size: 14px;
                            color: #888;
                            font-style: italic;
                            margin-top: 15px;
                        }
                
                        .info-box {
                            background-color: %s;
                            border-left: 4px solid %s;
                            padding: 20px;
                            border-radius: 10px;
                            margin: 30px 0;
                        }
                
                        .info-title {
                            font-weight: 600;
                            color: %s;
                            margin-bottom: 10px;
                            font-size: 16px;
                        }
                
                        .info-text {
                            color: #666;
                            font-size: 14px;
                            line-height: 1.5;
                        }
                
                        .footer {
                            background-color: %s;
                            padding: 30px;
                            text-align: center;
                            border-top: 1px solid #eee;
                        }
                
                        .shop-name {
                            color: %s;
                            font-weight: 700;
                            font-size: 20px;
                            margin-bottom: 10px;
                        }
                
                        .contact {
                            color: #666;
                            font-size: 14px;
                            margin: 10px 0;
                        }
                
                        .social-links {
                            margin-top: 20px;
                        }
                
                        .social-link {
                            display: inline-block;
                            margin: 0 10px;
                            color: %s;
                            text-decoration: none;
                            font-weight: 500;
                            transition: color 0.3s;
                        }
                
                        .social-link:hover {
                            color: %s;
                        }
                
                        .copyright {
                            margin-top: 20px;
                            color: #999;
                            font-size: 12px;
                        }
                
                        .telegram-link {
                            display: inline-flex;
                            align-items: center;
                            gap: 8px;
                            background: linear-gradient(135deg, #0088cc, #34aadc);
                            color: white;
                            padding: 10px 20px;
                            border-radius: 25px;
                            text-decoration: none;
                            font-weight: 500;
                            margin-top: 15px;
                            transition: transform 0.3s;
                        }
                
                        .telegram-link:hover {
                            transform: translateY(-2px);
                        }
                
                        .timer {
                            background: %s;
                            color: white;
                            padding: 8px 15px;
                            border-radius: 20px;
                            font-size: 12px;
                            display: inline-block;
                            margin-top: 10px;
                            font-weight: 600;
                        }
                
                        @media (max-width: 600px) {
                            .content {
                                padding: 30px 20px;
                            }
                
                            .header {
                                padding: 30px 20px;
                            }
                
                            .code {
                                font-size: 36px;
                                letter-spacing: 6px;
                            }
                
                            .logo {
                                font-size: 28px;
                            }
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <!-- Header qismi -->
                        <div class="header">
                            <div class="logo">SH-Z<span>MARKET</span></div>
                            <div class="tagline">Eng yaxshi mahsulotlar, eng qulay narxlarda</div>
                        </div>
                
                        <!-- Kontent qismi -->
                        <div class="content">
                            <div class="greeting">Salom, aziz mijoz! 👋</div>
                
                            <div class="message">
                                %s
                            </div>
                
                            <!-- Kod ko'rsatish qismi -->
                            <div class="code-container">
                                <div class="code-label">Tasdiqlash kodingiz</div>
                                <div class="code">%s</div>
                                <div class="action-text">%s</div>
                                <div class="timer">⏳ Kod 2 daqiqa amal qiladi</div>
                            </div>
                
                            <!-- Qo'shimcha ma'lumot -->
                            <div class="info-box">
                                <div class="info-title">💡 Eslatma</div>
                                <div class="info-text">
                                    Ushbu kod faqat siz uchun yaratilgan. Hech kimga ko'rsatmang yoki unga javob bermang. 
                                    Agar bu so'rovni siz amalga oshirmagan bo'lsangiz, xabarni e'tiborsiz qoldiring.
                                </div>
                            </div>
                        </div>
                
                        <!-- Footer qismi -->
                        <div class="footer">
                            <div class="shop-name">"""
                + SENDER_NAME + """
                </div>
                <div class="contact">📧 Murojaat uchun: support@shzmarket.uz</div>
                <div class="contact">📞 Telefon: +998 XX XXX XX XX</div>
                
                <div class="social-links">
                    <a href="https://t.me/sh_z_friends_market_robot" class="telegram-link">
                        📱 Telegram botimiz
                    </a>
                </div>
                
                <div class="copyright">
                    © 2024""" + SENDER_NAME + """
                            . Barcha huquqlar himoyalangan.
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """).formatted(
                // CSS uchun ranglar
                SHOP_COLOR_LIGHT,
                SHOP_COLOR_PRIMARY,
                SHOP_COLOR_SECONDARY,
                SHOP_COLOR_DARK,
                SHOP_COLOR_PRIMARY,
                SHOP_COLOR_SECONDARY,
                SHOP_COLOR_PRIMARY,
                SHOP_COLOR_PRIMARY,
                SHOP_COLOR_LIGHT,
                SHOP_COLOR_SECONDARY,
                SHOP_COLOR_SECONDARY,
                SHOP_COLOR_LIGHT,
                SHOP_COLOR_PRIMARY,
                SHOP_COLOR_PRIMARY,
                SHOP_COLOR_SECONDARY,
                SHOP_COLOR_PRIMARY,

                // Kontent
                message,
                code,
                actionText
        );
    }

    private String getMessageByPurpose(Purpose purpose) {
        return switch (purpose) {
            case SIGN_IN ->
                    SENDER_NAME + " tizimiga kirish uchun quyidagi tasdiqlash kodidan foydalaning. Bu kod sizning hisobingizni himoya qilish uchun zarur.";
            case SIGN_UP ->
                    SENDER_NAME + " da ro'yxatdan o'tishni yakunlash uchun quyidagi tasdiqlash kodidan foydalaning. Bizning jamoamizga xush kelibsiz!";
            case FORGOT_PASSWORD ->
                    "Parolingizni tiklash uchun quyidagi koddan foydalaning. Yangi parol o'rnatgach, xavfsizlik uchun uni hech kimga bermang.";
            case CHANGE_PASSWORD ->
                    "Parolingiz muvaffaqiyatli o'zgartirildi. Agar bu amaliyotni siz amalga oshirmagan bo'lsangiz, darhol biz bilan bog'laning.";
        };
    }

    private String getActionTextByPurpose(Purpose purpose) {
        return switch (purpose) {
            case SIGN_IN -> "Ushbu kodni tizimga kirish sahifasiga kiriting";
            case SIGN_UP -> "Ushbu kodni ro'yxatdan o'tish sahifasiga kiriting";
            case FORGOT_PASSWORD -> "Ushbu kodni parolni tiklash sahifasiga kiriting";
            case CHANGE_PASSWORD -> "Parol o'zgartirildi, qo'shimcha harakat talab qilinmaydi";
        };
    }

    //    @Override
    public void sendWelcomeEmail(String email, String username) {
        try {
            String subject = SENDER_NAME + " ga xush kelibsiz! 🎉";
            String html = buildWelcomeHtml(username);

            Map<String, Object> sender = new HashMap<>();
            sender.put("name", SENDER_NAME);
            sender.put("email", SENDER_EMAIL);

            Map<String, Object> to = new HashMap<>();
            to.put("email", email);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", sender);
            requestBody.put("to", new Map[]{to});
            requestBody.put("subject", subject);
            requestBody.put("htmlContent", html);

            String json = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_URL))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("api-key", brevoApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("✅ Welcome email yuborildi! " + email);
            } else {
                log.warning("⚠️ Welcome email yuborilmadi: " + response.statusCode());
            }
        } catch (Exception e) {
            log.severe("❌ Welcome email xatosi: " + e.getMessage());
        }
    }

    private String buildWelcomeHtml(String username) {
        return ("""
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background: #f5f5f5; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 15px; padding: 40px; }
                        .welcome { color: %s; font-size: 28px; margin-bottom: 20px; }
                        .benefits { margin: 20px 0; }
                        .benefit-item { display: flex; align-items: center; margin: 10px 0; }
                        .benefit-item svg { margin-right: 10px; color: %s; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="welcome">Xush kelibsiz, %s! 🎉</div>
                        <p>"""
                + SENDER_NAME +
                """
                        jamoasiga qo'shilganingiz bilan tabriklaymiz!</p>
                                        <div class="benefits">
                                            <div class="benefit-item">✅ Keng mahsulotlar tanlovi</div>
                                            <div class="benefit-item">🚀 Tez yetkazib berish</div>
                                            <div class="benefit-item">💰 Qulay narxlar</div>
                                            <div class="benefit-item">📞 24/7 mijozlar qo'llab-quvvati</div>
                                        </div>
                                    </div>
                                </body>
                                </html>
                        """).formatted(SHOP_COLOR_PRIMARY, SHOP_COLOR_SECONDARY, username);
    }

    //    @Override
    public boolean testConnection() {
        try {
            log.info("🔌 Brevo API ulanishi tekshirilmoqda...");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/account"))
                    .header("accept", "application/json")
                    .header("api-key", brevoApiKey)
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                log.info("✅ Brevo API ga ulandi!");
                return true;
            } else {
                log.warning("⚠️ Brevo API ulanish xatosi: " + response.statusCode());
                return false;
            }
        } catch (Exception e) {
            log.severe("❌ Brevo test xatosi: " + e.getMessage());
            return false;
        }
    }
}