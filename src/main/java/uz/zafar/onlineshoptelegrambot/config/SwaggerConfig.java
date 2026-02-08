package uz.zafar.onlineshoptelegrambot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI openAPI() {
                return new OpenAPI()
                        .info(new Info()
                                .title("Online Shop Telegram Bot API")
                                .description("Online shop backend + Telegram bot uchun REST API")
                                .version("1.0.0")
                                .contact(new Contact()
                                        .name("Zafar Ziyatov")
                                        .email("ziyatovzafar98@gmail.com"))
                                .license(new License()
                                        .name("Apache 2.0")
                                        .url("https://www.apache.org/licenses/LICENSE-2.0")));
        }
}
