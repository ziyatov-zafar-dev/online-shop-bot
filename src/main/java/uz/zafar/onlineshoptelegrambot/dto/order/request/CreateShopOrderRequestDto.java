package uz.zafar.onlineshoptelegrambot.dto.order.request;

import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.DeliveryType;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreateShopOrderRequestDto {
    private UUID shopId;
    private String phone;
    private DeliveryType deliveryType;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private Long chatId;
    private Double latitude;
    private Double longitude;
    private List<CreateOrderItemRequestDto> items;
    private String address;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UUID getShopId() {
        return shopId;
    }

    public void setShopId(UUID shopId) {
        this.shopId = shopId;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public List<CreateOrderItemRequestDto> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemRequestDto> items) {
        this.items = items;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
