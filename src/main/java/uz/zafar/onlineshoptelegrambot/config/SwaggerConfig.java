package uz.zafar.onlineshoptelegrambot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Online Shop Telegram Bot API",
                version = "1.0",
                description = "Spring Boot 4 + JWT + Telegram"
        )
)
public class SwaggerConfig {
}
