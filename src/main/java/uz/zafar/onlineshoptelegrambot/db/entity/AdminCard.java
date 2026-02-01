package uz.zafar.onlineshoptelegrambot.db.entity;

import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.CardType;

@Entity
public class AdminCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String number;
    private String owner;
    private String imgUrl;
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    public AdminCard(Integer id, String number, String owner, String imgUrl, CardType cardType) {
        this.id = id;
        this.number = number;
        this.owner = owner;
        this.imgUrl = imgUrl;
        this.cardType = cardType;
    }

    public AdminCard() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
}
