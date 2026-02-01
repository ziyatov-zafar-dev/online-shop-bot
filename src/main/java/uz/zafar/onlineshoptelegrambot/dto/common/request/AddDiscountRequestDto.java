package uz.zafar.onlineshoptelegrambot.dto.common.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.AppliedTo;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.DiscountType;

import java.math.BigDecimal;
import java.util.UUID;

public class AddDiscountRequestDto {
    private DiscountType type;
    private AppliedTo appliedTo;
    private BigDecimal value;
    private UUID productId;
    private Long subscriptionPlanId;

    public DiscountType getType() {
        return type;
    }

    public void setType(DiscountType type) {
        this.type = type;
    }

    public AppliedTo getAppliedTo() {
        return appliedTo;
    }

    public void setAppliedTo(AppliedTo appliedTo) {
        this.appliedTo = appliedTo;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }


    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Long getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(Long subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }
}
