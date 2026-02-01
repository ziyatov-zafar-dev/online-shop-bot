package uz.zafar.onlineshoptelegrambot.dto.shop.response;

import uz.zafar.onlineshoptelegrambot.db.entity.enums.WorkDay;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;

import java.math.BigDecimal;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class ShopResponse {
    private UUID id;
    private String nameUz;
    private String nameCyr;
    private String nameEn;
    private String nameRu;
    private String brandUrl;

    private String descriptionUz;
    private String descriptionCyr;
    private String descriptionRu;
    private String descriptionEn;
    private String logoUrl;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private List<WorkDay> workDays;
    private String phone;
    private String email;
    private String telegram;
    private String instagram;
    private String facebook;
    private String website;
    private boolean hasPhone;
    private boolean hasEmail;
    private boolean hasTelegram;
    private boolean hasInstagram;
    private boolean hasFacebook;
    private boolean hasWebsite;
    private String createdAt;
    private String updatedAt;
    private Double latitude;
    private Double longitude;
    private String address;
    private String deliveryInfoUz;
    private String deliveryInfoEn;
    private String deliveryInfoRu;
    private String deliveryInfoCyr;
    private BigDecimal deliveryPrice;
    private Boolean hasDelivery;
    private BigDecimal deliveryOutsidePrice;
    private Boolean hasOutsideDelivery;
    private Long sellerId;

    public String getDeliveryInfoUz() {
        return deliveryInfoUz;
    }

    public void setDeliveryInfoUz(String deliveryInfoUz) {
        this.deliveryInfoUz = deliveryInfoUz;
    }

    public String getDeliveryInfoEn() {
        return deliveryInfoEn;
    }

    public void setDeliveryInfoEn(String deliveryInfoEn) {
        this.deliveryInfoEn = deliveryInfoEn;
    }

    public String getDeliveryInfoRu() {
        return deliveryInfoRu;
    }

    public void setDeliveryInfoRu(String deliveryInfoRu) {
        this.deliveryInfoRu = deliveryInfoRu;
    }

    public String getDeliveryInfoCyr() {
        return deliveryInfoCyr;
    }

    public void setDeliveryInfoCyr(String deliveryInfoCyr) {
        this.deliveryInfoCyr = deliveryInfoCyr;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }


    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(BigDecimal deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public Boolean getHasDelivery() {
        return hasDelivery;
    }

    public void setHasDelivery(Boolean hasDelivery) {
        this.hasDelivery = hasDelivery;
    }

    public BigDecimal getDeliveryOutsidePrice() {
        return deliveryOutsidePrice;
    }

    public void setDeliveryOutsidePrice(BigDecimal deliveryOutsidePrice) {
        this.deliveryOutsidePrice = deliveryOutsidePrice;
    }

    public Boolean getHasOutsideDelivery() {
        return hasOutsideDelivery;
    }

    public void setHasOutsideDelivery(Boolean hasOutsideDelivery) {
        this.hasOutsideDelivery = hasOutsideDelivery;
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

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public LocalTime getWorkStartTime() {
        return workStartTime;
    }

    public void setWorkStartTime(LocalTime workStartTime) {
        this.workStartTime = workStartTime;
    }

    public LocalTime getWorkEndTime() {
        return workEndTime;
    }

    public void setWorkEndTime(LocalTime workEndTime) {
        this.workEndTime = workEndTime;
    }

    public List<WorkDay> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(List<WorkDay> workDays) {
        this.workDays = workDays;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isHasPhone() {
        return hasPhone;
    }

    public void setHasPhone(boolean hasPhone) {
        this.hasPhone = hasPhone;
    }

    public boolean isHasEmail() {
        return hasEmail;
    }

    public void setHasEmail(boolean hasEmail) {
        this.hasEmail = hasEmail;
    }

    public boolean isHasTelegram() {
        return hasTelegram;
    }

    public void setHasTelegram(boolean hasTelegram) {
        this.hasTelegram = hasTelegram;
    }

    public boolean isHasInstagram() {
        return hasInstagram;
    }

    public void setHasInstagram(boolean hasInstagram) {
        this.hasInstagram = hasInstagram;
    }

    public boolean isHasFacebook() {
        return hasFacebook;
    }

    public void setHasFacebook(boolean hasFacebook) {
        this.hasFacebook = hasFacebook;
    }

    public boolean isHasWebsite() {
        return hasWebsite;
    }

    public void setHasWebsite(boolean hasWebsite) {
        this.hasWebsite = hasWebsite;
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

    public ShopResponse(UUID id, String brandUrl, String nameUz,
                        String nameCyr, String nameEn, String nameRu,
                        String descriptionUz, String descriptionCyr,
                        String descriptionRu, String descriptionEn,
                        String logoUrl, LocalTime workStartTime,
                        LocalTime workEndTime, List<WorkDay> workDays,
                        String phone, String email, String telegram, String instagram,
                        String facebook, String website, boolean hasPhone, boolean
                                hasEmail, boolean hasTelegram, boolean hasInstagram,
                        boolean hasFacebook, boolean hasWebsite, String createdAt,
                        String updatedAt, Double latitude, Double longitude, String
                                address, String deliveryInfoUz, String deliveryInfoRu, String deliveryInfoEn, String deliveryInfoCyr, BigDecimal deliveryPrice,
                        Boolean hasDelivery, BigDecimal deliveryOutsidePrice, Boolean
                                hasOutsideDelivery, Long sellerId) {
        this.id = id;
        this.nameUz = nameUz;
        this.brandUrl = brandUrl;
        this.nameCyr = nameCyr;
        this.nameEn = nameEn;
        this.nameRu = nameRu;
        this.descriptionUz = descriptionUz;
        this.descriptionCyr = descriptionCyr;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
        this.logoUrl = logoUrl;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.workDays = workDays;
        this.phone = phone;
        this.email = email;
        this.telegram = telegram;
        this.instagram = instagram;
        this.facebook = facebook;
        this.website = website;
        this.hasPhone = hasPhone;
        this.hasEmail = hasEmail;
        this.hasTelegram = hasTelegram;
        this.hasInstagram = hasInstagram;
        this.hasFacebook = hasFacebook;
        this.hasWebsite = hasWebsite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.deliveryInfoUz = deliveryInfoUz;
        this.deliveryInfoCyr = deliveryInfoCyr;
        this.deliveryInfoEn = deliveryInfoEn;
        this.deliveryInfoRu = deliveryInfoRu;
        this.deliveryPrice = deliveryPrice;
        this.hasDelivery = hasDelivery;
        this.deliveryOutsidePrice = deliveryOutsidePrice;
        this.hasOutsideDelivery = hasOutsideDelivery;
        this.sellerId = sellerId;
    }

    public String getBrandUrl() {
        return brandUrl;
    }

    public void setBrandUrl(String brandUrl) {
        this.brandUrl = brandUrl;
    }

    public ShopResponse() {
    }
}