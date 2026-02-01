package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;
import uz.zafar.onlineshoptelegrambot.db.entity.ads.Ads;
import uz.zafar.onlineshoptelegrambot.db.entity.ads.AdsButton;
import uz.zafar.onlineshoptelegrambot.db.entity.ads.AdsButtonResponse;
import uz.zafar.onlineshoptelegrambot.db.entity.ads.AdsResponse;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.repositories.AdsButtonRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.AdsRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.rest.admin.dto.SendStat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/admin/ads")
public class AdminAdsResController {
    private final AdsRepository adsRepository;
    private final AdsButtonRepository adsButtonRepository;
    private final TelegramBot sellerBot;
    private final TelegramBot customerBot;
    private final BotCustomerRepository botCustomerRepository;
    private final BotSellerRepository botSellerRepository;

    public AdminAdsResController(AdsRepository adsRepository,
                                 AdsButtonRepository adsButtonRepository,
                                 @Qualifier("seller") TelegramBot sellerBot,
                                 @Qualifier("customer") TelegramBot customerBot,
                                 BotCustomerRepository botCustomerRepository,
                                 BotSellerRepository botSellerRepository) {
        this.adsRepository = adsRepository;
        this.adsButtonRepository = adsButtonRepository;
        this.sellerBot = sellerBot;
        this.customerBot = customerBot;
        this.botCustomerRepository = botCustomerRepository;
        this.botSellerRepository = botSellerRepository;
    }

    @GetMapping("list")
    public ResponseDto<List<AdsResponse>> list() {
        return ResponseDto.success(toList(adsRepository.list()));
    }

    @DeleteMapping("delete/{adsId}")
    @Transactional
    public ResponseDto<?> deleteById(@PathVariable("adsId") UUID adsId) {
        Ads ads = adsRepository.findById(adsId).orElse(null);
        if (ads == null) return ResponseDto.error(ErrorCode.ERROR);
        adsRepository.delete(ads);
        return ResponseDto.success();
    }

    @PutMapping("edit/{adsId}")
    @Transactional
    public ResponseDto<AdsResponse> editAds(@PathVariable("adsId") UUID adsId, @RequestBody CreateAds req) {
        Ads ads = adsRepository.findById(adsId).orElse(null);
        if (ads == null) return ResponseDto.error(ErrorCode.ERROR);
        ads.setPhotoUrl(req.photoUrl);
        ads.setHasPhoto(req.hasPhoto);
        ads.setTitle(req.title);
        ads.setDescription(req.description);
        for (AdsButton adsButton : adsButtonRepository.findAllByAdsId(adsId)) {
            adsButtonRepository.delete(adsButton);
        }
        for (AdsButtonResponse button : req.buttons) {
            AdsButton adsButton = new AdsButton();
            adsButton.setUrl(button.getUrl());
            adsButton.setViewText(button.getViewText());
            adsButton.setAdsId(adsId);
            adsButtonRepository.save(adsButton);
        }
        return ResponseDto.success(toResponse(adsRepository.save(ads)));
    }

    @PostMapping("create")
    public ResponseDto<AdsResponse> create(@RequestBody CreateAds req) {
        Ads ads = new Ads();
        ads.setTitle(req.title);
        ads.setDescription(req.description);
        ads.setHasPhoto(req.hasPhoto);
        ads.setPhotoUrl(req.photoUrl);
        ads = adsRepository.save(ads);
        for (AdsButtonResponse button : req.buttons) {
            AdsButton adsButton = new AdsButton();
            adsButton.setUrl(button.getUrl());
            adsButton.setViewText(button.getViewText());
            adsButton.setAdsId(ads.getPkey());
            adsButtonRepository.save(adsButton);
        }
        ads.setCreated(LocalDateTime.now());
        return ResponseDto.success(toResponse(adsRepository.save(ads)));
    }


    @PutMapping("send/{type}/{adsId}")
    public ResponseDto<?> send(
            @PathVariable("type") SendType type,
            @PathVariable("adsId") UUID adsId
    ) {
        Ads ads = adsRepository.findById(adsId).orElse(null);
        if (ads == null) {
            return ResponseDto.error(ErrorCode.ERROR);
        }

        AdsResponse response = toResponse(ads);
        InlineKeyboardMarkup markup =
                response.getButtons().isEmpty() ? new InlineKeyboardMarkup() : markup(response.getButtons());
        SendStat customerStat = new SendStat();
        SendStat sellerStat = new SendStat();

        switch (type) {
            case ALL -> {
                sendToCustomers(response, markup, customerStat);
                sendToSellers(response, markup, sellerStat);
            }
            case CUSTOMER -> sendToCustomers(response, markup, customerStat);
            case SELLER -> sendToSellers(response, markup, sellerStat);
        }

        return ResponseDto.success(
                new Object() {
                    public final SendStat customers = customerStat;
                    public final SendStat sellers = sellerStat;
                }
        );
    }

    private void sendToCustomers(
            AdsResponse response,
            InlineKeyboardMarkup markup,
            SendStat stat
    ) {
        for (BotCustomer customer : botCustomerRepository.findAll()) {
            stat.incTotal();
            try {
                boolean send;
                if (response.getHasPhoto()) {
                    send = customerBot.sendPhoto(
                            customer.getChatId(),
                            response.getPhotoUrl(),
                            getCaption(response.getTitle(), response.getDescription()),
                            markup
                    );
                } else {
                    send = customerBot.sendMessage1(
                            customer.getChatId(),
                            getCaption(response.getTitle(), response.getDescription()),
                            markup
                    );
                }
                if (send) stat.incSuccess();
                else stat.incFailed();
            } catch (Exception e) {
                stat.incFailed();
            }
        }
    }


    private void sendToSellers(
            AdsResponse response,
            InlineKeyboardMarkup markup,
            SendStat stat
    ) {
        botSellerRepository.findAll().forEach(seller -> {
            stat.incTotal();
            try {
                boolean send;
                if (response.getHasPhoto()) {
                    send = sellerBot.sendPhoto(
                            seller.getChatId(),
                            response.getPhotoUrl(),
                            getCaption(response.getTitle(), response.getDescription()),
                            markup
                    );
                } else {
                    send = sellerBot.sendMessage1(
                            seller.getChatId(),
                            getCaption(response.getTitle(), response.getDescription()),
                            markup
                    );
                }
                if (send) stat.incSuccess();
                else stat.incFailed();
            } catch (Exception e) {
                stat.incFailed();
            }
        });
    }


    private String getCaption(String title, String description) {
        StringBuilder caption = new StringBuilder();

        if (title != null && !title.isBlank()) {
            caption.append("<b>")
                    .append(title)
                    .append("</b>\n\n");
        }

        if (description != null && !description.isBlank()) {
            caption.append("<i>")
                    .append(description)
                    .append("</i>");
        }

        return caption.toString();
    }

    public enum SendType {
        ALL, SELLER, CUSTOMER
    }


    public static class CreateAds {
        private String title;
        private String description;
        private Boolean hasPhoto;
        private String photoUrl;
        private List<AdsButtonResponse> buttons;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getHasPhoto() {
            return hasPhoto;
        }

        public void setHasPhoto(Boolean hasPhoto) {
            this.hasPhoto = hasPhoto;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public List<AdsButtonResponse> getButtons() {
            return buttons;
        }

        public void setButtons(List<AdsButtonResponse> buttons) {
            this.buttons = buttons;
        }
    }

    private AdsResponse toResponse(Ads ads) {
        List<AdsButtonResponse> list = new ArrayList<>();
        for (AdsButton adsButton : adsButtonRepository.findAllByAdsId(ads.getPkey())) {
            list.add(new AdsButtonResponse(adsButton.getUrl(), adsButton.getViewText()));
        }
        return new AdsResponse(
                ads.getPkey(), ads.getTitle(), ads.getDescription(),
                ads.getHasPhoto(), ads.getPhotoUrl(), list, ads.getCreated()
        );
    }

    private List<AdsResponse> toList(List<Ads> adsList) {
        return adsList.stream()
                .map(this::toResponse)
                .toList();
    }

    private InlineKeyboardMarkup markup(List<AdsButtonResponse> list) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (AdsButtonResponse adsButtonResponse : list) {
            button = new InlineKeyboardButton();
            button.setText(adsButtonResponse.getViewText());
            button.setUrl(adsButtonResponse.getUrl());
            row.add(button);
            rows.add(row);
            row = new ArrayList<>();
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }
}
