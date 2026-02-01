package uz.zafar.onlineshoptelegrambot.db.entity.ads;

import jakarta.persistence.*;

import java.util.UUID;

@Entity

public class AdsButton {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", name = "id", updatable = false, nullable = false)
    private UUID pkey;
    private String url;
    private String viewText;
    private UUID adsId;

    public UUID getAdsId() {
        return adsId;
    }

    public void setAdsId(UUID adsId) {
        this.adsId = adsId;
    }

    public UUID getPkey() {
        return pkey;
    }

    public void setPkey(UUID pkey) {
        this.pkey = pkey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getViewText() {
        return viewText;
    }

    public void setViewText(String viewText) {
        this.viewText = viewText;
    }
}
