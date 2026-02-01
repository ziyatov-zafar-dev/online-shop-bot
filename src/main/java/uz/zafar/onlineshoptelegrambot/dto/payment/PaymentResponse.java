package uz.zafar.onlineshoptelegrambot.dto.payment;

import uz.zafar.onlineshoptelegrambot.db.entity.enums.PaymentPurpose;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.PaymentStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.PaymentType;
import uz.zafar.onlineshoptelegrambot.db.entity.payment.Payment;
import uz.zafar.onlineshoptelegrambot.dto.seller.SellerResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponse {
    private UUID id;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentType paymentType;
    private PaymentPurpose purpose;
    private Long sellerId;

    private String transactionId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SellerResponse seller;
    private String paymentImage;
    private Long imageSize;
    private String imageName;
    // Getters & Setters

    public UUID getId() {
        return id;
    }

    public SellerResponse getSeller() {
        return seller;
    }

    public void setSeller(SellerResponse seller) {
        this.seller = seller;
    }

    public String getPaymentImage() {
        return paymentImage;
    }

    public void setPaymentImage(String paymentImage) {
        this.paymentImage = paymentImage;
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

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }



    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public PaymentPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(PaymentPurpose purpose) {
        this.purpose = purpose;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
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

    public PaymentResponse(UUID id, BigDecimal amount,
                           PaymentStatus status, PaymentType paymentType, PaymentPurpose purpose,
                           Long sellerId, String transactionId, String description, LocalDateTime createdAt, LocalDateTime updatedAt,
                           SellerResponse seller,
                           String paymentImage,
                           Long imageSize,
                           String imageName) {
        this.id = id;
        this.amount = amount;

        this.status = status;
        this.paymentType = paymentType;
        this.purpose = purpose;
        this.sellerId = sellerId;
        this.transactionId = transactionId;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.seller = seller;
        this.paymentImage = paymentImage;
        this.imageSize = imageSize;
        this.imageName = imageName;
    }



}
