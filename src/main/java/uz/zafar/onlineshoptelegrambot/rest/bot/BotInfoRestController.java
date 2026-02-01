package uz.zafar.onlineshoptelegrambot.rest.bot;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.zafar.onlineshoptelegrambot.config.ApiSecurityConfig;
import uz.zafar.onlineshoptelegrambot.config.TelegramProperties;

@RestController
@RequestMapping("api/telegram/bot")
public class BotInfoRestController {
    private final TelegramProperties telegramProperties;
    private final ApiSecurityConfig apiSecurityConfig;

    public BotInfoRestController(TelegramProperties telegramProperties, ApiSecurityConfig apiSecurityConfig) {
        this.telegramProperties = telegramProperties;
        this.apiSecurityConfig = apiSecurityConfig;
    }

    @GetMapping("all-bots")
    public ResponseEntity<?> allBots(@RequestHeader(value = "X-API-TOKEN", required = false) String token) {
        return ResponseEntity.ok(telegramProperties);
    }

    @GetMapping("seller")
    public ResponseEntity<?> sellers(@RequestHeader(value = "X-API-TOKEN", required = false) String token) {
        return ResponseEntity.ok(telegramProperties.getSellers().getBot());
    }

    @GetMapping("admins")
    public ResponseEntity<?> admins(@RequestHeader(value = "X-API-TOKEN") String token) {
        if (token == null || !token.equals(apiSecurityConfig.getApiToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing API token");
        }
        return ResponseEntity.ok(telegramProperties.getAdmins().getBot());
    }

    @GetMapping("users")
    public ResponseEntity<?> users(@RequestHeader(value = "X-API-TOKEN") String token) {
        if (token == null || !token.equals(apiSecurityConfig.getApiToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing API token");
        }
        return ResponseEntity.ok(telegramProperties.getUsers().getBot());
    }

    @GetMapping("super-admins")
    public ResponseEntity<?> superAdmin(@RequestHeader(value = "X-API-TOKEN") String token) {
        if (token == null || !token.equals(apiSecurityConfig.getApiToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing API token");
        }
        return ResponseEntity.ok(telegramProperties.getSuperAdmin().getBot());
    }
}