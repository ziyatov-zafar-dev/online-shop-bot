package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.contact.ContactWe;
import uz.zafar.onlineshoptelegrambot.db.repositories.ContactWeRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;

import java.util.List;

@RestController
@RequestMapping("api/admin/contact")
public class AdminContactsRestController {
    private final ContactWeRepository contactWeRepository;

    public AdminContactsRestController(ContactWeRepository contactWeRepository) {
        this.contactWeRepository = contactWeRepository;
    }

    @GetMapping("list")
    public List<?> list() {
        return contactWeRepository.allContacts();
    }

    @PostMapping("add-contact")
    public ResponseDto<?> add(@RequestBody AddContact req) {
        ContactWe contactWe = new ContactWe();
        contactWe.setFullName(req.fullName);
        contactWe.setPhone(req.phone);
        contactWe.setTelegramUsername(req.telegramUsername);
        contactWeRepository.save(contactWe);
        return ResponseDto.success();
    }


    @PutMapping("edit-contact/{contactId}")
    public ResponseDto<?> edit(@PathVariable("contactId") Integer contactId, @RequestBody AddContact req) {
        ContactWe contactWe = contactWeRepository.findById(contactId).orElse(null);
        if (contactWe == null) return ResponseDto.error(ErrorCode.ERROR);
        contactWe.setFullName(req.fullName);
        contactWe.setPhone(req.phone);
        contactWe.setTelegramUsername(req.telegramUsername);
        contactWeRepository.save(contactWe);
        return ResponseDto.success();
    }

    @DeleteMapping("delete-contact/{contactId}")
    @Transactional
    public ResponseDto<?> delete(@PathVariable("contactId") Integer contactId) {
        ContactWe contactWe = contactWeRepository.findById(contactId).orElse(null);
        if (contactWe == null) return ResponseDto.error(ErrorCode.ERROR);
        contactWe.setDeleted(true);
        contactWeRepository.save(contactWe);
        return ResponseDto.success();
    }

    public static class AddContact {
        private String fullName;
        private String phone;
        private String telegramUsername;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getTelegramUsername() {
            return telegramUsername;
        }

        public void setTelegramUsername(String telegramUsername) {
            this.telegramUsername = telegramUsername;
        }
    }
}
