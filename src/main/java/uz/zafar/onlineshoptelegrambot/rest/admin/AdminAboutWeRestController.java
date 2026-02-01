package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.AboutWe;
import uz.zafar.onlineshoptelegrambot.db.repositories.AboutWeRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;

@RestController
@RequestMapping("api/admin/about/we")
public class AdminAboutWeRestController {
    private final AboutWeRepository aboutWeRepository;

    public AdminAboutWeRestController(AboutWeRepository aboutWeRepository) {
        this.aboutWeRepository = aboutWeRepository;
    }

    @GetMapping
    public ResponseDto<?> get() {
        if (aboutWeRepository.findAll().isEmpty()) {
            AboutWe we = new AboutWe();
            we.setCyr("cyr");
            we.setUz("cyr");
            we.setEn("cyr");
            we.setRu("cyr");
            return ResponseDto.success(we);
        }
        return ResponseDto.success(aboutWeRepository.findAll().get(0));
    }

    @PutMapping("edit")
    public ResponseDto<?> edit(@RequestBody AboutWeReq req) {
        AboutWe we;
        if (aboutWeRepository.findAll().isEmpty()) {
            we = new AboutWe();
        } else we = aboutWeRepository.findAll().get(0);
        we.setCyr(req.getCyr());
        we.setUz(req.getUz());
        we.setEn(req.getEn());
        we.setRu(req.getRu());
        try {
            return ResponseDto.success(aboutWeRepository.save(we));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.error(ErrorCode.ERROR);
        }
    }

    public static class AboutWeReq {
        private String uz;
        private String ru;
        private String en;
        private String cyr;

        public String getUz() {
            return uz;
        }

        public void setUz(String uz) {
            this.uz = uz;
        }

        public String getRu() {
            return ru;
        }

        public void setRu(String ru) {
            this.ru = ru;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }

        public String getCyr() {
            return cyr;
        }

        public void setCyr(String cyr) {
            this.cyr = cyr;
        }
    }
}
