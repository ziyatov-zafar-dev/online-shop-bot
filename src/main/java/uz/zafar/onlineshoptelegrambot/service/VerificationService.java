package uz.zafar.onlineshoptelegrambot.service;

import uz.zafar.onlineshoptelegrambot.db.entity.enums.VerificationType;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.db.entity.user.VerificationCode;

import java.util.Optional;

public interface VerificationService {
    public VerificationCode createVerification(User user, VerificationType type, String targetEmail);

    public Optional<VerificationCode> validateCode(User user, VerificationType type, String code, String targetEmail);
}
