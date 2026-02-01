package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SellerStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SubscriptionPlanRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.UserRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.seller.SellerResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/seller")
public class AdminSellerRestController {
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public AdminSellerRestController(SellerRepository sellerRepository, UserRepository userRepository, SubscriptionPlanRepository subscriptionPlanRepository) {
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }


    @PutMapping("confirm-seller/{sellerId}")
    @Transactional
    public ResponseDto<?> confirmSeller(@PathVariable("sellerId") Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        seller.setStatus(SellerStatus.APPROVED);
        seller.setSubscriptionPlanId(subscriptionPlanRepository.findByName(SubscriptionPlanType.TRIAL).get().getPkey());
        seller.setPlanExpiresAt(LocalDateTime.now().plusMonths(SubscriptionPlanType.TRIAL.getMonth()));
        sellerRepository.save(seller);
        return ResponseDto.success();
    }

    @PutMapping("rejected-seller/{sellerId}")
    @Transactional
    public ResponseDto<?> rejectedSeller(@PathVariable("sellerId") Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        seller.setStatus(SellerStatus.REJECTED);
        sellerRepository.save(seller);
        return ResponseDto.success();
    }

    @GetMapping("/all-sellers")
    public ResponseDto<?> allSellers() {
        try {
            List<SellerResponse> list = sellerRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .sorted(Comparator.comparing(
                            sr -> sr.getUser().getCreatedAt(),
                            Comparator.reverseOrder()
                    ))
                    .toList();
            return ResponseDto.success(list);
        } catch (Exception e) {
            return new ResponseDto<>(
                    false, new ResponseDto.Message(e.getMessage(), e.getMessage(), e.getMessage(), e.getMessage()), null, null
            );
        }
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

        Optional<SubscriptionPlan> byName = subscriptionPlanRepository.findByName(SubscriptionPlanType.TRIAL);
        res.setPlanExpiresAt(seller.getPlanExpiresAt());
        res.setSubscriptionPlan(
                seller.getSubscriptionPlanId() == null ? byName.orElse(null) : subscriptionPlanRepository.findById(seller.getSubscriptionPlanId()).orElse(null)
        );
        res.setUser(userRepository.findById(seller.getUserid()).orElse(null));
        return res;

    }
}
