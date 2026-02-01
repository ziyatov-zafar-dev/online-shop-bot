package uz.zafar.onlineshoptelegrambot.dto.product.request;

import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;

import java.util.UUID;

public class AddProductTypeImageRequestDto {
    private String imageUrl;
    private String imgName;
    private Long imgSize;
    private Boolean main;
    private UUID productTypeId;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public Long getImgSize() {
        return imgSize;
    }

    public void setImgSize(Long imgSize) {
        this.imgSize = imgSize;
    }

    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public UUID getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(UUID productTypeId) {
        this.productTypeId = productTypeId;
    }
}
