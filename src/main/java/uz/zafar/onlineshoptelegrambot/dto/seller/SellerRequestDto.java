package uz.zafar.onlineshoptelegrambot.dto.seller;

import uz.zafar.onlineshoptelegrambot.db.entity.enums.CardType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Gender;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;

public class SellerRequestDto {
    private Gender gender;
    private String phone;
    private String cardNumber;
    private CardType cardType;
    private String cardImageUrl;
    private Long cardImageSize;
    private String cardImageName;
    private String cardOwner;

    public Gender getGender() {
        return gender;
    }

    public String getCardOwner() {
        return cardOwner;
    }

    public void setCardOwner(String cardOwner) {
        this.cardOwner = cardOwner;
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

    public void setCardImageName(String cardImageName) {
        this.cardImageName = cardImageName;
    }
}
