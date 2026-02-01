package uz.zafar.onlineshoptelegrambot.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class CustomerCategoryResponse {
    private UUID id;
    private String nameUz;
    private String nameCyr;
    private String nameEn;
    private String nameRu;
    private String descriptionUz;
    private String descriptionCyr;
    private String descriptionRu;
    private String descriptionEn;
    private Integer orderNumber;
    private String imageUrl;
    private UUID parentId;
    private String createdAt;
    private String updatedAt;
    private boolean hasChildren;

    public CustomerCategoryResponse(UUID id, String nameUz, String nameCyr, String nameEn, String nameRu, String descriptionUz, String descriptionCyr, String descriptionRu, String descriptionEn, Integer orderNumber, String imageUrl, UUID parentId, String createdAt, String updatedAt, boolean hasChildren) {
        this.id = id;
        this.nameUz = nameUz;
        this.nameCyr = nameCyr;
        this.nameEn = nameEn;
        this.nameRu = nameRu;
        this.descriptionUz = descriptionUz;
        this.descriptionCyr = descriptionCyr;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
        this.orderNumber = orderNumber;
        this.imageUrl = imageUrl;
        this.parentId = parentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.hasChildren = hasChildren;
    }

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

    public String getNameCyr() {
        return nameCyr;
    }

    public void setNameCyr(String nameCyr) {
        this.nameCyr = nameCyr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
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

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
