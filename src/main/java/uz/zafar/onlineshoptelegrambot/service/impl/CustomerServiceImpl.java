package uz.zafar.onlineshoptelegrambot.service.impl;

import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final BotCustomerRepository botCustomerRepository;

    public CustomerServiceImpl(BotCustomerRepository botCustomerRepository) {
        this.botCustomerRepository = botCustomerRepository;
    }

    @Override
    public ResponseDto<BotCustomer> checkUser(Long chatId) {
        BotCustomer botCustomer = botCustomerRepository.checkUser(chatId).orElse(null);
        if (botCustomer == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER);
        }
        return ResponseDto.success(botCustomer);
    }
}
