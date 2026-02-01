package uz.zafar.onlineshoptelegrambot.dto.shop.request;

import uz.zafar.onlineshoptelegrambot.db.entity.enums.WorkDay;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class EditOrAddShopRequestDto {
    private String nameUz;
    private String nameCyr;
    private String nameEn;
    private String nameRu;
    private String descriptionUz;
    private String descriptionCyr;
    private String descriptionEn;
    private String descriptionRu;
    private String brandUrl;
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
    private Boolean hasPhone;
    private Boolean hasEmail;
    private Boolean hasTelegram;
    private Boolean hasInstagram;
    private Boolean hasFacebook;
    private Boolean hasWebsite;
    private Boolean hasLocation;
    private Double latitude;
    private Double longitude;
    private String address;
    private String deliveryInfoUz;
    private String deliveryInfoRu;
    private String deliveryInfoEn;
    private String deliveryInfoCyr;
    private BigDecimal deliveryPrice;
    private Boolean hasDelivery;
    private BigDecimal deliveryOutsidePrice;
    private Boolean hasOutsideDelivery;

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

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu = descriptionRu;
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

    public Boolean getHasPhone() {
        return hasPhone;
    }

    public void setHasPhone(Boolean hasPhone) {
        this.hasPhone = hasPhone;
    }

    public Boolean getHasEmail() {
        return hasEmail;
    }

    public void setHasEmail(Boolean hasEmail) {
        this.hasEmail = hasEmail;
    }

    public Boolean getHasTelegram() {
        return hasTelegram;
    }

    public void setHasTelegram(Boolean hasTelegram) {
        this.hasTelegram = hasTelegram;
    }

    public Boolean getHasInstagram() {
        return hasInstagram;
    }

    public void setHasInstagram(Boolean hasInstagram) {
        this.hasInstagram = hasInstagram;
    }

    public Boolean getHasFacebook() {
        return hasFacebook;
    }

    public void setHasFacebook(Boolean hasFacebook) {
        this.hasFacebook = hasFacebook;
    }

    public Boolean getHasWebsite() {
        return hasWebsite;
    }

    public void setHasWebsite(Boolean hasWebsite) {
        this.hasWebsite = hasWebsite;
    }

    public Boolean getHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(Boolean hasLocation) {
        this.hasLocation = hasLocation;
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

    public String getDeliveryInfoUz() {
        return deliveryInfoUz;
    }

    public void setDeliveryInfoUz(String deliveryInfoUz) {
        this.deliveryInfoUz = deliveryInfoUz;
    }

    public String getDeliveryInfoRu() {
        return deliveryInfoRu;
    }

    public void setDeliveryInfoRu(String deliveryInfoRu) {
        this.deliveryInfoRu = deliveryInfoRu;
    }

    public String getDeliveryInfoEn() {
        return deliveryInfoEn;
    }

    public void setDeliveryInfoEn(String deliveryInfoEn) {
        this.deliveryInfoEn = deliveryInfoEn;
    }

    public String getDeliveryInfoCyr() {
        return deliveryInfoCyr;
    }

    public void setDeliveryInfoCyr(String deliveryInfoCyr) {
        this.deliveryInfoCyr = deliveryInfoCyr;
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

    public String getBrandUrl() {
        return brandUrl;
    }

    public void setBrandUrl(String brandUrl) {
        this.brandUrl = brandUrl;
    }
}
