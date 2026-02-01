package uz.zafar.onlineshoptelegrambot.dto.product.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus;
import uz.zafar.onlineshoptelegrambot.dto.common.DiscountResponse;
import uz.zafar.onlineshoptelegrambot.dto.shop.response.ShopResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductResponse {
    private UUID id;
    private String nameUz;
    private String nameRu;
    private String nameEn;
    private String nameCyr;
    private String descriptionUz;
    private String descriptionCyr;
    private String descriptionRu;
    private String descriptionEn;
    private String sku;
    private ProductStatus status;
    private List<ProductTypeResponse> types;
    private UUID categoryId;
    private DiscountResponse discount;
    private ShopResponse shop;
    private String createdAt;
    private String updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNameUz() {
        return nameUz;
    }

    public void setNameUz(String nameUz) {
        this.nameUz = nameUz;
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

    public String getNameCyr() {
        return nameCyr;
    }

    public void setNameCyr(String nameCyr) {
        this.nameCyr = nameCyr;
    }

    public String getDescriptionUz() {
        return descriptionUz;
    }

    public void setDescriptionUz(String descriptionUz) {
        this.descriptionUz = descriptionUz;
    }

    public String getDescriptionCyr() {
        return descriptionCyr;
    }

    public void setDescriptionCyr(String descriptionCyr) {
        this.descriptionCyr = descriptionCyr;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu = descriptionRu;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public List<ProductTypeResponse> getTypes() {
        return types;
    }

    public void setTypes(List<ProductTypeResponse> types) {
        this.types = types;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public DiscountResponse getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountResponse discount) {
        this.discount = discount;
    }

    public ShopResponse getShop() {
        return shop;
    }

    public void setShop(ShopResponse shop) {
        this.shop = shop;
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

    public ProductResponse(UUID id, String nameUz, String nameRu, String nameEn, String nameCyr, String descriptionUz, String descriptionCyr, String descriptionRu, String descriptionEn, String sku, ProductStatus status, List<ProductTypeResponse> types, UUID categoryId, DiscountResponse discount, ShopResponse shop, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nameUz = nameUz;
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.nameCyr = nameCyr;
        this.descriptionUz = descriptionUz;
        this.descriptionCyr = descriptionCyr;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
        this.sku = sku;
        this.status = status;
        this.types = types;
        this.categoryId = categoryId;
        this.discount = discount;
        this.shop = shop;
        this.createdAt = createdAt.toString();
        this.updatedAt = updatedAt.toString();
    }

    public ProductResponse() {
    }
}
