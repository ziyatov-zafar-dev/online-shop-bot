package uz.zafar.onlineshoptelegrambot.botservice;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;

@Service
public class SuperAdminTelegramBotFunction {
    private final TelegramBot bot;

    public SuperAdminTelegramBotFunction(@Qualifier("super-admin") TelegramBot bot) {
        this.bot = bot;
    }

    public void start(Long chatId, String firstName, String lastName, String username) {
        bot.sendMessage(chatId, "Super admin role");
    }

    public void example(Long chatId, String payload) {
        bot.sendMessage(chatId, "You sent the payload: " + payload);
    }
}
