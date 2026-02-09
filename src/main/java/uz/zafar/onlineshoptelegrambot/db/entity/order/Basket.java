package uz.zafar.onlineshoptelegrambot.db.entity.order;

import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.BasketType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer_baskets1")
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", name = "id")
    private UUID pkey;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private BotCustomer customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id", nullable = false)
    private ProductType productType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BasketType type;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private BigDecimal totalPrice;
    private LocalDateTime created;

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public UUID getPkey() {
        return this.pkey;
    }

    public void setPkey(UUID pkey) {
        this.pkey = pkey;
    }

    public BotCustomer getCustomer() {
        return this.customer;
    }

    public void setCustomer(BotCustomer customer) {
        this.customer = customer;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public BasketType getType() {
        return type;
    }

    public void setType(BasketType type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Basket() {
        this.created = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    void calculateTotal() {
        this.totalPrice = price.multiply(BigDecimal.valueOf(quantity));
    }
}
