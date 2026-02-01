package uz.zafar.onlineshoptelegrambot.db.entity.common;

import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.DiscountType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkey;
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private SubscriptionPlanType name;
    private boolean discount;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    public Long getPkey() {
        return pkey;
    }

    public void setPkey(Long pkey) {
        this.pkey = pkey;
    }

    public SubscriptionPlanType getName() {
        return name;
    }

    public void setName(SubscriptionPlanType name) {
        this.name = name;
    }

    public boolean isDiscount() {
        return discount;
    }

    public void setDiscount(boolean discount) {
        this.discount = discount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
