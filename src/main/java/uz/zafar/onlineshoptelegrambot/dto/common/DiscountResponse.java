package uz.zafar.onlineshoptelegrambot.dto.common;

import uz.zafar.onlineshoptelegrambot.db.entity.enums.AppliedTo;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class DiscountResponse {
    private UUID id;
    private DiscountType type;
    private AppliedTo appliedTo;
    private BigDecimal value;
    private String createdAt;
    private String updatedAt;
    private UUID productId;
    private Long subscriptionPlanId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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



    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt.toString();
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt.toString();
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
