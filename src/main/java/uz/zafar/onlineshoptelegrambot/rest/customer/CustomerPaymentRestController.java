package uz.zafar.onlineshoptelegrambot.rest.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.repositories.PaymentRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.payment.PaymentResponse;
import uz.zafar.onlineshoptelegrambot.mapper.PaymentMapper;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/customer/payment")
public class CustomerPaymentRestController {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BotSellerRepository botSellerRepository;
    private final SellerRepository sellerRepository;

    public CustomerPaymentRestController(PaymentRepository paymentRepository, PaymentMapper paymentMapper, BotSellerRepository botSellerRepository, SellerRepository sellerRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.botSellerRepository = botSellerRepository;
        this.sellerRepository = sellerRepository;
    }

    @GetMapping("list/{chatId}")
    public ResponseDto<?> myPayments(@PathVariable("chatId") Long chatId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        List<PaymentResponse> list = paymentMapper.toList(paymentRepository.myPayments(seller.getPkey()));
        for (PaymentResponse paymentResponse : list) {
            if (paymentResponse.getAmount() == null) paymentResponse.setAmount(BigDecimal.ZERO);
        }
        return ResponseDto.success(list);
    }
}
