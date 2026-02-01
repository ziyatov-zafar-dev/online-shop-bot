package uz.zafar.onlineshoptelegrambot.db.entity.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.WorkDay;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shops")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID pkey;
    @Column(nullable = false)
    private String nameUz;
    @Column(nullable = false)
    private String nameCyr;
    @Column(nullable = false)
    private String nameEn;
    @Column(nullable = false)
    private String nameRu;
    @Column(columnDefinition = "TEXT")
    private String descriptionUz;
    @Column(columnDefinition = "TEXT")
    private String descriptionCyr;
    @Column(nullable = false,name = "brand_url",columnDefinition = "TEXT")
    private String brandUrl;
    @Column(columnDefinition = "TEXT")
    private String descriptionEn;
    @Column(columnDefinition = "TEXT")
    private String descriptionRu;
    private Boolean active;
    @Column(columnDefinition = "TEXT",name = "logo")
    private String logoUrl;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;
    @JsonIgnore
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    @ElementCollection(targetClass = WorkDay.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "shop_work_days",
            joinColumns = @JoinColumn(name = "shop_id")
    )
    @Column(name = "worked_days")
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
    @Column(columnDefinition = "TEXT")
    private String deliveryInfoUz;
    @Column(columnDefinition = "TEXT")
    private String deliveryInfoRu;
    @Column(columnDefinition = "TEXT")
    private String deliveryInfoEn;
    @Column(columnDefinition = "TEXT")
    private String deliveryInfoCyr;
    @Column(nullable = false)
    private BigDecimal deliveryPrice;
    @Column(nullable = false)
    private Boolean hasDelivery;
    @Column(nullable = false)
    private BigDecimal deliveryOutsidePrice;
    @Column(nullable = false)
    private Boolean hasOutsideDelivery;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Shop() {
        this.active = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /* =========================
       GETTERS & SETTERS
       ========================= */
    public UUID getPkey() {
        return pkey;
    }

    public Boolean getHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(Boolean hasLocation) {
        this.hasLocation = hasLocation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Seller getSeller() {
        return this.seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
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

    public List<WorkDay> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(List<WorkDay> workDays) {
        this.workDays = workDays;
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

    public BigDecimal getDeliveryOutsidePrice() {
        return deliveryOutsidePrice;
    }

    public void setDeliveryOutsidePrice(BigDecimal deliveryOutsidePrice) {
        this.deliveryOutsidePrice = deliveryOutsidePrice;
    }

    public String getBrandUrl() {
        return brandUrl;
    }

    public void setBrandUrl(String brandUrl) {
        this.brandUrl = brandUrl;
    }

    public Boolean getHasOutsideDelivery() {
        return hasOutsideDelivery;
    }

    public void setHasOutsideDelivery(Boolean hasOutsideDelivery) {
        this.hasOutsideDelivery = hasOutsideDelivery;
    }

    /* =========================
               toString
               ========================= */
    @Override
    public String toString() {
        return "Shop{" +
                "pkey=" + pkey +
                ", name='" + nameUz + '\'' +
                ", active=" + active +
                ", seller=" + (seller != null ? seller.getPkey() : null) +
                ", products=" + (products != null ? products.size() : 0) +
                ", workDays=" + workDays +
                ", workStart=" + workStartTime +
                ", workEnd=" + workEndTime +
                ", phone=" + phone +
                ", telegram=" + telegram +
                ", instagram=" + instagram +
                ", facebook=" + facebook +
                ", website=" + website +
                '}';
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
}
