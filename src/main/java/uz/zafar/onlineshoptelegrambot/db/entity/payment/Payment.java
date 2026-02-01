package uz.zafar.onlineshoptelegrambot.db.entity.payment;

import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.PaymentPurpose;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.PaymentStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.PaymentType;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "payments")
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID pkey;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus status;
    private BigDecimal amount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;
    private String transactionId;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String paymentImage;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private Long imageSize;
    private String imageName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private PaymentPurpose paymentPurpose;

    public UUID getPkey() {
        return pkey;
    }

    public void setPkey(UUID pkey) {
        this.pkey = pkey;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPaymentImage() {
        return paymentImage;
    }

    public void setPaymentImage(String paymentImage) {
        this.paymentImage = paymentImage;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Long getImageSize() {
        return imageSize;
    }

    public void setImageSize(Long imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public PaymentPurpose getPaymentPurpose() {
        return paymentPurpose;
    }

    public void setPaymentPurpose(PaymentPurpose paymentPurpose) {
        this.paymentPurpose = paymentPurpose;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
