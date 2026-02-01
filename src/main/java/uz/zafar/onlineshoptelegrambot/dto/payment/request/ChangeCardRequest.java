package uz.zafar.onlineshoptelegrambot.dto.payment.request;

import uz.zafar.onlineshoptelegrambot.db.entity.enums.CardType;

public class ChangeCardRequest {
    private String number;
    private CardType cardType ;
    private String owner;
    private String imageUrl;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
