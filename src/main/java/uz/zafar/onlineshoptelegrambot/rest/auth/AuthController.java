package uz.zafar.onlineshoptelegrambot.rest.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.auth.*;
import uz.zafar.onlineshoptelegrambot.dto.auth.response.TokenResponse;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.security.UserPrincipal;
import uz.zafar.onlineshoptelegrambot.service.AuthService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Sign-up, sign-in, verification, password reset, refresh-token va username tekshirish")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("user-info")
    public ResponseDto<?> userInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseDto.error(ErrorCode.ERROR);
        }
        return ResponseDto.success(userPrincipal.getUser());
    }

    @Operation(summary = "Email/parol bilan ro'yxatdan o'tish (2 bosqich)", description = "User yaratadi, tasdiqlash kodi emailga yuboriladi")
    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDto<Object>> signUp(@Valid @RequestBody SignUpRequest request) {
        request.setEmail(request.getEmail().trim());
        request.setUsername(request.getUsername().trim());
        ResponseDto<Object> response = authService.signUp(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @Operation(summary = "Sign-up kodi bilan tasdiqlash")
    @PostMapping("/sign-up/verify")
    public ResponseEntity<ResponseDto<?>> verifySignUp(@Valid @RequestBody SignUpVerifyRequest request) {
        ResponseDto<?> response = authService.verifySignUp(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @Operation(summary = "Email yoki username va parol bilan kirish (kod yuborish bosqichi)")
    @PostMapping("/sign-in")
    public ResponseDto<Void> signIn(@Valid @RequestBody LoginForm request) {
        request.setLogin(request.getLogin().trim());
        return authService.signIn(request);
    }

    @Operation(summary = "Kodni tasdiqlab JWT olish")
    @PostMapping("/sign-in/verify")
    public ResponseEntity<ResponseDto<TokenResponse>> verifySignIn(@Valid @RequestBody SignInVerifyRequest request, HttpServletRequest http) {
        ResponseDto<TokenResponse> response = authService.verifySignIn(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @Operation(summary = "Parolni unutdim – kod yuborish")
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDto<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }


    @Operation(summary = "Parolni unutdim – kod yuborish")
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDto<Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}
