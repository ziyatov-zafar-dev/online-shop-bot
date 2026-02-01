package uz.zafar.onlineshoptelegrambot.db.entity.bot.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.enums.CustomerEventCode;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.DeliveryType;

import java.util.UUID;

@Entity
public class BotCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", name = "id")
    private UUID pkey;
    private Long chatId;
    private String username;
    private String firstName;
    private String lastName;
    private String telegramPhone;
    private Double latitude;
    private Double longitude;
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "event")
    private CustomerEventCode eventCode;
    @JsonIgnore
    private Integer eventCodeLocation = 0;
    @Enumerated(EnumType.STRING)
    private Language language;
    @JsonIgnore
    private UUID categoryId;
    @JsonIgnore
    @Column(name = "delivery_type_string")
    private String deliveryType;
    @JsonIgnore
    private String helperPhone;
    @JsonIgnore
    private UUID shopOrderId;

    public UUID getShopOrderId() {
        return shopOrderId;
    }

    public void setShopOrderId(UUID shopOrderId) {
        this.shopOrderId = shopOrderId;
    }

    public String getHelperPhone() {
        return helperPhone;
    }

    public void setHelperPhone(String helperPhone) {
        this.helperPhone = helperPhone;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public UUID getPkey() {
        return pkey;
    }

    public void setPkey(UUID pkey) {
        this.pkey = pkey;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
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

    public String getTelegramPhone() {
        return telegramPhone;
    }

    public void setTelegramPhone(String telegramPhone) {
        this.telegramPhone = telegramPhone;
    }

    public CustomerEventCode getEventCode() {
        return eventCode;
    }

    public void setEventCode(CustomerEventCode eventCode) {
        this.eventCode = eventCode;
    }

    public Integer getEventCodeLocation() {
        return eventCodeLocation != null ? eventCodeLocation : 0;
    }

    public void setEventCodeLocation(Integer eventCodeLocation) {
        this.eventCodeLocation = eventCodeLocation;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
