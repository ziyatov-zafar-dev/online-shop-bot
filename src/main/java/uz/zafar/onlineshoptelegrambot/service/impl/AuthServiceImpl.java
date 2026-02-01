package uz.zafar.onlineshoptelegrambot.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.zafar.onlineshoptelegrambot.dto.enums.Purpose;
import uz.zafar.onlineshoptelegrambot.security.jwt.JwtService;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.VerificationType;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.db.entity.user.VerificationCode;
import uz.zafar.onlineshoptelegrambot.db.repositories.UserRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.auth.*;
import uz.zafar.onlineshoptelegrambot.dto.auth.response.TokenResponse;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.service.AuthService;
import uz.zafar.onlineshoptelegrambot.service.EmailService;
import uz.zafar.onlineshoptelegrambot.service.VerificationService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationService verificationService, JwtService jwtService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Override
    public ResponseDto<Void> signIn(LoginForm request) {
        User user;
        user = findByLogin(request.getLogin()).orElse(null);
        if (user == null) return ResponseDto.error(ErrorCode.UNAUTHORIZED);
        if (!user.getEnabled()) {
            return (ResponseDto.error(ErrorCode.NOT_CONFIRMED_ACCOUNT));
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseDto.error(ErrorCode.BAD_CREDENTIALS);
        }
        VerificationCode code = verificationService.createVerification(user, VerificationType.SIGN_IN, null);
        emailService.send(user.getEmail(), code.getCode(), "Kirish kodi", Purpose.SIGN_IN);
        return (ResponseDto.success());
    }

    @Transactional
    @Override
    public ResponseDto<TokenResponse> verifySignIn(SignInVerifyRequest request) {

        User user = findByLogin(request.getLogin()).orElse(null);
        if (user == null) return ResponseDto.error(ErrorCode.UNAUTHORIZED);
        Optional<VerificationCode> valid = verificationService.validateCode(user, VerificationType.SIGN_IN, request.getCode(), null);

        if (valid.isEmpty()) {
            return ResponseDto.error(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        TokenResponse response = new TokenResponse();
        response.setAccessToken(refresh);
        response.setRefreshToken(refresh);
        return ResponseDto.success(response);
    }

    private Optional<User> findByLogin(String login) {
        login = login.toLowerCase();
        if (login.contains("@")) {
            return userRepository.findByEmail(login);
        }
        return userRepository.findByUsername(login);
    }

    @Transactional
    @Override
    public ResponseDto<Object> signUp(SignUpRequest request) {
        String email = request.getEmail().toLowerCase();
        String username = request.getUsername().toLowerCase();

        // Avvalgi foydalanuvchini email bo'yicha tekshirish
        Optional<User> existingByEmail = userRepository.findByEmail(email);
        Optional<User> existingByUsername = userRepository.findByUsername(username);

        // Agar email yoki username ishlatilgan bo'lsa va enabled=true bo'lsa, xato qaytarish
        if (existingByEmail.isPresent() && existingByEmail.get().isEnabled()) {
            return ResponseDto.error(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (existingByUsername.isPresent() && existingByUsername.get().isEnabled()) {
            return ResponseDto.error(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // Parol va confirmPassword mosligini tekshirish
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseDto.error(ErrorCode.PASSWORDS_NOT_MATCH);
        }

        // Username formatini tekshirish
        if (!isValidUsername(username)) {
            return ResponseDto.error(ErrorCode.INVALID_USERNAME);
        }

        User user;
        if ((existingByEmail.isPresent() && !existingByEmail.get().isEnabled())
                || (existingByUsername.isPresent() && !existingByUsername.get().isEnabled())) {
            // Tasdiqlanmagan foydalanuvchi bor: uni update qilamiz
            user = existingByEmail.orElse(existingByUsername.get());
            user.setUsername(username.toLowerCase());
            user.setFullName(request.getFullName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setBirthdate(request.getBirthdate());
            user.setUpdatedAt(LocalDateTime.now());
            user.setEnabled(false); // hali tasdiqlanmadi
        } else {
            // Yangi foydalanuvchi yaratish
            user = new User();
            user.setUsername(username);
            user.setFullName(request.getFullName());
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setBirthdate(request.getBirthdate());
            user.setEnabled(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
        }

        user = userRepository.save(user);

        VerificationCode verificationCode = verificationService.createVerification(user, VerificationType.SIGN_UP, null);
        emailService.send(user.getEmail(), verificationCode.getCode(), "Ro'yxatdan o'tish kodi", Purpose.SIGN_UP);
        return ResponseDto.success();
    }


    @Transactional
    @Override
    public ResponseDto<TokenResponse> verifySignUp(SignUpVerifyRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (null == user) return ResponseDto.error(ErrorCode.BAD_CREDENTIALS);
        Optional<VerificationCode> valid = verificationService.validateCode(user, VerificationType.SIGN_UP, request.getCode(), null);

        if (valid.isEmpty()) {
            return ResponseDto.error(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        user.setEnabled(true);
        user = userRepository.save(user);
        return ResponseDto.success();
    }

    @Transactional
    @Override
    public ResponseDto<Object> forgotPassword(ForgotPasswordRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        optionalUser.ifPresent(user -> {
            VerificationCode verificationCode = verificationService.createVerification(user, VerificationType.FORGOT_PASSWORD, null);
            emailService.send(user.getEmail(), verificationCode.getCode(), "Parolni o'zgartirish kodi", Purpose.FORGOT_PASSWORD);

//            emailService.sendEmail(user.getEmail(), "Parolni tiklash", "Parolni tiklash kodini kiriting.", verificationCode.getCode());
//            email sender

        });
        return optionalUser.isPresent() ? ResponseDto.success() : ResponseDto.error(ErrorCode.NOT_FOUND_EMAIL);
    }

    @Transactional
    @Override
    public ResponseDto<Object> resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BadCredentialsException("Login yoki parol noto‘g‘ri."));
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseDto.error(ErrorCode.PASSWORDS_NOT_MATCH);
        }
        Optional<VerificationCode> valid = verificationService.validateCode(user, VerificationType.FORGOT_PASSWORD, request.getCode(), null);
        if (valid.isEmpty()) {
            return ResponseDto.error(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user = userRepository.save(user);
        return ResponseDto.success();
    }


    public boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return username.matches("^(?=.{5,32}$)[a-zA-Z0-9]+(_[a-zA-Z0-9]+)*$");
    }

}
