package uz.zafar.onlineshoptelegrambot.scheduler;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;
import uz.zafar.onlineshoptelegrambot.bot.kyb.seller.SellerKyb;
import uz.zafar.onlineshoptelegrambot.bot.msg.SellerMsg;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.enums.SellerEventCode;
import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SubscriptionPlanRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class SellerCheckingPlan {
    private final SellerRepository sellerRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final BotSellerRepository botSellerRepository;
    private final TelegramBot sellerBot;
    private final SellerMsg sellerMsg;
    private final SellerKyb sellerKyb;
    public SellerCheckingPlan(SellerRepository sellerRepository,
                              SubscriptionPlanRepository subscriptionPlanRepository,
                              BotSellerRepository botSellerRepository,
                              @Qualifier("seller") TelegramBot sellerBot,
                              SellerMsg sellerMsg, SellerKyb sellerKyb) {
        this.sellerRepository = sellerRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.botSellerRepository = botSellerRepository;
        this.sellerBot = sellerBot;
        this.sellerMsg = sellerMsg;
        this.sellerKyb = sellerKyb;
    }

    @Scheduled(
            fixedRate = 60 * 60 * 1000 / 2 / 3 // 10mint
    )
    public void checkTimeAndSend() {
        List<Seller> list = sellerRepository.findAllExpiredSellers(LocalDateTime.now(), List.of(
                SubscriptionPlanType.MONTH_1,
                SubscriptionPlanType.MONTH_2,
                SubscriptionPlanType.MONTH_3,
                SubscriptionPlanType.MONTH_6,
                SubscriptionPlanType.MONTH_12,
                SubscriptionPlanType.TRIAL
        ));
        for (Seller seller : list) {
            Long subscriptionPlanId = seller.getSubscriptionPlanId();
            Optional<SubscriptionPlan> check = subscriptionPlanRepository.findById(subscriptionPlanId);
            if (check.isEmpty()) continue;
            SubscriptionPlan subscriptionPlan = check.get();
            if (SubscriptionPlanType.ACTIVE_ALWAYS == subscriptionPlan.getName()) continue;
            if (subscriptionPlan.getName() != SubscriptionPlanType.EXPIRED) {
                SubscriptionPlan thisPlan = subscriptionPlanRepository.findByName(SubscriptionPlanType.EXPIRED).orElse(null);
                if (thisPlan != null) {
                    seller.setSubscriptionPlanId(thisPlan.getPkey());
                    seller = sellerRepository.save(seller);
                    List<BotSeller> sellers = botSellerRepository.findAllByUserId(seller.getUserid());
                    for (BotSeller botSeller : sellers) {
                        sellerBot.sendMessage(botSeller.getChatId(), sellerMsg.subscriptionExpiredMessage(botSeller.getLanguage()),
                                sellerKyb.subscriptionExpiredBtn(botSeller.getLanguage()));
                        botSeller.setEventCode(SellerEventCode.EXPIRED);
                        botSeller.setEventCodeLocation(0);
                        botSellerRepository.save(botSeller);
                    }
                }
            }
        }
    }


    // @Scheduled(fixedRate = 5 * 60 * 1000)
    // public void checkDaysLeft() {
    //     try {
    //         final String URL = "https://online-shop-bot-1.onrender.com/api/test";
    //         ResponseEntity<String> response = restTemplate.exchange(
    //                 URL,
    //                 HttpMethod.GET,
    //                 null,
    //                 String.class
    //         );
    //         String responseBody = response.getBody();
    //         System.out.println("API RESPONSE:");
    //         System.out.println(responseBody);
    //         sendMessage("%s apiga so'rov yuborildi".formatted(URL));
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // private void sendMessage(String text) {

    //     try {
    //         String url = "https://api.telegram.org/bot8144960804:AAFQAI7IAigb1pQhTEiCmA0ZUoDddu6u4NM/sendMessage";

    //         Map<String, Object> body = new HashMap<>();
    //         body.put("chat_id", 7882316826L);
    //         body.put("text", text);
    //         body.put("parse_mode", "HTML"); // ixtiyoriy

    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setContentType(MediaType.APPLICATION_JSON);

    //         HttpEntity<String> request = new HttpEntity<>(
    //                 objectMapper.writeValueAsString(body),
    //                 headers
        //     );

        //     ResponseEntity<String> response = restTemplate.exchange(
        //             url,
        //             HttpMethod.POST,
        //             request,
        //             String.class
        //     );

        //     System.out.println("Telegram response:");
        //     System.out.println(response.getBody());

        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    // }
}
