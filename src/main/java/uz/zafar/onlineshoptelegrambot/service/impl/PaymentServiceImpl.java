package uz.zafar.onlineshoptelegrambot.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;
import uz.zafar.onlineshoptelegrambot.db.entity.AdminCard;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.CardType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.PaymentStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.payment.Payment;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.repositories.AdminCardRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.PaymentRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.payment.PaymentResponse;
import uz.zafar.onlineshoptelegrambot.mapper.PaymentMapper;
import uz.zafar.onlineshoptelegrambot.service.PaymentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final SellerRepository sellerRepository;
    private final TelegramBot sellerBot;
    private final AdminCardRepository adminCardRepository;
    private final BotSellerRepository botSellerRepository;


    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentMapper paymentMapper,
                              SellerRepository sellerRepository,
                              @Qualifier("seller") TelegramBot sellerBot, AdminCardRepository adminCardRepository, BotSellerRepository botSellerRepository
    ) {
        this.paymentRepository = paymentRepository;

        this.paymentMapper = paymentMapper;
        this.sellerRepository = sellerRepository;
        this.sellerBot = sellerBot;
        this.adminCardRepository = adminCardRepository;
        this.botSellerRepository = botSellerRepository;
    }

    @Override
    public ResponseDto<List<PaymentResponse>> getAllPayments() {
        return ResponseDto.success(
                paymentRepository.findAll().stream()
                        .map(paymentMapper::toResponse)
                        .toList()
        );
    }

    @Override
    public ResponseDto<PaymentResponse> confirmPayment(String transactionID, BigDecimal amount) {
        Payment payment = paymentRepository.findByTransactionId(transactionID).orElse(null);
        if (payment == null) {
            return ResponseDto.error(ErrorCode.ERROR);
        }
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.CONFIRM);
        payment.setDescription("Ok");
        payment.setUpdatedAt(LocalDateTime.now());
        Seller seller = payment.getSeller();
        seller.setBalance(seller.getBalance().add(amount));
        sellerRepository.save(seller);
        for (BotSeller botSeller : botSellerRepository.findAllByUserId(seller.getUserid())) {
            sellerBot.sendPhoto(botSeller.getChatId(), payment.getPaymentImage(), confirmPaymentInfo(
                    botSeller.getLanguage(),
                    payment.getTransactionId(),
                    amount, seller.getBalance()
            ));
        }
        return ResponseDto.success(paymentMapper.toResponse(paymentRepository.save(payment)));
    }

    private String confirmPaymentInfo(Language language, String transactionId, BigDecimal addedAmount, BigDecimal totalAmount) {
        String formattedAdded = addedAmount.setScale(2, RoundingMode.HALF_UP).toPlainString();
        String formattedTotal = totalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString();

        return switch (language) {
            case UZBEK -> String.format("""
                    ✅ <b>To'lov muvaffaqiyatli tasdiqlandi!</b>
                    
                    🔹 <b>Tranzaksiya ID:</b> <code>%s</code>
                    💰 <b>Qo'shilgan summa:</b> <code>%s UZS</code>
                    🏦 <b>Jami balans:</b> <code>%s UZS</code>
                    
                    <i>Mablag' hisobingizga qo'shildi</i> 💳
                    """, transactionId, formattedAdded, formattedTotal);

            case CYRILLIC -> String.format("""
                    ✅ <b>Тўлов муваффақиятли тасдиқланди!</b>
                    
                    🔹 <b>Транзакция ID:</b> <code>%s</code>
                    💰 <b>Қўшилган сумма:</b> <code>%s UZS</code>
                    🏦 <b>Жами баланс:</b> <code>%s UZS</code>
                    
                    <i>Маблағ ҳисобингизга қўшилди</i> 💳
                    """, transactionId, formattedAdded, formattedTotal);

            case RUSSIAN -> String.format("""
                    ✅ <b>Платёж успешно подтверждён!</b>
                    
                    🔹 <b>Транзакция ID:</b> <code>%s</code>
                    💰 <b>Добавленная сумма:</b> <code>%s UZS</code>
                    🏦 <b>Общий баланс:</b> <code>%s UZS</code>
                    
                    <i>Средства зачислены на ваш счёт</i> 💳
                    """, transactionId, formattedAdded, formattedTotal);

            case ENGLISH -> String.format("""
                    ✅ <b>Payment confirmed successfully!</b>
                    
                    🔹 <b>Transaction ID:</b> <code>%s</code>
                    💰 <b>Added amount:</b> <code>%s UZS</code>
                    🏦 <b>Total balance:</b> <code>%s UZS</code>
                    
                    <i>Funds have been credited to your account</i> 💳
                    """, transactionId, formattedAdded, formattedTotal);
        };
    }


    @Override
    public ResponseDto<PaymentResponse> cancelPayment(String transactionID, String description) {
        Payment payment = paymentRepository.findByTransactionId(transactionID).orElse(null);
        if (payment == null) {
            return ResponseDto.error(ErrorCode.ERROR);
        }
        payment.setAmount(BigDecimal.ZERO);
        payment.setStatus(PaymentStatus.CANCEL);
        payment.setDescription(description);
        payment.setUpdatedAt(LocalDateTime.now());
        for (BotSeller botSeller : botSellerRepository.findAllByUserId(payment.getSeller().getUserid())) {
            sellerBot.sendPhoto(botSeller.getChatId(), payment.getPaymentImage(), cancelPaymentInfo(
                    botSeller.getLanguage(),
                    payment.getTransactionId(),
                    description
            ));
        }
        return ResponseDto.success(paymentMapper.toResponse(paymentRepository.save(payment)));
    }

    private String cancelPaymentInfo(Language language, String transactionId, String because) {
        return switch (language) {
            case UZBEK -> """
                    ❌ <b>To'lov bekor qilindi!</b>
                    
                    🆔 <b>Tranzaksiya ID:</b> <code>%s</code>
                    📝 <b>Sabab:</b> %s
                    """.formatted(transactionId, because);

            case CYRILLIC -> """
                    ❌ <b>Тўлов бекор қилинди!</b>
                    
                    🆔 <b>Транзакция ID:</b> <code>%s</code>
                    📝 <b>Сабаб:</b> %s
                    """.formatted(transactionId, because);

            case RUSSIAN -> """
                    ❌ <b>Платёж отменён!</b>
                    
                    🆔 <b>Транзакция ID:</b> <code>%s</code>
                    📝 <b>Причина:</b> %s
                    """.formatted(transactionId, because);

            case ENGLISH -> """
                    ❌ <b>Payment cancelled!</b>
                    
                    🆔 <b>Transaction ID:</b> <code>%s</code>
                    📝 <b>Reason:</b> %s
                    """.formatted(transactionId, because);
        };
    }

    @Override
    public ResponseDto<AdminCard> change(String number, String owner, String imageUrl, CardType cardType) {
        AdminCard adminCard = adminCardRepository.findAll().isEmpty() ? new AdminCard() : adminCardRepository.findAll().get(0);
        adminCard.setNumber(number);
        adminCard.setCardType(cardType);
        adminCard.setOwner(owner);
        adminCard.setImgUrl(imageUrl);
        return ResponseDto.success(adminCardRepository.save(adminCard));
    }

}
