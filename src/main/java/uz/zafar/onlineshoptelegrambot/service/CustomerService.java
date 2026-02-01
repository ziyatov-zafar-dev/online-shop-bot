package uz.zafar.onlineshoptelegrambot.service;

import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;

public interface CustomerService {
    ResponseDto<BotCustomer> checkUser(Long chatId);
}
