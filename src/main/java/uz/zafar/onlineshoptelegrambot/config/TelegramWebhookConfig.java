//package uz.codebyz.onlinecoursebackend.config;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestTemplate;
//
//@Configuration
//public class TelegramWebhookConfig {
//
//    private final TelegramProperties props;
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public TelegramWebhookConfig(TelegramProperties props) {
//        this.props = props;
//    }
//
//    @PostConstruct
//    public void registerWebhook() {
//
//        String token = props.getUsers().getBot().getToken();
//        String webhookUrl = props.getBaseUrl()
//                + props.getUsers().getBot().getWebhookPath();
//
//        String url = "https://api.telegram.org/bot"
//                + token
//                + "/setWebhook?url="
//                + webhookUrl;
//
//        restTemplate.getForObject(url, String.class);
//
//        System.out.println("✅ Telegram USERS webhook registered: " + webhookUrl);
//    }
//}
package uz.zafar.onlineshoptelegrambot.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TelegramWebhookConfig {

    private final TelegramProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    public TelegramWebhookConfig(TelegramProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void registerAllWebhooks() {

        // USERS
        register(
                "USERS",
                props.getUsers().getBot().getToken(),
                props.getUsers().getBot().getWebhookPath()
        );

        // SELLERS
        register(
                "SELLERS",
                props.getSellers().getBot().getToken(),
                props.getSellers().getBot().getWebhookPath()
        );

        // CONFIRM SELLERS
        register(
                "SUPER-ADMIN",
                props.getSuperAdmin().getBot().getToken(),
                props.getSuperAdmin().getBot().getWebhookPath()
        );

        // ADMINS
        register(
                "ADMINS",
                props.getAdmins().getBot().getToken(),
                props.getAdmins().getBot().getWebhookPath()
        );
    }

    private void register(String name, String token, String webhookPath) {

        if (token == null || webhookPath == null) {
            System.out.println("⚠️ Telegram " + name + " webhook skipped (token or path is null)");
            return;
        }

        String webhookUrl = props.getBaseUrl() + webhookPath;

        String url = "https://api.telegram.org/bot"
                + token
                + "/setWebhook?url="
                + webhookUrl;

        restTemplate.getForObject(url, String.class);

        System.out.println("✅ Telegram " + name + " webhook registered: " + webhookUrl);
    }
}
