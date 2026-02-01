package uz.zafar.onlineshoptelegrambot.rest.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;
import uz.zafar.onlineshoptelegrambot.bot.msg.SellerMsg;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SellerStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.gson.IpWhoIsResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.seller.SellerRequestDto;
import uz.zafar.onlineshoptelegrambot.security.UserPrincipal;
import uz.zafar.onlineshoptelegrambot.service.GsonService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/seller/checking")
public class SellerRestController {
    private final SellerRepository sellerRepository;
    private final BotSellerRepository botSellerRepository;
    private final GsonService gsonService;
    private final TelegramBot bot;
    private final SellerMsg sellerMsg;

    public SellerRestController(SellerRepository sellerRepository,
                                BotSellerRepository botSellerRepository,
                                GsonService gsonService, @Qualifier("seller") TelegramBot bot, SellerMsg sellerMsg) {
        this.sellerRepository = sellerRepository;
        this.botSellerRepository = botSellerRepository;
        this.gsonService = gsonService;
        this.bot = bot;
        this.sellerMsg = sellerMsg;
    }

    @GetMapping("has-seller")
    public ResponseDto<Boolean> hasSeller1(@AuthenticationPrincipal UserPrincipal principal) {
        boolean success = sellerRepository.findByUserid(
                principal.getUser().getPkey()
        ).isPresent();
        ResponseDto<Boolean> response = new ResponseDto<>();
        response.setSuccess(true);
        response.setData(success);
        response.setErrorCode(ErrorCode.NONE);
        response.setMessage(new ResponseDto.Message("1", "1", "1", "1"));
        return response;
    }

    @GetMapping("get-status")
    public ResponseDto<Status> getStatus(@AuthenticationPrincipal UserPrincipal principal) {
        Seller seller = sellerRepository.findByUserid(principal.getUser().getPkey()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.ERROR);
        return ResponseDto.success(new Status(seller.getStatus()));
    }

    public static class Status {
        private SellerStatus status;

        public Status(SellerStatus status) {
            this.status = status;
        }

        public SellerStatus getStatus() {
            return status;
        }

        public void setStatus(SellerStatus status) {
            this.status = status;
        }
    }

    @PostMapping("set-user-for-telegrambot")
    public ResponseDto<?> setUserForTelegramBot(@RequestParam("chat_id") Long chatId, @AuthenticationPrincipal UserPrincipal principal, HttpServletRequest req) throws Exception {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        }
        Seller seller = sellerRepository.findByUserid(principal.getUser().getPkey()).orElse(null);
        if (seller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        }
        if (seller.getStatus() == SellerStatus.APPROVED) {
            botSeller.setUserid(principal.getUser().getPkey());
            botSeller.setConnectingToSellerAt(LocalDateTime.now());
            IpWhoIsResponseDto location = gsonService.getLocation(req).getData();
            botSeller.setIp(location.getIp());
            botSeller = botSellerRepository.save(botSeller);
            List<BotSeller> list = botSellerRepository.findAllByUserId(principal.getUser().getPkey());
            BotSeller y = botSeller;
            list = list.stream().filter(x -> !x.getPkey().equals(y.getPkey())).toList();
            if (!list.isEmpty()) {
                for (BotSeller user : list) {
                    try {
                        bot.sendMessage(user.getChatId(), sellerMsg.warningDevice(user.getLanguage(), location, botSeller));
                        bot.sendContact(user.getChatId(), botSeller.getTelegramPhone(), botSeller.getFirstName(), botSeller.getLastName());
                    } catch (Exception ignored) {

                    }
                }
            }
            return ResponseDto.success(botSeller);
        }
        return ResponseDto.error(
                ErrorCode.REJECTED
        );
    }

    private boolean bestPractice(List<BotSeller> list, BotSeller user) {
        for (BotSeller botSeller : list) {
            if (botSeller.getPkey().equals(user.getPkey())) return true;
        }
        return false;
    }

    @PostMapping("set-seller")
    public ResponseDto<?> setSellerProfile(@RequestBody SellerRequestDto request, @AuthenticationPrincipal UserPrincipal principal) {
        Seller seller = sellerRepository.findByUserid(principal.getUser().getPkey()).orElse(null);
        if (seller != null) {
            return ResponseDto.error(ErrorCode.SELLER_ALREADY_EXISTS);
        }
        seller = new Seller();
        seller.setGender(request.getGender());
        seller.setPhone(request.getPhone());
        seller.setCardNumber(request.getCardNumber());
        seller.setCardImageUrl(request.getCardImageUrl());
        seller.setCardImageName(request.getCardImageName());
        seller.setCardImageSize(request.getCardImageSize());
        seller.setCardType(request.getCardType());
        seller.setBalance(BigDecimal.ZERO);
        seller.setCardOwner(request.getCardOwner());
        seller.setStatus(SellerStatus.PENDING);
        seller.setUserid(principal.getUser().getPkey());
        seller.setCardAddedTime(LocalDateTime.now());
        seller = sellerRepository.save(seller);
        //sender admin
        return ResponseDto.success(seller);
    }

}
