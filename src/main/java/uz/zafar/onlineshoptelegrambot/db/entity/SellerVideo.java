package uz.zafar.onlineshoptelegrambot.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "seller_videos")
public class SellerVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    // UZ (Latin)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String videoUrlUz;
    private Long sizeUz;
    private String filenameUz;

    // CYRILLIC
    @Column(columnDefinition = "TEXT")
    private String videoUrlCyr;
    private Long sizeCyr;
    private String filenameCyr;

    // RU
    @Column(columnDefinition = "TEXT")
    private String videoUrlRu;
    private Long sizeRu;
    private String filenameRu;

    // EN
    @Column(columnDefinition = "TEXT")
    private String videoUrlEn;
    private Long sizeEn;
    private String filenameEn;

    // ===== GETTERS & SETTERS =====

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVideoUrlUz() {
        return videoUrlUz;
    }

    public void setVideoUrlUz(String videoUrlUz) {
        this.videoUrlUz = videoUrlUz;
    }

    public Long getSizeUz() {
        return sizeUz;
    }

    public void setSizeUz(Long sizeUz) {
        this.sizeUz = sizeUz;
    }

    public String getFilenameUz() {
        return filenameUz;
    }

    public void setFilenameUz(String filenameUz) {
        this.filenameUz = filenameUz;
    }

    public String getVideoUrlCyr() {
        return videoUrlCyr;
    }

    public void setVideoUrlCyr(String videoUrlCyr) {
        this.videoUrlCyr = videoUrlCyr;
    }

    public Long getSizeCyr() {
        return sizeCyr;
    }

    public void setSizeCyr(Long sizeCyr) {
        this.sizeCyr = sizeCyr;
    }

    public String getFilenameCyr() {
        return filenameCyr;
    }

    public void setFilenameCyr(String filenameCyr) {
        this.filenameCyr = filenameCyr;
    }

    public String getVideoUrlRu() {
        return videoUrlRu;
    }

    public void setVideoUrlRu(String videoUrlRu) {
        this.videoUrlRu = videoUrlRu;
    }

    public Long getSizeRu() {
        return sizeRu;
    }

    public void setSizeRu(Long sizeRu) {
        this.sizeRu = sizeRu;
    }

    public String getFilenameRu() {
        return filenameRu;
    }

    public void setFilenameRu(String filenameRu) {
        this.filenameRu = filenameRu;
    }

    public String getVideoUrlEn() {
        return videoUrlEn;
    }

    public void setVideoUrlEn(String videoUrlEn) {
        this.videoUrlEn = videoUrlEn;
    }

    public Long getSizeEn() {
        return sizeEn;
    }

    public void setSizeEn(Long sizeEn) {
        this.sizeEn = sizeEn;
    }

    public String getFilenameEn() {
        return filenameEn;
    }

    public void setFilenameEn(String filenameEn) {
        this.filenameEn = filenameEn;
    }
}
