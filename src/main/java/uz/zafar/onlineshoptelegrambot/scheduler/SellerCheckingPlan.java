package uz.zafar.onlineshoptelegrambot.scheduler;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;
import uz.zafar.onlineshoptelegrambot.bot.kyb.seller.SellerKyb;
import uz.zafar.onlineshoptelegrambot.bot.msg.SellerMsg;
import uz.zafar.onlineshoptelegrambot.bot.msg.UserMsg;
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


    @Scheduled(
            fixedRate = 24 * 60 * 60 * 1000, // 24 soat
            initialDelay = 0
    )
    public void checkDaysLeft() {

    }
}
