package uz.zafar.onlineshoptelegrambot.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.zafar.onlineshoptelegrambot.botservice.TelegramAdminsService;
import uz.zafar.onlineshoptelegrambot.botservice.TelegramSuperAdminService;
import uz.zafar.onlineshoptelegrambot.botservice.TelegramSellersService;
import uz.zafar.onlineshoptelegrambot.botservice.TelegramUsersService;

import java.util.Map;

@RestController
@Hidden
@RequestMapping("telegram")
public class TelegramWebhookController {


    private final TelegramSellersService telegramSellersService;
    private final TelegramAdminsService telegramAdminsService;
    private final TelegramUsersService telegramUsersService;
    private final TelegramSuperAdminService telegramSuperAdminService;

    public TelegramWebhookController(TelegramSellersService telegramSellersService, TelegramAdminsService telegramAdminsService, TelegramUsersService telegramUsersService, TelegramSuperAdminService telegramSuperAdminService) {
        this.telegramSellersService = telegramSellersService;
        this.telegramAdminsService = telegramAdminsService;
        this.telegramUsersService = telegramUsersService;
        this.telegramSuperAdminService = telegramSuperAdminService;
    }

    @PostMapping("sellers/webhook")
    public ResponseEntity<Void> handleSellers(@RequestBody Map<String, Object> update, HttpServletRequest request) throws Exception {
        telegramSellersService.handleUpdate(update, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("admins/webhook")
    public ResponseEntity<Void> handleAdmins(@RequestBody Map<String, Object> update, HttpServletRequest request) throws Exception {
        telegramAdminsService.handleUpdate(update, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("users/webhook")
    public ResponseEntity<Void> handleUsers(@RequestBody Map<String, Object> update, HttpServletRequest request) throws Exception {
        telegramUsersService.handleUpdate(update, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("super-admin/webhook")
    public ResponseEntity<Void> handleConfirmUsers(@RequestBody Map<String, Object> update, HttpServletRequest request) throws Exception {
        telegramSuperAdminService.handleUpdate(update, request);
        return ResponseEntity.ok().build();
    }
}
