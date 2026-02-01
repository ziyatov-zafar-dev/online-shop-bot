package uz.zafar.onlineshoptelegrambot.db.entity.common;

import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.CardType;

@Entity
public class PlatformCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long pkey;
    @Enumerated(EnumType.STRING)
    private CardType cardType;
    private String cardNumber;
    private String owner;
    private String imgUrl;

    public Long getPkey() {
        return pkey;
    }

    public void setPkey(Long pkey) {
        this.pkey = pkey;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
