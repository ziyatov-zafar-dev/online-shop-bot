package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.common.Discount;
import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.AppliedTo;
import uz.zafar.onlineshoptelegrambot.db.repositories.DiscountRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SubscriptionPlanRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.common.request.AddDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.discount.request.EditDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.mapper.DiscountMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("api/admin/subsription/plan")
public class PlatformSubscriptionRestController {
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    public PlatformSubscriptionRestController(SubscriptionPlanRepository subscriptionPlanRepository, DiscountRepository discountRepository, DiscountMapper discountMapper) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.discountRepository = discountRepository;
        this.discountMapper = discountMapper;
    }

    @GetMapping("list")
    public ResponseDto<?> subscriptionPlans() {
        return ResponseDto.success(subscriptionPlanRepository.findAll(Sort.by(Sort.Direction.ASC, "pkey")));
    }

    @PutMapping("update/plan/{subscriptionPlanId}")
    public ResponseDto<?> update(
            @PathVariable("subscriptionPlanId") Long subscriptionPlanId, @RequestBody UpdatePrice updatePrice
    ) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(subscriptionPlanId).orElse(null);
        if (subscriptionPlan == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SUBSCRIPTION_PLAN);
        subscriptionPlan.setPrice(updatePrice.getPrice());
        subscriptionPlan.setUpdatedAt(LocalDateTime.now());
        subscriptionPlanRepository.save(subscriptionPlan);
        return ResponseDto.success();
    }


    @GetMapping("discount/{planId}")
    public ResponseDto<?> discount(@PathVariable("planId") Long subscriptionPlanId) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(subscriptionPlanId).orElse(null);
        if (subscriptionPlan == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SUBSCRIPTION_PLAN);
        Discount discount = discountRepository.getSubscriptionPlan(subscriptionPlanId).orElse(null);
        if (discount == null) return ResponseDto.error(ErrorCode.NOT_FOUND_DISCOUNT);
        return ResponseDto.success(discountMapper.toResponse(discount));
    }

    @PostMapping("discount/{planId}")
    @Transactional
    public ResponseDto<?> addDiscount(@PathVariable("planId") Long subscriptionPlanId, @RequestBody AddDiscountRequestDto req) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(subscriptionPlanId).orElse(null);
        if (subscriptionPlan == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SUBSCRIPTION_PLAN);
        Discount discount;
        discount = discountRepository.getSubscriptionPlan(subscriptionPlanId).orElse(null);
        if (discount == null) {
            discount = new Discount();
        }
        discount.setType(req.getType());
        discount.setAppliedTo(AppliedTo.PLATFORM_PLAN);
        discount.setValue(req.getValue());
        discount.setSubscriptionPlanId(subscriptionPlanId);
        discount.setCreatedAt(LocalDateTime.now());
        discount.setUpdatedAt(LocalDateTime.now());
        subscriptionPlan.setDiscount(true);
        subscriptionPlanRepository.save(subscriptionPlan);
        Discount save = discountRepository.save(discount);
        return ResponseDto.success();
    }

    @PutMapping("edit/discount/{planId}")
    @Transactional
    public ResponseDto<?> editDiscount(@PathVariable("planId") Long subscriptionPlanId, @RequestBody EditDiscountRequestDto req) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(subscriptionPlanId).orElse(null);
        if (subscriptionPlan == null)
            return ResponseDto.error(ErrorCode.NOT_FOUND_SUBSCRIPTION_PLAN);
        if (!subscriptionPlan.isDiscount())
            return ResponseDto.error(ErrorCode.NOT_FOUND_DISCOUNT);
        Discount discount;
        discount = discountRepository.getSubscriptionPlan(subscriptionPlanId).orElse(null);
        if (discount == null) return ResponseDto.error(ErrorCode.NOT_FOUND_DISCOUNT);
        discount.setType(req.getType());
        discount.setValue(req.getValue());
        discountRepository.save(discount);
        return ResponseDto.success();
    }

    @DeleteMapping("remove/discount/{planId}")
    @Transactional
    public ResponseDto<?> removeDiscount(@PathVariable("planId") Long subscriptionPlanId) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(subscriptionPlanId).orElse(null);
        if (subscriptionPlan == null)
            return ResponseDto.error(ErrorCode.NOT_FOUND_SUBSCRIPTION_PLAN);
        if (!subscriptionPlan.isDiscount())
            return ResponseDto.error(ErrorCode.NOT_FOUND_DISCOUNT);
        Discount discount;
        discount = discountRepository.getSubscriptionPlan(subscriptionPlanId).orElse(null);
        if (discount == null) return ResponseDto.error(ErrorCode.NOT_FOUND_DISCOUNT);
        discountRepository.delete(discount);
        subscriptionPlan.setDiscount(false);
        subscriptionPlanRepository.save(subscriptionPlan);
        return ResponseDto.success();
    }

    public static class UpdatePrice {
        private BigDecimal price;

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
