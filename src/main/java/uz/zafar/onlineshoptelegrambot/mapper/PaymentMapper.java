package uz.zafar.onlineshoptelegrambot.mapper;

import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.payment.Payment;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SubscriptionPlanRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.UserRepository;
import uz.zafar.onlineshoptelegrambot.dto.payment.PaymentResponse;
import uz.zafar.onlineshoptelegrambot.dto.seller.SellerResponse;

import java.util.List;

@Component
public class PaymentMapper {
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public PaymentMapper(SellerRepository sellerRepository, UserRepository userRepository, SubscriptionPlanRepository subscriptionPlanRepository) {
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    public List<PaymentResponse> toList(List<Payment> p) {
        return p.stream()
                .map(this::toResponse)
                .toList();
    }

    public PaymentResponse toResponse(Payment p) {
        Seller seller = sellerRepository.findById(p.getSeller().getPkey()).orElse(null);
        return new PaymentResponse(
                p.getPkey(),
                p.getAmount(),
                p.getStatus(),
                p.getPaymentType(),
                p.getPaymentPurpose(),
                (seller == null ? null : seller.getPkey()),
                p.getTransactionId(),
                p.getDescription(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                toResponse(p.getSeller()),
                p.getPaymentImage(),
                p.getImageSize(),
                p.getImageName()
        );
    }

    private SellerResponse toResponse(Seller seller) {
        if (seller == null) {
            return null;
        }

        SellerResponse res = new SellerResponse();

        res.setId(seller.getPkey());
        res.setGender(seller.getGender());
        res.setPhone(seller.getPhone());

        res.setCardNumber(seller.getCardNumber());
        res.setCardType(seller.getCardType());
        res.setCardOwner(seller.getCardOwner());

        res.setCardImageUrl(seller.getCardImageUrl());
        res.setCardImageName(seller.getCardImageName());
        res.setCardImageSize(seller.getCardImageSize());
        res.setCardAddedTime(seller.getCardAddedTime());

        res.setBalance(seller.getBalance());
        res.setStatus(seller.getStatus());

        res.setPlanExpiresAt(seller.getPlanExpiresAt());
        res.setSubscriptionPlan(
                subscriptionPlanRepository.findById(seller.getSubscriptionPlanId()).orElse(null)
        );
        res.setUser(userRepository.findById(seller.getUserid()).orElse(null));
        return res;

    }
}
