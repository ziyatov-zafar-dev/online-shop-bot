package uz.zafar.onlineshoptelegrambot.dto.order.request;

import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateOrderItemRequestDto {
    private UUID productTypeId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;

    public UUID getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(UUID productTypeId) {
        this.productTypeId = productTypeId;
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

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
