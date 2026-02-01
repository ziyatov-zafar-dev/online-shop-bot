package uz.zafar.onlineshoptelegrambot.service;

import uz.zafar.onlineshoptelegrambot.dto.enums.Purpose;

public interface EmailService {
    void send(String email, String code, String title, Purpose purpose);
}
