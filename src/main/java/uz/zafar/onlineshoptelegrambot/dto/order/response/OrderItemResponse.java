package uz.zafar.onlineshoptelegrambot.dto.order.response;

import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductResponse;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductTypeResponse;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemResponse {
    private UUID id;
    private UUID shopOrderId;
    private ProductTypeResponse productType;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private ProductResponse product ;

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getShopOrderId() {
        return shopOrderId;
    }

    public void setShopOrderId(UUID shopOrderId) {
        this.shopOrderId = shopOrderId;
    }

    public ProductTypeResponse getProductType() {
        return productType;
    }

    public void setProductType(ProductTypeResponse productType) {
        this.productType = productType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public OrderItemResponse(UUID id, UUID shopOrderId, ProductTypeResponse productType, Integer quantity, BigDecimal price, BigDecimal totalPrice) {
        this.id = id;
        this.shopOrderId = shopOrderId;
        this.productType = productType;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {

        this.totalPrice = totalPrice;
    }
}

