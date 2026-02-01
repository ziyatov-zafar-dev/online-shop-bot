package uz.zafar.onlineshoptelegrambot.rest.superadmin;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.UserRole;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.db.entity.user.VerificationCode;
import uz.zafar.onlineshoptelegrambot.db.repositories.UserRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.VerificationCodeRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/super-admin/users")
public class SuperAdminAdminRestController {

    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminAdminRestController(UserRepository userRepository, VerificationCodeRepository verificationCodeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admins")
    public ResponseEntity<?> admins() {
        return ResponseEntity.ok(
                userRepository.admins()
        );
    }

    @PutMapping("/change-password/{userid}")
    @Transactional
    public ResponseDto<?> changePassword(@PathVariable("userid") UUID userid, @RequestBody ChangePassword req) {
        User user = userRepository.findById(userid).orElse(null);
        if (user == null) return ResponseDto.error(ErrorCode.ERROR);
        if (user.getRole() == UserRole.SELLER || user.getRole() == UserRole.USER)
            return ResponseDto.error(ErrorCode.ERROR);
        if (!req.password.equals(req.confirmPassword)) return ResponseDto.error(ErrorCode.PASSWORDS_NOT_MATCH);
        user.setPassword(passwordEncoder.encode(req.password));
        userRepository.save(user);
        return ResponseDto.success();
    }

    @PostMapping("create-new-admin")
    @Transactional
    public ResponseDto<?> create_admin(@RequestBody CreateNewAdmin req) {
        if (userRepository.findByEmail(req.email.toLowerCase()).isPresent()) {
            return ResponseDto.error(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.findByUsername(req.username.toLowerCase()).isPresent()) {
            return ResponseDto.error(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        User user = new User();
        user.setFullName(req.fullName);
        user.setBirthdate(req.getBirthdate());
        user.setRole(UserRole.ADMIN);
        user.setPassword(passwordEncoder.encode(req.password));
        user.setUsername(req.getUsername().toLowerCase());
        user.setEmail(req.getEmail().toLowerCase());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return ResponseDto.success();
    }

    @PutMapping("change-email/{userid}")
    @Transactional
    public ResponseDto<?> changeEmail(@PathVariable("userid") UUID userid, @RequestParam("email") String email) {
        User user = userRepository.findById(userid).orElse(null);
        if (user == null) return ResponseDto.error(ErrorCode.ERROR);
        if (userRepository.findByEmail(email.toLowerCase()).isPresent()) {
            return ResponseDto.error(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        user.setEmail(email.toLowerCase());
        userRepository.save(user);
        return ResponseDto.success();
    }

    @DeleteMapping("delete-admin/{userid}")
    @Transactional
    public ResponseDto<?> deleteAdmin(@PathVariable("userid") UUID userid) {
        User user = userRepository.findById(userid).orElse(null);
        if (user == null) return ResponseDto.error(ErrorCode.ERROR);
        List<VerificationCode> list = verificationCodeRepository.findAllByUserId(user.getPkey());
        if (!list.isEmpty()) for (VerificationCode verificationCode : list) {
            verificationCodeRepository.delete(verificationCode);
        }
        userRepository.delete(user);
        return ResponseDto.success();
    }

    @GetMapping("verification-codes/list")
    public ResponseEntity<?> ver(@RequestParam("page") int page, @RequestParam("size") int size) {
        return ResponseEntity.ok(
                verificationCodeRepository.findAllVerificationCodes(PageRequest.of(page, size))
        );
    }

    public static class CreateNewAdmin {
        private String fullName;
        private LocalDate birthdate;
        private String password;
        private String username;
        private String email;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public LocalDate getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(LocalDate birthdate) {
            this.birthdate = birthdate;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class ChangePassword {
        private String password;
        private String confirmPassword;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}
