package uz.zafar.onlineshoptelegrambot.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.order.Basket;

@Entity
public class AboutWe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String uz;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String ru;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String en;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String cyr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUz() {
        return uz;
    }

    public void setUz(String uz) {
        this.uz = uz;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getCyr() {
        return cyr;
    }

    public void setCyr(String cyr) {
        this.cyr = cyr;
    }
}
