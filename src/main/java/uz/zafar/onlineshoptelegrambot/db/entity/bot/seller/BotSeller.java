package uz.zafar.onlineshoptelegrambot.db.entity.bot.seller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.enums.SellerEventCode;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class BotSeller {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", name = "id")
    private UUID pkey;
    @Column(nullable = false, unique = true)
    private Long chatId;
    private String username;
    private String firstName;
    private String lastName;
    private String telegramPhone;
    @Column(nullable = true)
    private UUID userid;
    private String ip;
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "seller_event_code_1")
    private SellerEventCode eventCode;
    @Enumerated(EnumType.STRING)
    private Language language;
    @JsonIgnore
    private Integer eventCodeLocation;
    private LocalDateTime connectingToSellerAt;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public UUID getPkey() {
        return pkey;
    }

    public LocalDateTime getConnectingToSellerAt() {
        return connectingToSellerAt;
    }

    public void setConnectingToSellerAt(LocalDateTime connectingToSellerAt) {
        this.connectingToSellerAt = connectingToSellerAt;
    }

    public void setPkey(UUID pkey) {
        this.pkey = pkey;
    }

    public SellerEventCode getEventCode() {
        return eventCode;
    }

    public void setEventCode(SellerEventCode eventCode) {
        this.eventCode = eventCode;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UUID getUserid() {
        return userid;
    }

    public void setUserid(UUID userid) {
        this.userid = userid;
    }

    public String getTelegramPhone() {
        return telegramPhone;
    }

    public void setTelegramPhone(String telegramPhone) {
        this.telegramPhone = telegramPhone;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {

        this.language = language;
    }

    public Integer getEventCodeLocation() {
        return eventCodeLocation;
    }

    public void setEventCodeLocation(Integer eventCodeLocation) {
        this.eventCodeLocation = eventCodeLocation;
    }
}
