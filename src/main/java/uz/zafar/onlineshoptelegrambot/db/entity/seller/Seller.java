package uz.zafar.onlineshoptelegrambot.db.entity.seller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.CardType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Gender;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SellerStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long pkey;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String phone;
    private String cardNumber;
    @Enumerated(EnumType.STRING)
    private CardType cardType;
    @Column(columnDefinition = "TEXT", name = "image_url")
    private String cardImageUrl;
    private String cardOwner;
    private Long cardImageSize;
    private String cardImageName;
    private LocalDateTime cardAddedTime;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private SellerStatus status;
    @Column(unique = true, nullable = true)
    @JsonIgnore
    private UUID userid;
    private LocalDateTime planExpiresAt;
    private Long subscriptionPlanId;

    public Long getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(Long subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public Long getPkey() {
        return pkey;
    }

    public void setPkey(Long pkey) {
        this.pkey = pkey;
    }

    public LocalDateTime getCardAddedTime() {
        return cardAddedTime;
    }

    public void setCardAddedTime(LocalDateTime cardAddedTime) {
        this.cardAddedTime = cardAddedTime;
    }

    public Gender getGender() {
        return gender;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public Long getCardImageSize() {
        return cardImageSize;
    }

    public void setCardImageSize(Long cardImageSize) {
        this.cardImageSize = cardImageSize;
    }

    public String getCardImageName() {
        return cardImageName;
    }

    public LocalDateTime getPlanExpiresAt() {
        return planExpiresAt;
    }

    public void setPlanExpiresAt(LocalDateTime planExpiresAt) {
        this.planExpiresAt = planExpiresAt;
    }

    public void setCardImageName(String cardImageName) {
        this.cardImageName = cardImageName;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public SellerStatus getStatus() {
        return status;
    }

    public void setStatus(SellerStatus status) {
        this.status = status;
    }

    public UUID getUserid() {
        return userid;
    }

    public void setUserid(UUID userid) {
        this.userid = userid;
    }

    public String getCardOwner() {
        return cardOwner;
    }

    public void setCardOwner(String cardOwner) {
        this.cardOwner = cardOwner;
    }
}
