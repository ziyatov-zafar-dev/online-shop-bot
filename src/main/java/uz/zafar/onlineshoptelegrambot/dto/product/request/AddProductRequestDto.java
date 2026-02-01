package uz.zafar.onlineshoptelegrambot.dto.product.request;

import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.dto.common.request.AddDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductTypeResponse;

import java.util.List;
import java.util.UUID;

public class AddProductRequestDto {
    private String nameUz;
    private String nameRu;
    private String nameEn;
    private String nameCyr;
    private String descriptionUz;
    private String descriptionCyr;
    private String descriptionRu;
    private String descriptionEn;
    private UUID categoryId;
    private List<AddProductTypeRequestDto> productTypes;
    private AddDiscountRequestDto discount;
    private Boolean hasDiscount;
    private UUID shopId;

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

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public List<AddProductTypeRequestDto> getProductTypes() {
        return productTypes;
    }

    public void setProductTypes(List<AddProductTypeRequestDto> productTypes) {
        this.productTypes = productTypes;
    }

    public AddDiscountRequestDto getDiscount() {
        return discount;
    }

    public void setDiscount(AddDiscountRequestDto discount) {
        this.discount = discount;
    }

    public Boolean getHasDiscount() {
        return hasDiscount;
    }

    public void setHasDiscount(Boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    public UUID getShopId() {
        return shopId;
    }

    public void setShopId(UUID shopId) {
        this.shopId = shopId;
    }
}
