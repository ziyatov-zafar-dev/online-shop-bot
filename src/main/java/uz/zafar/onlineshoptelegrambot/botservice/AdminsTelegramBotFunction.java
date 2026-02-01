package uz.zafar.onlineshoptelegrambot.botservice;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;
import uz.zafar.onlineshoptelegrambot.config.TelegramProperties;

@Service
public class AdminsTelegramBotFunction {

    private final TelegramProperties telegramProperties;
    private final TelegramBot bot;

    public AdminsTelegramBotFunction(TelegramProperties telegramProperties,
                                     @Qualifier("admin") TelegramBot bot
    ) {
        this.telegramProperties = telegramProperties;
        this.bot = bot;
    }

    public void start(Long chatId, String firstName, String lastName, String username) {
        bot.sendMessage(chatId,"adminstelegrambot");
    }
}
