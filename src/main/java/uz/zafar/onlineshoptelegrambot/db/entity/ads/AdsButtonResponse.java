package uz.zafar.onlineshoptelegrambot.db.entity.ads;

public class AdsButtonResponse {
    private String url;
    private String viewText;

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

    public AdsButtonResponse(String url, String viewText) {
        this.url = url;
        this.viewText = viewText;
    }
}
