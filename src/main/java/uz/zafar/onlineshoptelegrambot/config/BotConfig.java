package uz.zafar.onlineshoptelegrambot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;

@Configuration
public class BotConfig {
    @Bean
    public TelegramBot telegramBot(ObjectMapper objectMapper, TelegramProperties telegramProperties) {
        return new TelegramBot(telegramProperties.getSellers().getBot().getToken(), objectMapper);
    }

    @Bean("seller")
    public TelegramBot seller(ObjectMapper objectMapper, TelegramProperties telegramProperties) {
        return new TelegramBot(telegramProperties.getSellers().getBot().getToken(), objectMapper);
    }

    @Bean("admin")
    public TelegramBot admin(ObjectMapper objectMapper, TelegramProperties telegramProperties) {
        return new TelegramBot(telegramProperties.getAdmins().getBot().getToken(), objectMapper);
    }

    @Bean("customer")
    public TelegramBot customer(ObjectMapper objectMapper, TelegramProperties telegramProperties) {
        return new TelegramBot(telegramProperties.getUsers().getBot().getToken(), objectMapper);
    }

    @Bean("super-admin")
    public TelegramBot superAdmin(ObjectMapper objectMapper, TelegramProperties telegramProperties) {
        return new TelegramBot(telegramProperties.getSuperAdmin().getBot().getToken(), objectMapper);
    }
}
