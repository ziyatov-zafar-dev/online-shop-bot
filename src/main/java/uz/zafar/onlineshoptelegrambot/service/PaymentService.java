package uz.zafar.onlineshoptelegrambot.service;

import uz.zafar.onlineshoptelegrambot.db.entity.AdminCard;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.CardType;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.payment.PaymentResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    ResponseDto<List<PaymentResponse>> getAllPayments();

    ResponseDto<PaymentResponse> confirmPayment(String transactionID, BigDecimal amount);

    ResponseDto<PaymentResponse> cancelPayment(String transactionID, String description);

    ResponseDto<AdminCard> change(String number, String owner, String imageUrl, CardType cardType);
}
