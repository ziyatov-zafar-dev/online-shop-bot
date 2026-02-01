package uz.zafar.onlineshoptelegrambot.dto.bot.kyb;

public class Button {
    private String text;
    private ButtonType type;
    private String data;
    private String url;
    private String webappUrl;

    public String getText() {
        return text;
    }
    public Button(String text, ButtonType type) {
        this.text = text;
        this.type = type;
    }

    public Button(String text, ButtonType type, String data, String url, String webappUrl) {
        this.text = text;
        this.type = type;
        this.data = data;
        this.url = url;
        this.webappUrl = webappUrl;
    }

    public Button(String text, String webbAppUrl) {
        this.text = text;
        this.webappUrl = webbAppUrl;
        this.type = ButtonType.WEBAPP;
    }

    public Button() {
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebappUrl() {
        return this.webappUrl;
    }

    public void setWebappUrl(String webappUrl) {
        this.webappUrl = webappUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ButtonType getType() {
        return this.type;
    }

    public void setType(ButtonType type) {
        this.type = type;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
