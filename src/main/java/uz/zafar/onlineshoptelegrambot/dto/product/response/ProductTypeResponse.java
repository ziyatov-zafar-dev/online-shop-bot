package uz.zafar.onlineshoptelegrambot.dto.product.response;

import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductTypeResponse {
    private UUID pkey;
    private String nameUz;
    private String nameCyr;
    private String nameRu;
    private String nameEn;
    private BigDecimal price;
    private UUID productId;
    private List<ProductTypeImageResponse> images;
    private String createdAt;
    private String updatedAt;
    private Integer stock;
    public UUID getPkey() {
        return pkey;
    }

    public void setPkey(UUID pkey) {
        this.pkey = pkey;
    }

    public String getNameUz() {
        return nameUz;
    }

    public void setNameUz(String nameUz) {
        this.nameUz = nameUz;
    }

    public String getNameCyr() {
        return nameCyr;
    }

    public void setNameCyr(String nameCyr) {
        this.nameCyr = nameCyr;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public List<ProductTypeImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ProductTypeImageResponse> images) {
        this.images = images;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt.toString();
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt.toString();
    }


    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public ProductTypeResponse() {
    }

    public ProductTypeResponse(UUID pkey, String nameUz, String nameCyr, String nameRu, String nameEn, BigDecimal price, UUID productId, List<ProductTypeImageResponse> images, LocalDateTime createdAt, LocalDateTime updatedAt, Integer stock) {
        this.pkey = pkey;
        this.nameUz = nameUz;
        this.nameCyr = nameCyr;
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.price = price;
        this.productId = productId;
        this.images = images;
        this.createdAt = createdAt.toString();
        this.updatedAt = updatedAt.toString();
        this.stock = stock;
    }
}
