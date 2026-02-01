package uz.zafar.onlineshoptelegrambot.dto.order.response;

import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.DeliveryType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.dto.shop.response.ShopResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ShopOrderResponse {
    private UUID id;
    private ShopResponse shop;
    private Seller seller;
    private String phone;
    private ShopOrderStatusInfo status;
    private DeliveryType deliveryType;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BotCustomer client;
    private BigDecimal deliveryPrice;
    private Double latitude;
    private Double longitude;
    private String address;
    private List<OrderItemResponse> items;

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public void setStatus(ShopOrderStatusInfo status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ShopResponse getShop() {
        return shop;
    }

    public void setShop(ShopResponse shop) {
        this.shop = shop;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public ShopOrderStatusInfo getStatus() {
        return this.status;
    }

    public void setStatus(ShopOrderStatus status) {
        this.status = new ShopOrderStatusInfo(status, deliveryType);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BotCustomer getClient() {
        return client;
    }

    public void setClient(BotCustomer client) {
        this.client = client;
    }

    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(BigDecimal deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
