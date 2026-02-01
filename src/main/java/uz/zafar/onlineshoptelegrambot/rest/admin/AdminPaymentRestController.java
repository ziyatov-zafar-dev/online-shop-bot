package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.config.TelegramProperties;
import uz.zafar.onlineshoptelegrambot.db.entity.AdminCard;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.CardType;
import uz.zafar.onlineshoptelegrambot.db.repositories.AdminCardRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.payment.request.ChangeCardRequest;
import uz.zafar.onlineshoptelegrambot.service.PaymentService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/payment")
public class AdminPaymentRestController {
    private final PaymentService paymentService;
    private final AdminCardRepository adminCardRepository;
    private final TelegramProperties telegramProperties;

    public AdminPaymentRestController(PaymentService paymentService, AdminCardRepository adminCardRepository, TelegramProperties telegramProperties) {
        this.paymentService = paymentService;
        this.adminCardRepository = adminCardRepository;
        this.telegramProperties = telegramProperties;
    }

    @GetMapping("list")
    public ResponseDto<?> findAll() {
        return paymentService.getAllPayments();
    }


    @PostMapping("/confirm")
    public ResponseDto<?> confirmPayment(@RequestBody ConfirmPaymentRequestDto req) {
        return paymentService.confirmPayment(req.getTransactionId(), req.getAmount());
    }

    @PostMapping("cancel")
    public ResponseDto<?> cancelPayment(@RequestBody CancelPaymentRequestDto req) {
        return paymentService.cancelPayment(req.getTransactionId(), req.getDesc());
    }

    @PostMapping("change-card")
    public ResponseDto<?> changeCard(@RequestBody ChangeCardRequest req) throws IOException {
        return paymentService.change(
                req.getNumber(), req.getOwner(), req.getImageUrl(), req.getCardType()
        );
    }

    @GetMapping("platform-card")
    public ResponseDto<?> platformCard() {
        return ResponseDto.success(adminCardRepository.findAll().isEmpty() ? new AdminCard(
                1, "9860 1001 2671 7766", "Zafar Ziyatov", "https://i.pinimg.com/736x/c6/25/f9/c625f90ff47a950a94fc4bd0b09f02f4.jpg", CardType.HUMO
        ) : adminCardRepository.findAll().get(0));
    }

    public static class ConfirmPaymentRequestDto {
        private String transactionId;
        private BigDecimal amount;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class CancelPaymentRequestDto {
        private String transactionId;
        private String desc;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public String createCardImage(String ownerName, String cardNumber, String cardType, String expiryDate) throws IOException {
        // Papka yaratish (agar mavjud bo'lmasa)
        Path uploadPath = Paths.get("uploads/admin-card");
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int imageWidth = 1018;
        int imageHeight = 642;
        // Rasm yaratish
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Sifat sozlamalari
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(new Color(0, 102, 204)); // #0066CC
        g2d.fillRect(0, 0, imageWidth, imageHeight);

        Font fontBold = loadFont("fonts/arialbd.ttf", 28);
        Font fontRegular = loadFont("fonts/arial.ttf", 24);
        Font fontSmall = loadFont("fonts/arial.ttf", 20);
        Font fontDigital = loadFont("fonts/arialbd.ttf", 32);
        // 1. BANK NOMI (yuqori chap)
        g2d.setFont(fontBold);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Hamkorbank", 40, 50);

        // 2. KARTA RAQAMI (markazda)
        // Raqamni formatlash: 1234 5678 9012 3456
        String formattedCardNumber = formatCardNumber(cardNumber);

        g2d.setFont(fontDigital);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(formattedCardNumber);
        int xPosition = (imageWidth - textWidth) / 2;
        int yPosition = imageHeight / 2;

        g2d.drawString(formattedCardNumber, xPosition, yPosition);

        // 3. KARTA TURI (pastki chap)
        g2d.setFont(fontBold);
        g2d.drawString(cardType.toUpperCase(), 40, imageHeight - 50);

        // 4. KARTA EGASI (pastki o'ng)
        g2d.setFont(fontRegular);
        String upperOwnerName = ownerName.toUpperCase();
        int ownerNameWidth = g2d.getFontMetrics().stringWidth(upperOwnerName);
        g2d.drawString(upperOwnerName, imageWidth - ownerNameWidth - 40, imageHeight - 50);

        // 5. "GOOD THRU" VA "VALID THRU" (o'ng tomonda)
        g2d.setFont(fontSmall);
        g2d.drawString("GOOD THRU", imageWidth - 250, 200);
        g2d.drawString("VALID THRU", imageWidth - 250, 230);
        // 6. AMAL QILISH MUDDATI
        g2d.setFont(fontBold);
        g2d.drawString(expiryDate, imageWidth - 250, 260);

        g2d.dispose();

        // Fayl nomini yaratish
        String fileName = generateFileName(ownerName, cardNumber);
        String fullPath = "uploads/admin-card/" + fileName + "." + "png";

        // Rasmni saqlash
        File output = new File(fullPath);
        ImageIO.write(image, "png", output);

        return fullPath;
    }

    private Font loadFont(String fontPath, float size) {
        try {
            // Resource-dan fontni yuklash
            File fontFile = new File("src/main/resources/" + fontPath);
            if (fontFile.exists()) {
                return Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(size);
            } else {
                // Agar font topilmasa, standart fontdan foydalanish
                return new Font("Arial", Font.BOLD, (int) size);
            }
        } catch (Exception e) {
            // Xatolik yuz bersa, standart font
            return new Font("Arial", Font.BOLD, (int) size);
        }
    }

    private String formatCardNumber(String cardNumber) {
        // 16 xonali raqamni 4-4 guruhlarga ajratish
        if (cardNumber.length() == 16) {
            return cardNumber.replaceAll("(.{4})", "$1 ").trim();
        }
        return cardNumber;
    }

    private String generateFileName(String ownerName, String cardNumber) {
        // Ismni toza qilish
        String cleanName = ownerName.toLowerCase()
                .replaceAll("[^a-zа-яёўқғәіөү]", "_")
                .replaceAll("_+", "_");

        // Vaqt stamp qo'shish
        String timeStamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // UUID qo'shish (noyoblik uchun)
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        return String.format("%s_%s_%s_%s",
                cleanName,
                cardNumber.substring(0, 4),
                timeStamp,
                uuid);
    }

}
