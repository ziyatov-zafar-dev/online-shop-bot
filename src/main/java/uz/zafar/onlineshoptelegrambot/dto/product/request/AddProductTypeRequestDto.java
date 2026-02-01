package uz.zafar.onlineshoptelegrambot.dto.product.request;

import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;

import java.math.BigDecimal;
import java.util.List;

public class AddProductTypeRequestDto {
    private String nameUz;
    private String nameCyr;
    private String nameRu;
    private String nameEn;
    private BigDecimal price;
    private Integer stock;
    private List<AddProductTypeImageRequestDto> images;

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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public List<AddProductTypeImageRequestDto> getImages() {
        return images;
    }

    public void setImages(List<AddProductTypeImageRequestDto> images) {
        this.images = images;
    }
}
