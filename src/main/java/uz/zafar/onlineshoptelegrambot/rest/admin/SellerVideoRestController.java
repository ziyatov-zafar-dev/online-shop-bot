package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.SellerVideo;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerVideoRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;

import java.util.List;

@RestController
@RequestMapping("api/admin/seller/video")
public class SellerVideoRestController {
    private final SellerVideoRepository sellerVideoRepository;

    public SellerVideoRestController(SellerVideoRepository sellerVideoRepository) {
        this.sellerVideoRepository = sellerVideoRepository;
    }


    @PostMapping("change-video")
    public ResponseDto<?> changeVideo(@RequestBody SellerVideoRequest req) {

        SellerVideo video = sellerVideoRepository
                .findAll()
                .stream()
                .findFirst()
                .orElse(new SellerVideo());
        video.setVideoUrlUz(req.getVideoUrlUz());
        video.setSizeUz(req.getSizeUz());
        video.setFilenameUz(req.getFilenameUz());
        video.setVideoUrlCyr(req.getVideoUrlCyr());
        video.setSizeCyr(req.getSizeCyr());
        video.setFilenameCyr(req.getFilenameCyr());
        video.setVideoUrlRu(req.getVideoUrlRu());
        video.setSizeRu(req.getSizeRu());
        video.setFilenameRu(req.getFilenameRu());
        video.setVideoUrlEn(req.getVideoUrlEn());
        video.setSizeEn(req.getSizeEn());
        video.setFilenameEn(req.getFilenameEn());
        sellerVideoRepository.save(video);
        return ResponseDto.success();
    }


    @GetMapping
    public SellerVideo video() {
        return sellerVideoRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(SellerVideo::new);
    }


    public static class SellerVideoRequest {

        private String videoUrlUz;
        private Long sizeUz;
        private String filenameUz;

        // CYRILLIC
        private String videoUrlCyr;
        private Long sizeCyr;
        private String filenameCyr;

        // RU
        private String videoUrlRu;
        private Long sizeRu;
        private String filenameRu;

        // EN
        private String videoUrlEn;
        private Long sizeEn;
        private String filenameEn;

        // ===== GETTERS & SETTERS =====

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
}
