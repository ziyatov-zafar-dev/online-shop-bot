package uz.zafar.onlineshoptelegrambot.dto.seller;

import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.CardType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Gender;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SellerStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SellerResponse {
    private Gender gender;
    private Long id;
    private String phone;
    private String cardNumber;
    private CardType cardType;

    private String cardImageUrl;
    private String cardOwner;
    private Long cardImageSize;
    private String cardImageName;
    private LocalDateTime cardAddedTime;
    private BigDecimal balance;
    private SellerStatus status;
    private SubscriptionPlan subscriptionPlan ;
    private LocalDateTime planExpiresAt;
    private User user;

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getCardOwner() {
        return cardOwner;
    }

    public void setCardOwner(String cardOwner) {
        this.cardOwner = cardOwner;
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

    public void setCardImageName(String cardImageName) {
        this.cardImageName = cardImageName;
    }

    public LocalDateTime getCardAddedTime() {
        return cardAddedTime;
    }

    public void setCardAddedTime(LocalDateTime cardAddedTime) {
        this.cardAddedTime = cardAddedTime;
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

    public LocalDateTime getPlanExpiresAt() {
        return planExpiresAt;
    }

    public void setPlanExpiresAt(LocalDateTime planExpiresAt) {
        this.planExpiresAt = planExpiresAt;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public SellerResponse() {
    }

    public SellerResponse(Gender gender, Long id, String phone, String cardNumber, CardType cardType, String cardImageUrl, String cardOwner, Long cardImageSize, String cardImageName, LocalDateTime cardAddedTime, BigDecimal balance, SellerStatus status, SubscriptionPlan subscriptionPlan, LocalDateTime planExpiresAt, User user) {
        this.gender = gender;
        this.id = id;
        this.phone = phone;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.cardImageUrl = cardImageUrl;
        this.cardOwner = cardOwner;
        this.cardImageSize = cardImageSize;
        this.cardImageName = cardImageName;
        this.cardAddedTime = cardAddedTime;
        this.balance = balance;
        this.status = status;
        this.subscriptionPlan = subscriptionPlan;
        this.planExpiresAt = planExpiresAt;
        this.user = user;
    }
}
