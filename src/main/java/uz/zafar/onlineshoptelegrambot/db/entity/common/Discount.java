package uz.zafar.onlineshoptelegrambot.db.entity.common;

import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.AppliedTo;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", name = "id")
    private UUID pkey;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppliedTo appliedTo;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal value;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private Long subscriptionPlanId;

    public Discount() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getPkey() {
        return pkey;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product productId) {
        this.product = productId;
    }

    public Long getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(Long subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public void setPkey(UUID pkey) {
        this.pkey = pkey;
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
