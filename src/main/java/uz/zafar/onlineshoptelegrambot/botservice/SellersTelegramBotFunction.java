package uz.zafar.onlineshoptelegrambot.botservice;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;
import uz.zafar.onlineshoptelegrambot.bot.kyb.seller.SellerButton;
import uz.zafar.onlineshoptelegrambot.bot.kyb.seller.SellerKyb;
import uz.zafar.onlineshoptelegrambot.bot.msg.SellerMsg;
import uz.zafar.onlineshoptelegrambot.config.TelegramProperties;
import uz.zafar.onlineshoptelegrambot.db.entity.AdminCard;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.enums.SellerEventCode;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;
import uz.zafar.onlineshoptelegrambot.db.entity.common.Discount;
import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.contact.ContactWe;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.*;
import uz.zafar.onlineshoptelegrambot.db.entity.payment.Payment;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.db.repositories.*;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeImageRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.TelegramUpdateData;
import uz.zafar.onlineshoptelegrambot.dto.bot.kyb.Button;
import uz.zafar.onlineshoptelegrambot.mapper.ProductTypeMapper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellersTelegramBotFunction {
    private final BotSellerRepository sellerRepository;
    private final TelegramBot bot;
    private final SellerMsg msg;
    private final SellerKyb kyb;
    private final SellerButton sellerButton;
    private final String confirmSellerWebbAppUrl;
    private final String shopWebbAppUrl;
    private final UserRepository userRepository;
    private final SellerRepository sellerUserRep;
    private final String sellerProductWebappUrl;
    private final ShopRepository shopRepository;
    private final SellerRepository sellerRepository1;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final ProductTypeMapper productTypeMapper;
    private final TelegramProperties telegramProperties;
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeImageRepository productTypeImageRepository;
    private final AdminCardRepository adminCardRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ContactWeRepository contactWeRepository;

    public SellersTelegramBotFunction(BotSellerRepository sellerRepository, @Qualifier("seller") TelegramBot bot,
                                      SellerMsg msg, SellerKyb kyb,
                                      SellerButton sellerButton,
                                      @Value("${telegram.app.seller.confirm.webapp.url}")
                                      String confirmSellerWebbAppUrl,
                                      @Value("${telegram.app.seller.shop.webapp.url}") String shopWebbAppUrl,
                                      UserRepository userRepository, SellerRepository sellerUserRep,
                                      @Value("${telegram.app.seller.product.webapp.url}") String sellerProductWebappUrl, ShopRepository shopRepository, SellerRepository sellerRepository1, ProductRepository productRepository, DiscountRepository discountRepository, ProductTypeMapper productTypeMapper, TelegramProperties telegramProperties, ProductTypeRepository productTypeRepository, ProductTypeImageRepository productTypeImageRepository, AdminCardRepository adminCardRepository, PaymentRepository paymentRepository, SubscriptionPlanRepository subscriptionPlanRepository, ContactWeRepository contactWeRepository) {
        this.sellerRepository = sellerRepository;
        this.bot = bot;
        this.msg = msg;
        this.kyb = kyb;
        this.sellerButton = sellerButton;
        this.confirmSellerWebbAppUrl = confirmSellerWebbAppUrl;
        this.shopWebbAppUrl = shopWebbAppUrl;
        this.userRepository = userRepository;
        this.sellerUserRep = sellerUserRep;
        this.sellerProductWebappUrl = sellerProductWebappUrl;
        this.shopRepository = shopRepository;
        this.sellerRepository1 = sellerRepository1;
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
        this.productTypeMapper = productTypeMapper;
        this.telegramProperties = telegramProperties;
        this.productTypeRepository = productTypeRepository;
        this.productTypeImageRepository = productTypeImageRepository;
        this.adminCardRepository = adminCardRepository;
        this.paymentRepository = paymentRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.contactWeRepository = contactWeRepository;
    }


    public void start(Long chatId, String firstName, String lastName, String username) {
        BotSeller user = refreshUser(chatId, firstName, lastName, username);

        if (user.getLanguage() == null) {
            String fullName = firstName + (lastName == null ? "" : (" " + lastName));
            bot.sendMessage(user.getChatId(), msg.welcomeCyr(fullName), kyb.requestLang());
            user = eventCode(chatId, SellerEventCode.VERIFY);
            return;
        }

        if (user.getTelegramPhone() == null) {
            bot.sendMessage(user.getChatId(), msg.shareContact(user.getLanguage()), kyb.shareContact(user.getLanguage()));
            user = eventCodeLocation(chatId, 1);
            return;
        }
        if (user.getUserid() == null) {
            user = eventCodeLocation(chatId, 1);
            bot.sendMessage(
                    user.getChatId(), msg.requestSellerLogin(user.getLanguage()),
                    kyb.requestSellerLoginAndChangeLanguage(user.getLanguage(),
                            confirmSellerWebbAppUrl + "/" + user.getChatId()));
            return;
        }
        User currentUser = userRepository.findById(user.getUserid()).orElse(null);
        if (currentUser == null) return;
        Seller seller = sellerUserRep.findByUserid(currentUser.getPkey()).orElse(null);
        if (seller == null) {
            bot.sendMessage(chatId, msg.rejectedMsg(user.getLanguage()), kyb.requestSellerLoginAndChangeLanguage1(
                    user.getLanguage(), confirmSellerWebbAppUrl + "/" + user.getChatId()
            ));
            return;
        }
        if (seller.getStatus() == SellerStatus.REJECTED) {
            bot.sendMessage(chatId, msg.rejectedMsg(user.getLanguage()), kyb.requestSellerLoginAndChangeLanguage1(
                    user.getLanguage(), confirmSellerWebbAppUrl + "/" + user.getChatId()
            ));
            return;
        }
        if (seller.getStatus() == SellerStatus.APPROVED) {
            Seller checkSeller = sellerRepository1.checkActivePlanSeller(seller.getPkey()).orElse(null);
            if (checkSeller == null) {
                bot.sendMessage(user.getChatId(), msg.subscriptionExpiredMessage(user.getLanguage()), kyb.subscriptionExpiredBtn(user.getLanguage()));
                eventCode(chatId, SellerEventCode.EXPIRED);
                return;
            } else {
                user = eventCode(chatId, SellerEventCode.MENU);
                bot.sendMessage(user.getChatId(), msg.menu(user.getLanguage()),
                        kyb.menu(user.getLanguage(), shopWebbAppUrl + "/" + chatId, chatId));
                return;
            }

        }
    }

    public BotSeller getUser(Long chatId) {
        return sellerRepository.checkUser(chatId).orElse(null);
    }

    public BotSeller refreshUser(Long chatId, String firstName, String lastName, String username) {
        BotSeller user = getUser(chatId);
        if (user == null) {
            user = new BotSeller();
            user.setChatId(chatId);
        }
        user.setFirstName(firstName.length() >= 30 ? firstName.substring(0, 30) : firstName);
        user.setLastName(lastName == null ? null : (lastName.length() >= 30 ? lastName.substring(0, 30) : lastName));
        user.setUsername(username);
        user = sellerRepository.save(user);
        return user;
    }

    private BotSeller eventCode(Long chatId, SellerEventCode eventCode) {
        BotSeller user = getUser(chatId);
        user.setEventCode(eventCode);
        user.setEventCodeLocation(0);
        return sellerRepository.save(user);
    }

    private BotSeller eventCodeLocation(Long chatId, int i) {
        BotSeller user = getUser(chatId);
        user.setEventCodeLocation(i);
        return sellerRepository.save(user);
    }

    public void verify(BotSeller user, String text) {
        List<String> buttons;
        if (user.getEventCodeLocation() == 0) {
            buttons = sellerButton.requestLang().stream().map(Button::getText).toList();
            Language language;
            if (text.equals(buttons.get(0))) {
                language = Language.UZBEK;
            } else if (text.equals(buttons.get(1))) {
                language = Language.CYRILLIC;
            } else if (text.equals(buttons.get(2))) {
                language = Language.RUSSIAN;
            } else if (text.equals(buttons.get(3))) {
                language = Language.ENGLISH;
            } else {
                user.setLanguage(Language.CYRILLIC);
                wrongBtn(user, kyb.requestLang());
                return;
            }
            user.setLanguage(language);
            user = sellerRepository.save(user);
            bot.sendMessage(user.getChatId(), msg.changedLanguage(user.getLanguage()));
            start(user.getChatId(), user.getFirstName(), user.getLastName(), user.getFirstName());
            return;
        }
        if (user.getEventCodeLocation() == 1) {
            buttons = sellerButton.changeLanguageAndShareContact(user.getLanguage()).stream().map(Button::getText).toList();
            if (text.equals(buttons.get(1))) {
                bot.sendMessage(user.getChatId(), msg.changeLanguage(user.getLanguage()), kyb.requestLang());
                eventCodeLocation(user.getChatId(), 0);
            } else wrongBtn(user, kyb.shareContact(user.getLanguage()));
            return;
        }
    }

    private void wrongBtn(BotSeller user, ReplyKeyboardMarkup markup) {
        bot.sendMessage(user.getChatId(), msg.wrongBtn(user.getLanguage()), markup);
    }

    public void verify(BotSeller user, String phoneNumber, Long contactUserId) {
        if (user.getEventCodeLocation() == 1) {
            if (contactUserId.equals(user.getChatId())) {
                if (phoneNumber.charAt(0) != '+') phoneNumber = ('+' + phoneNumber);
                user.setTelegramPhone(phoneNumber);
                user = sellerRepository.save(user);
                bot.sendMessage(user.getChatId(), msg.savedContact(user.getLanguage()));
                start(user.getChatId(), user.getFirstName(), user.getLastName(), user.getUsername());
            } else wrongBtn(user, kyb.shareContact(user.getLanguage()));
        }
    }

    public void example(Long chatId, String categoryId, String productId, String typeId) {
        bot.sendMessage(chatId, """
                category id: %s
                product id: %s
                type id: %s
                """.formatted(categoryId, productId, typeId));
    }

    public void menu(BotSeller user, String text) throws Exception {
        Integer location = user.getEventCodeLocation();
        List<String> buttons = sellerButton.menu(user.getLanguage(), shopWebbAppUrl + "/" + user.getChatId(), user.getChatId()).stream().map(Button::getText).toList();
        if (location == 0) {
            if (text.equals(buttons.get(1))) {
                Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
                if (seller == null) return;
                if (shopRepository.getShopByChatId(seller.getPkey()).isEmpty()) {
                    bot.sendMessage(user.getChatId(), msg.requestAddShopPlease(user.getLanguage()), kyb.notShops(
                            user.getLanguage(),
                            shopWebbAppUrl + "/" + user.getChatId()
                    ));
                    return;
                }
                bot.sendMessage(user.getChatId(),sellerProductWebappUrl + "/" + user.getChatId());
                List<Product> products = getAllProducts(user);
                bot.sendMessage(user.getChatId(),
                        msg.yourProductLists(user.getLanguage()),
                        kyb.yourProductLists(user.getLanguage(), products, sellerProductWebappUrl + "/" + user.getChatId(), null));

            } else if (text.equals(buttons.get(2))) {
            } else if (text.equals(buttons.get(3))) {
                List<BotSeller> users = sellerRepository.findAllByUserId(user.getUserid());
                sendLongMessage(user.getChatId(), msg.myDevices(
                        users, user.getLanguage(), user.getChatId()
                ), kyb.logoutAllBtn(user.getLanguage()));
                eventCode(user.getChatId(), SellerEventCode.DEVICES);
            } else if (text.equals(buttons.get(4))) {
                Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
                if (seller == null)
                    return;
                bot.sendMessage(user.getChatId(), msg.myBalance(user.getLanguage(), seller, false, subscriptionPlanRepository.findById(seller.getSubscriptionPlanId()).orElse(null)), kyb.topUpAccount(user.getLanguage()));
            } else if (text.equals(buttons.get(8))) {
                bot.sendMessage(user.getChatId(), text, kyb.settings(user.getLanguage()));
                user.setEventCodeLocation(20);
                sellerRepository.save(user);
            } else if (text.equals(buttons.get(9))) {
                bot.sendMessage(user.getChatId(), msg.help(user.getLanguage()), kyb.backBtn(user.getLanguage()));
                for (ContactWe we : contactWeRepository.allContacts()) {
                    bot.sendContact(
                            user.getChatId(), we.getPhone(), we.getFullName(), null
                    );
                }
            } else if (text.equals(buttons.get(10))) {
                bot.sendMessage(user.getChatId(), msg.confirmLogout(user.getLanguage()), kyb.confirmLogout(user.getLanguage()));
                user.setEventCodeLocation(1234);
                sellerRepository.save(user);
            } else {
                if (text.equals(sellerButton.backBtn(user.getLanguage()).get(0).getText())) {
                    bot.sendMessage(user.getChatId(), text, kyb.menu(user.getLanguage(), shopWebbAppUrl + "/" + user.getChatId(), user.getChatId()));
                    return;
                }
                bot.sendMessage(user.getChatId(), msg.wrongBtn(user.getLanguage()), kyb.menu(user.getLanguage(), shopWebbAppUrl + "/" + user.getChatId(), user.getChatId()));
            }
            return;

        }
        if (location == 1234) {
            boolean inArray = false;
            String[] s = {"✅ Ha", "✅ Ҳа", "✅ Да", "✅ Yes"};
            for (String string : s) {
                if (text.equals(string)) {
                    inArray = true;
                    break;
                }
            }
            if (inArray) {
                user.setUserid(null);
                sellerRepository.save(user);
                bot.sendMessage(user.getChatId(),msg.successfullyLogout(user.getLanguage()));
            }
            start(user);
            return;
        }
        if (location == 1) {
            List<Button> list = sellerButton.payment(user.getLanguage());
            buttons = list.stream()
                    .map(Button::getText)
                    .toList();
            if (text.equals(buttons.get(1))) {
                start(user);
            } else {
                if (text.equals(buttons.get(0))) {
                    bot.sendMessage(user.getChatId(), msg.getReceiptNoteForMenu(user.getLanguage()), kyb.backBtn(user.getLanguage()));
                    user.setEventCodeLocation(2);
                    sellerRepository.save(user);
                } else {
                    bot.sendPhoto(
                            user.getChatId(), adminCardRepository.findAll().get(0).getImgUrl(),
                            msg.paymentInformation(user.getLanguage(), adminCardRepository.findAll().get(0))
                                    + "\n\n" + msg.wrongBtn(user.getLanguage()),
                            kyb.paymentBtn(user.getLanguage()));
                }
            }
            return;
        }

        if (location == 2) {
            if (text.equals(sellerButton.backBtn(user.getLanguage()).get(0).getText())) {
                bot.sendPhoto(user.getChatId(), adminCardRepository.findAll().get(0).getImgUrl(), msg.paymentInformation(
                        user.getLanguage(), adminCardRepository.findAll().get(0)
                ), kyb.paymentBtn(user.getLanguage()));
                user.setEventCodeLocation(1);
                sellerRepository.save(user);
            } else {
                bot.sendMessage(
                        user.getChatId(), msg.wrongBtn(user.getLanguage()),
                        kyb.backBtn(user.getLanguage()));
                return;
            }
        }
        Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
        if (seller == null) return;
        if (location == 20) {
            List<String> settings = sellerButton.settings(user.getLanguage()).stream()
                    .map(Button::getText)
                    .toList();
            if (text.equals(settings.get(0))) {
                bot.sendMessage(user.getChatId(), msg.changeLanguage(user.getLanguage()), kyb.requestLang());
                return;
            } else if (text.equals(settings.get(1))) {
                bot.sendMessage(user.getChatId(), msg.sellerProfileInformation(user.getLanguage(), seller, Objects.requireNonNull(userRepository.findById(seller.getUserid()).orElse(null))), kyb.changeProfile(user.getLanguage()));
                user.setEventCodeLocation(21);
                sellerRepository.save(user);
                return;
            } else if (text.equals(settings.get(2))) {
                bot.sendPhoto(user.getChatId(), seller.getCardImageUrl(), msg.cardInfo(user.getLanguage(), seller), kyb.cardEdit(user.getLanguage()));
                user.setEventCodeLocation(24);
                sellerRepository.save(user);
                return;
            } else if (text.equals(settings.get(3))) {
                SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(seller.getSubscriptionPlanId()).orElse(null);
                if (subscriptionPlan == null)
                    return;
                bot.sendMessage(user.getChatId(), msg.subscriptionPlanMsg(user.getLanguage()), kyb.selectSubscriptionPlans(
                        user.getLanguage(), List.of(
                                SubscriptionPlanType.MONTH_1,
                                SubscriptionPlanType.MONTH_2,
                                SubscriptionPlanType.MONTH_3,
                                SubscriptionPlanType.MONTH_6,
                                SubscriptionPlanType.MONTH_12,
                                SubscriptionPlanType.ACTIVE_ALWAYS
                        )
                ));
                user.setEventCodeLocation(100);
                sellerRepository.save(user);
            } else if (text.equals(settings.get(4))) {
                start(user);
                return;
            } else {
                List<String> langs = sellerButton.requestLang().stream()
                        .map(Button::getText)
                        .toList();
                if (text.equals(langs.get(0))) {
                    user.setLanguage(Language.UZBEK);
                } else if (text.equals(langs.get(1))) {
                    user.setLanguage(Language.CYRILLIC);
                } else if (text.equals(langs.get(2))) {
                    user.setLanguage(Language.RUSSIAN);
                } else if (text.equals(langs.get(3))) {
                    user.setLanguage(Language.ENGLISH);
                } else {
                    return;
                }
                user.setEventCodeLocation(0);
                bot.sendMessage(user.getChatId(), msg.changedLanguage(user.getLanguage()));
                menu(user, sellerButton.menu(user.getLanguage(), null, null).get(8).getText());
                sellerRepository.save(user);
                return;
            }
        }

        if (location == 21) {
            buttons = sellerButton.changeProfile(user.getLanguage()).stream()
                    .map(Button::getText)
                    .toList();
            if (text.equals(buttons.get(0))) {
                bot.sendMessage(user.getChatId(), msg.editPhone(user.getLanguage(), seller.getPhone()), kyb.back(user.getLanguage()));
                user.setEventCodeLocation(22);
                sellerRepository.save(user);
                return;
            }
            if (text.equals(buttons.get(1))) {
                bot.sendMessage(user.getChatId(), msg.editFullName(user.getLanguage(), userRepository.findById(seller.getUserid()).orElse(null)), kyb.back(user.getLanguage()));
                user.setEventCodeLocation(23);
                sellerRepository.save(user);
                return;
            }
            if (text.equals(buttons.get(2))) {
                user.setEventCodeLocation(0);
                text = sellerButton.menu(user.getLanguage(), null, null).get(8).getText();
                menu(user, text);

                return;
            }
            bot.sendMessage(user.getChatId(), msg.wrongBtn(user.getLanguage()), kyb.changeProfile(user.getLanguage()));
            return;
        }

        if (location == 100) {
            buttons = sellerButton.selectSubscriptionPlans(user.getLanguage(), List.of(
                            SubscriptionPlanType.MONTH_1,
                            SubscriptionPlanType.MONTH_2,
                            SubscriptionPlanType.MONTH_3,
                            SubscriptionPlanType.MONTH_6,
                            SubscriptionPlanType.MONTH_12,
                            SubscriptionPlanType.ACTIVE_ALWAYS
                    )).stream()
                    .map(Button::getText).toList();
            if (sellerButton.backBtn(user.getLanguage()).get(0).getText().equals(text)) {
                user.setEventCodeLocation(0);
                text = sellerButton.menu(user.getLanguage(), null, null).get(8).getText();
                menu(user, text);
                return;
            }
            SubscriptionPlanType planType;
            if (text.equals(buttons.get(0))) planType = SubscriptionPlanType.MONTH_1;
            else if (text.equals(buttons.get(1))) planType = SubscriptionPlanType.MONTH_2;
            else if (text.equals(buttons.get(2))) planType = SubscriptionPlanType.MONTH_3;
            else if (text.equals(buttons.get(3))) planType = SubscriptionPlanType.MONTH_6;
            else if (text.equals(buttons.get(4))) planType = SubscriptionPlanType.MONTH_12;
            else if (text.equals(buttons.get(5))) planType = SubscriptionPlanType.ACTIVE_ALWAYS;
            else {
                wrongBtn(user, kyb.selectSubscriptionPlans(user.getLanguage(), List.of(
                        SubscriptionPlanType.MONTH_1,
                        SubscriptionPlanType.MONTH_2,
                        SubscriptionPlanType.MONTH_3,
                        SubscriptionPlanType.MONTH_6,
                        SubscriptionPlanType.MONTH_12,
                        SubscriptionPlanType.ACTIVE_ALWAYS
                )));
                return;
            }
            SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findByName(planType).orElse(null);
            if (subscriptionPlan == null)
                return;
            bot.sendMessage(user.getChatId(), msg.subscriptionInformation(user.getLanguage(), subscriptionPlan,
                    discountRepository.getSubscriptionPlan(subscriptionPlan.getPkey()).orElse(null)), kyb.planSubscription(user.getLanguage(), subscriptionPlan.getPkey()));
        }
        if (location == 22 || location == 23) {
            if (text.equals(sellerButton.back(user.getLanguage()).get(0).getText())) {
                user.setEventCodeLocation(20);
                menu(user, sellerButton.settings(user.getLanguage()).get(1).getText());
                sellerRepository.save(user);
                return;
            }
            if (location == 22) {
                seller.setPhone(text);
                sellerRepository1.save(seller);
            }
            if (location == 23) {
                User currentUser = userRepository.findById(seller.getUserid()).orElse(null);
                if (currentUser == null)
                    return;
                currentUser.setFullName(text);
                userRepository.save(currentUser);
            }
            bot.sendMessage(user.getChatId(), msg.changed(user.getLanguage()));
            user.setEventCodeLocation(20);
            menu(user, sellerButton.settings(user.getLanguage()).get(1).getText());
            sellerRepository.save(user);
            return;
        }

        if (location == 24) {
            buttons = sellerButton.editCard(user.getLanguage()).stream()
                    .map(Button::getText)
                    .toList();
            if (text.equals(buttons.get(0))) {
                bot.sendMessage(user.getChatId(), msg.editingCardPhotoMsg(user.getLanguage()), kyb.back(user.getLanguage()));
                return;
            }
            if (text.equals(buttons.get(1))) {
                bot.sendMessage(user.getChatId(), msg.editingCardOwner(user.getLanguage(), seller.getCardOwner()), kyb.back(user.getLanguage()));
                user.setEventCodeLocation(25);
                sellerRepository.save(user);
                return;
            }
            if (text.equals(buttons.get(2))) {
                bot.sendMessage(user.getChatId(), msg.editingCardNumber(user.getLanguage()), kyb.back(user.getLanguage()));
                user.setEventCodeLocation(26);
                sellerRepository.save(user);
                return;
            }
            if (text.equals(buttons.get(3))) {
                user.setEventCodeLocation(0);
                text = sellerButton.menu(user.getLanguage(), null, null).get(8).getText();
                menu(user, text);
                return;
            }
            return;
        }

        if (location == 25 || 26 == location) {
            if (text.equals(sellerButton.back(user.getLanguage()).get(0).getText())) {
                bot.sendPhoto(user.getChatId(), seller.getCardImageUrl(), msg.cardInfo(user.getLanguage(), seller), kyb.cardEdit(user.getLanguage()));
                user.setEventCodeLocation(24);
                sellerRepository.save(user);
                return;
            }
            if (location == 25) {
                seller.setCardOwner(text);
            } else {
                seller.setCardNumber(text);
            }
            sellerRepository1.save(seller);
            bot.sendMessage(user.getChatId(), msg.changed(user.getLanguage()));
            bot.sendPhoto(user.getChatId(), seller.getCardImageUrl(), msg.cardInfo(user.getLanguage(), seller), kyb.cardEdit(user.getLanguage()));
            user.setEventCodeLocation(24);
            sellerRepository.save(user);
        }
    }

    public void menu(BotSeller user, TelegramUpdateData updateData) throws Exception {
        String data = updateData.getCallbackData();
        String callbackQueryId = updateData.getCallbackQueryId();
        Integer location = user.getEventCodeLocation();
        if (location == 0) {
            if (data.equals("TOP_UP")) {
                bot.answerCallbackQuery(callbackQueryId, "✅ Ok", false);
                bot.deleteMessage(user.getChatId(), updateData.getMessageId());
                bot.sendPhoto(user.getChatId(), adminCardRepository.findAll().get(0).getImgUrl(), msg.paymentInformation(
                        user.getLanguage(), adminCardRepository.findAll().get(0)
                ), kyb.paymentBtn(user.getLanguage()));
                user.setEventCodeLocation(1);
                sellerRepository.save(user);
                return;
            }
            if (data.startsWith("PAGE")) {
                bot.sendMessage(user.getChatId(),sellerProductWebappUrl + "/" + user.getChatId());
                bot.editMessageText(user.getChatId(), updateData.getMessageId(), msg.yourProductLists(user.getLanguage()), kyb.yourProductLists(
                        user.getLanguage(),
                        getAllProducts(user),
                        sellerProductWebappUrl + "/" + user.getChatId(),
                        data
                ));ds
                return;
            }
            if (data.startsWith("PRODUCT_")) {
                String imgUrl = "error";
                UUID productId = UUID.fromString(data.split("_")[1]);
                Product product = productRepository.findById(productId).orElse(null);
                if (product == null) {
                    bot.answerCallbackQuery(callbackQueryId, msg.notFoundProductMsg(user.getLanguage()), false);
                    return;
                }
                ProductType firstType = getFirstType(product);
                ProductTypeImage mainImage = getMainImageType(firstType);
                bot.deleteMessage(user.getChatId(), updateData.getMessageId());
                if (!bot.sendPhoto(user.getChatId(), mainImage.getImageUrl(), msg.aboutProduct(
                        user.getLanguage(), product, getAllProductTypes(product), getUsersBotSeenThisProduct(product)
                ), kyb.aboutProduct(user.getLanguage(), getAllProductTypes(product), firstType, mainImage))) {
                    bot.sendMessage(user.getChatId(), msg.wrongProductPhoto(user.getLanguage()) + "\n\n" + msg.aboutProduct(
                            user.getLanguage(), product, getAllProductTypes(product), getUsersBotSeenThisProduct(product)
                    ));
                }
            } else if (data.startsWith("PRODUCTTYPE_")) {
                UUID productTypeId = UUID.fromString(data.split("_")[1]);
                ProductType productType = productTypeRepository.findById(productTypeId).orElse(null);
                if (productType == null) return;
                bot.editMessagePhoto(user.getChatId(), updateData.getMessageId(), getMainImageType(productType).getImageUrl(), msg.aboutProduct(
                        user.getLanguage(), productType.getProduct(), getAllProductTypes(productType.getProduct()), getUsersBotSeenThisProduct(productType.getProduct())
                ), kyb.aboutProduct(user.getLanguage(), getAllProductTypes(productType.getProduct()), productType, getMainImageType(productType)));
            } else if (data.startsWith("IMG_")) {
                UUID imageId = UUID.fromString(data.split("_")[1]);
                ProductTypeImage image = productTypeImageRepository.findById(imageId).orElse(null);
                if (image == null) return;
                ProductType productType = image.getProductType();
                bot.editMessagePhoto(user.getChatId(), updateData.getMessageId(), image.getImageUrl(), msg.aboutProduct(
                        user.getLanguage(), productType.getProduct(), getAllProductTypes(productType.getProduct()), getUsersBotSeenThisProduct(productType.getProduct())
                ), kyb.aboutProduct(user.getLanguage(), getAllProductTypes(productType.getProduct()), productType, image));
            } else if (data.equals("BACK_TO_PRODUCTS")) {
                bot.deleteMessage(user.getChatId(), updateData.getMessageId());
                this.menu(user, sellerButton.menu(user.getLanguage(), null, user.getChatId()).get(1).getText());
            }
        }

        if (location == 100) {
            bot.answerCallbackQuery(updateData.getCallbackQueryId(), "Ok", false);
            data = updateData.getCallbackData();
            if (data.equals("TOP_UP")) {
                bot.answerCallbackQuery(callbackQueryId, "✅ Ok", false);
                bot.deleteMessage(user.getChatId(), updateData.getMessageId());
                Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
                if (seller == null)
                    return;
                bot.sendPhoto(user.getChatId(), adminCardRepository.findAll().get(0).getImgUrl(), msg.paymentInformation(
                        user.getLanguage(), adminCardRepository.findAll().get(0)
                ), kyb.paymentBtn(user.getLanguage()));
                user.setEventCodeLocation(1);
                sellerRepository.save(user);
                return;
            }
            Long subscriptionPlanId = Long.valueOf(data.split(":")[1]);
            SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(subscriptionPlanId).orElse(null);
            if (subscriptionPlan == null)
                return;
            Discount discount = discountRepository.getSubscriptionPlan(subscriptionPlan.getPkey()).orElse(null);
            if (discount != null && subscriptionPlan.isDiscount()) {
                Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
                if (seller == null) {
                    bot.sendMessage(user.getChatId(), msg.failedCheckCreatingImage(user.getLanguage()), true);
                    return;
                }
                Payment payment = new Payment();
                payment.setStatus(PaymentStatus.CONFIRM);
                BigDecimal removePrice;
                if (discount.getType() == DiscountType.FIXED) {
                    removePrice = subscriptionPlan.getPrice().subtract(discount.getValue());
                } else {
                    removePrice = subscriptionPlan.getPrice()
                            .multiply(BigDecimal.valueOf(100).subtract(discount.getValue()))
                            .divide(BigDecimal.valueOf(100));
                }
                if (seller.getBalance().compareTo(removePrice) < 0) {
                    bot.editMessageText(user.getChatId(), updateData.getMessageId(), msg.accountIsLittle(user.getLanguage()), kyb.topUpAccount(user.getLanguage()));
                    return;
                }
                seller.setBalance(seller.getBalance().subtract(removePrice));
                payment.setAmount(removePrice);
                payment.setSeller(seller);
                payment.setTransactionId(generateTransactionId());
                payment.setDescription("Yangi to'lov keldi");

                payment.setPaymentType(PaymentType.EXPENSE);
                payment.setImageName("img");
                payment.setCreatedAt(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());
                payment.setPaymentPurpose(PaymentPurpose.SUBSCRIPTION_RENEWAL);
                User u = userRepository.findById(user.getUserid()).orElse(null);
                if (u == null) return;
                String savedImagePath = drawPaymentImage(
                        payment.getAmount(),
                        u.getFullName(),
                        payment.getTransactionId(),
                        LocalDateTime.now(),
                        telegramProperties.getSellers().getBot().getUsername(),
                        subscriptionPlan.getName());
                if (savedImagePath == null) {
                    bot.sendMessage(user.getChatId(), msg.failedCheckCreatingImage(user.getLanguage()), true);
                    return;
                }
                savedImagePath = (telegramProperties.getBaseUrl() + "/" + savedImagePath);
                payment.setPaymentImage(savedImagePath);
                payment.setImageSize(getFileSize(savedImagePath));
                paymentRepository.save(payment);
                seller.setSubscriptionPlanId(subscriptionPlanId);
                seller.setPlanExpiresAt(LocalDateTime.now().plusMonths(subscriptionPlan.getName().getMonth()));
                sellerRepository1.save(seller);
                bot.sendPhoto(user.getChatId(), savedImagePath, msg.updatedSubscriptionPlan(user.getLanguage(), subscriptionPlan));
                user.setEventCodeLocation(0);
                menu(user, sellerButton.menu(user.getLanguage(), null, null).get(8).getText());

            } else {
                Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
                if (seller == null) {
                    bot.sendMessage(user.getChatId(), msg.failedCheckCreatingImage(user.getLanguage()), true);
                    return;
                }
                Payment payment = new Payment();
                payment.setStatus(PaymentStatus.CONFIRM);
                payment.setAmount(subscriptionPlan.getPrice());
                payment.setSeller(seller);
                if (seller.getBalance().compareTo(subscriptionPlan.getPrice()) < 0) {
                    bot.editMessageText(user.getChatId(), updateData.getMessageId(), msg.accountIsLittle(user.getLanguage()), kyb.topUpAccount(user.getLanguage()));
                    return;
                }
                payment.setTransactionId(generateTransactionId());
                payment.setDescription("Yangi to'lov keldi");
                payment.setPaymentType(PaymentType.EXPENSE);
                payment.setImageName("img");
                payment.setCreatedAt(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());
                payment.setPaymentPurpose(PaymentPurpose.SUBSCRIPTION_RENEWAL);
                User u = userRepository.findById(user.getUserid()).orElse(null);
                if (u == null) return;
                String savedImagePath = drawPaymentImage(
                        payment.getAmount(),
                        u.getFullName(),
                        payment.getTransactionId(),
                        LocalDateTime.now(),
                        telegramProperties.getSellers().getBot().getUsername(),
                        subscriptionPlan.getName());
                if (savedImagePath == null) {
                    bot.sendMessage(user.getChatId(), msg.failedCheckCreatingImage(user.getLanguage()), true);
                    return;
                }

                savedImagePath = (telegramProperties.getBaseUrl() + "/" + savedImagePath);
                payment.setPaymentImage(savedImagePath);
                payment.setImageSize(getFileSize(savedImagePath));
                paymentRepository.save(payment);
                seller.setSubscriptionPlanId(subscriptionPlanId);
                seller.setPlanExpiresAt(LocalDateTime.now().plusMonths(subscriptionPlan.getName().getMonth()));
                seller.setBalance(seller.getBalance().subtract(subscriptionPlan.getPrice()));
                sellerRepository1.save(seller);
                bot.sendPhoto(user.getChatId(), savedImagePath, msg.updatedSubscriptionPlan(user.getLanguage(), subscriptionPlan));
                user.setEventCodeLocation(0);
                menu(user, sellerButton.menu(user.getLanguage(), null, null).get(8).getText());
            }
        }
    }

    private String getUsersBotSeenThisProduct(Product product) {
        return telegramProperties.getUsers().getBot().getUsername() + "?start=product_" + product.getPkey();
    }

    public void sendLongMessage(Long chatId, String text) {

        if (text == null || text.isBlank()) {
            return;
        }

        int length = text.length();
        int start = 0;

        while (start < length) {
            int end = Math.min(start + 4096, length);

            String part = text.substring(start, end);
            bot.sendMessage(chatId, part);

            start = end;
        }
    }

    public void sendLongMessage(Long chatId, String text, ReplyKeyboardMarkup markup) {

        if (text == null || text.isBlank()) {
            return;
        }

        int length = text.length();
        int start = 0;

        while (start < length) {
            int end = Math.min(start + 4096, length);

            String part = text.substring(start, end);
            bot.sendMessage(chatId, part, markup);

            start = end;
        }
    }

    private List<Product> getAllProducts(BotSeller user) {
        Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
        if (seller == null) return new ArrayList<>();
        return productRepository.findAllProductsBySeller(seller.getPkey());
    }

    private List<ProductType> getAllProductTypes(Product product) {
        return product.getProductTypes().stream()
                .filter(type -> !type.getDeleted())
                .sorted(Comparator.comparing(ProductType::getCreatedAt))
                .collect(Collectors.toList());
    }

    private ProductType getFirstType(Product product) {
        return getAllProductTypes(product).get(0);
    }

    private ProductTypeImage getMainImageType(ProductType type) {
        for (ProductTypeImage image : getMainImagesType(type)) {
            if (image.getMain()) return image;
        }
        return getMainImagesType(type).get(0);
    }

    private List<ProductTypeImage> getMainImagesType(ProductType type) {
        return type.getImages().stream()
                .filter(image -> !image.getDeleted())
                .sorted(Comparator.comparing(ProductTypeImage::getCreatedAt))
                .collect(Collectors.toList());
    }

    public void devices(BotSeller user, String text) {
        Integer location = user.getEventCodeLocation();
        if (location == 0) {
            List<String> buttons = sellerButton.logoutAllBtn(user.getLanguage()).stream()
                    .map(Button::getText)
                    .toList();
            if (buttons.get(0).equals(text)) {
                List<BotSeller> users = sellerRepository.findAllByUserId(user.getUserid());
                for (BotSeller botSeller : users) {
                    if (!user.equals(botSeller)) {
                        botSeller.setUserid(null);
                        sellerRepository.save(botSeller);
                        bot.sendMessage(botSeller.getChatId(), msg.logoutSeller(user.getLanguage()), true);
                    }
                }
                bot.sendMessage(
                        user.getChatId(), msg.successAllLogoutDevice(user.getLanguage())
                );
                start(user);
                return;
            }
            if (buttons.get(1).equals(text)) {
                start(user);
                return;
            }
        }
    }

    private void start(BotSeller user) {
        start(user.getChatId(), user.getFirstName(), user.getLastName(), user.getUsername());
    }

    public void expired(BotSeller user, String text) {
        Integer location = user.getEventCodeLocation();
        Language language = user.getLanguage();
        if (location == 0) {
            List<String> buttons = sellerButton.subscriptionExpiredBtn(language).stream()
                    .map(Button::getText)
                    .toList();
            if (buttons.get(1).equals(text)) {
                AdminCard adminCard = adminCardRepository.findAll().get(0);
                bot.sendPhoto(user.getChatId(), adminCard.getImgUrl(), msg.paymentInformation(
                        language, adminCard
                ), kyb.paymentBtn(language));
                return;
            }

            if (buttons.get(0).equals(text)) {
                AdminCard adminCard = adminCardRepository.findAll().get(0);
                Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
                if (seller == null) return;
                bot.sendMessage(user.getChatId(), msg.myBalance(user.getLanguage(), seller, true, subscriptionPlanRepository.findById(seller.getSubscriptionPlanId()).orElse(null)), kyb.topUpAccount(user.getLanguage()));
                return;
            }

            if (buttons.get(2).equals(text)) {
                AdminCard adminCard = adminCardRepository.findAll().get(0);
                List<SubscriptionPlanType> planNames = List.of(
                        SubscriptionPlanType.MONTH_1,
                        SubscriptionPlanType.MONTH_2,
                        SubscriptionPlanType.MONTH_3,
                        SubscriptionPlanType.MONTH_6,
                        SubscriptionPlanType.MONTH_12,
                        SubscriptionPlanType.ACTIVE_ALWAYS
                );
                List<SubscriptionPlan> plans = subscriptionPlanRepository.getViewPlans(planNames);
                bot.sendPhoto(user.getChatId(), adminCard.getImgUrl(), msg.subscriptionPlanMsg(
                        language
                ), kyb.selectSubscriptionPlans(language, planNames));
                user.setEventCodeLocation(2);
                sellerRepository.save(user);
                return;
            }
            List<String> paymentBtn = sellerButton.payment(language).stream()
                    .map(Button::getText)
                    .toList();
            if (text.equals(paymentBtn.get(1))) {
                start(user);
                return;
            }
            if (text.equals(paymentBtn.get(0))) {
                user.setEventCodeLocation(1);
                bot.sendMessage(user.getChatId(), msg.getCheckImage(user.getLanguage()), true);
                sellerRepository.save(user);
            }
        } else if (location == 1) {
            bot.sendMessage(user.getChatId(), msg.getCheckImage(language), true);
        } else if (location == 2) {
            List<SubscriptionPlan> plans = getPlans();
            List<String> buttons = sellerButton.selectSubscriptionPlans(user.getLanguage(), List.of(
                            SubscriptionPlanType.MONTH_1,
                            SubscriptionPlanType.MONTH_2,
                            SubscriptionPlanType.MONTH_3,
                            SubscriptionPlanType.MONTH_6,
                            SubscriptionPlanType.MONTH_12,
                            SubscriptionPlanType.ACTIVE_ALWAYS
                    )).stream()
                    .map(Button::getText)
                    .toList();
            SubscriptionPlanType planType;
            if (text.equals(buttons.get(0))) planType = SubscriptionPlanType.MONTH_1;
            else if (text.equals(buttons.get(1))) planType = SubscriptionPlanType.MONTH_2;
            else if (text.equals(buttons.get(2))) planType = SubscriptionPlanType.MONTH_3;
            else if (text.equals(buttons.get(3))) planType = SubscriptionPlanType.MONTH_6;
            else if (text.equals(buttons.get(4))) planType = SubscriptionPlanType.MONTH_12;
            else if (text.equals(buttons.get(5))) planType = SubscriptionPlanType.ACTIVE_ALWAYS;
            else if (text.equals(buttons.get(6))) {
                start(user);
                return;
            } else {
                bot.sendMessage(user.getChatId(), msg.wrongBtn(language), kyb.selectSubscriptionPlans(user.getLanguage(), List.of(
                        SubscriptionPlanType.MONTH_1,
                        SubscriptionPlanType.MONTH_2,
                        SubscriptionPlanType.MONTH_3,
                        SubscriptionPlanType.MONTH_6,
                        SubscriptionPlanType.MONTH_12,
                        SubscriptionPlanType.ACTIVE_ALWAYS
                )));
                return;
            }
            SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findByName(planType).orElse(null);
            if (subscriptionPlan == null) return;
            Discount discount = discountRepository.getSubscriptionPlan(subscriptionPlan.getPkey()).orElse(null);
            bot.sendMessage(user.getChatId(), msg.subscriptionInformation(user.getLanguage(), subscriptionPlan, discount), kyb.planSubscription(
                    user.getLanguage(),
                    subscriptionPlan.getPkey()
            ));
        }
    }

    private List<SubscriptionPlan> getPlans() {
        List<SubscriptionPlanType> planNames = List.of(
                SubscriptionPlanType.MONTH_1,
                SubscriptionPlanType.MONTH_2,
                SubscriptionPlanType.MONTH_3,
                SubscriptionPlanType.MONTH_6,
                SubscriptionPlanType.MONTH_12,
                SubscriptionPlanType.ACTIVE_ALWAYS
        );
        return subscriptionPlanRepository.getViewPlans(planNames);
    }

    public void expired(BotSeller user, List<String> photoFileIds) {
        if (user.getEventCodeLocation() != 1) return;
        String fileId = photoFileIds.get(photoFileIds.size() - 1);
        String savedImagePath = savePaymentImage(bot.getBotToken(), fileId);
        Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
        if (savedImagePath == null || seller == null) {
            bot.sendMessage(user.getChatId(), msg.failedUploadCheck(user.getLanguage()), true);
            return;
        }
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.NEW);
        payment.setAmount(null);
        payment.setSeller(seller);
        payment.setTransactionId(generateTransactionId());
        payment.setDescription("Yangi to'lov keldi");
        payment.setPaymentImage(telegramProperties.getBaseUrl() + "/" + savedImagePath);
        payment.setPaymentType(PaymentType.EXPENSE);
        payment.setImageSize(getFileSize(telegramProperties.getBaseUrl() + "/" + savedImagePath));
        payment.setImageName("payment-image");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setPaymentPurpose(PaymentPurpose.ACCOUNT_TOPUP);
        paymentRepository.save(payment);
        bot.sendPhoto(user.getChatId(), payment.getPaymentImage(), msg.senderCheckForChecking(user.getLanguage(), payment.getTransactionId()));
        start(user);
    }

    public String savePaymentImage(String botToken, String fileId) {
        try {
            // 1. getFile API
            String getFileUrl = "https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId;
            HttpURLConnection conn = (HttpURLConnection) new URL(getFileUrl).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 2. file_path ni ajratib olish
            String filePath = response.toString()
                    .split("\"file_path\":\"")[1]
                    .split("\"")[0];
            String downloadUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;
            InputStream inputStream = new URL(downloadUrl).openStream();

            File dir = new File("uploads/seller/payment");
            if (!dir.exists()) dir.mkdirs();

            // 5. Unique filename
            String extension = filePath.substring(filePath.lastIndexOf("."));
            String timestamp = LocalDateTime.now()
                    .toString()
                    .replace(":", "-")
                    .replace(".", "-");
            String filename = timestamp + UUID.randomUUID().toString() + extension;

            File outputFile = new File(dir, filename);
            FileOutputStream fos = new FileOutputStream(outputFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            fos.close();
            inputStream.close();

            // 6. Saqlangan path qaytariladi
            return "uploads/seller/payment/" + filename;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String generateTransactionId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "TXN_" + timestamp + "_" + randomPart;
    }

    public long getFileSize(String fileUrl) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(fileUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000); // 5 soniya timeout
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return conn.getContentLengthLong();
            } else {
                System.err.println("HTTP error: " + responseCode + " for URL: " + fileUrl);
                return -1;
            }
        } catch (IOException e) {
            System.err.println("Error getting file size for URL: " + fileUrl);
            e.printStackTrace();
            return -1;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void expired(BotSeller user, TelegramUpdateData updateData) {
        if (user.getEventCodeLocation() == 0) {
            bot.answerCallbackQuery(updateData.getCallbackQueryId(), "Ok", false);
            expired(user, sellerButton.subscriptionExpiredBtn(user.getLanguage()).get(1).getText());
            return;
        }
        if (user.getEventCodeLocation() == 2) {
            bot.answerCallbackQuery(updateData.getCallbackQueryId(), "Ok", false);
            String data = updateData.getCallbackData();
            Long subscriptionPlanId = Long.valueOf(data.split(":")[1]);
            SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(subscriptionPlanId).orElse(null);
            if (subscriptionPlan == null)
                return;
            Discount discount = discountRepository.getSubscriptionPlan(subscriptionPlan.getPkey()).orElse(null);
            if (discount != null && subscriptionPlan.isDiscount()) {
                Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
                if (seller == null) {
                    bot.sendMessage(user.getChatId(), msg.failedCheckCreatingImage(user.getLanguage()), true);
                    return;
                }
                Payment payment = new Payment();
                payment.setStatus(PaymentStatus.CONFIRM);
                BigDecimal removePrice;
                if (discount.getType() == DiscountType.FIXED) {
                    removePrice = subscriptionPlan.getPrice().subtract(discount.getValue());
                } else {
                    removePrice = subscriptionPlan.getPrice()
                            .multiply(BigDecimal.valueOf(100).subtract(discount.getValue()))
                            .divide(BigDecimal.valueOf(100));
                }
                if (seller.getBalance().compareTo(removePrice) < 0) {
                    bot.sendMessage(user.getChatId(), msg.accountIsLittle(user.getLanguage()));
                    return;
                }
                seller.setBalance(seller.getBalance().subtract(removePrice));
                payment.setAmount(removePrice);
                payment.setSeller(seller);
                payment.setTransactionId(generateTransactionId());
                payment.setDescription("Yangi to'lov keldi");

                payment.setPaymentType(PaymentType.EXPENSE);
                payment.setImageName("img");
                payment.setCreatedAt(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());
                payment.setPaymentPurpose(PaymentPurpose.SUBSCRIPTION_RENEWAL);
                User u = userRepository.findById(user.getUserid()).orElse(null);
                if (u == null) return;
                String savedImagePath = drawPaymentImage(
                        payment.getAmount(),
                        u.getFullName(),
                        payment.getTransactionId(),
                        LocalDateTime.now(),
                        telegramProperties.getSellers().getBot().getUsername(),
                        subscriptionPlan.getName());
                if (savedImagePath == null) {
                    bot.sendMessage(user.getChatId(), msg.failedCheckCreatingImage(user.getLanguage()), true);
                    return;
                }
                savedImagePath = (telegramProperties.getBaseUrl() + "/" + savedImagePath);
                payment.setPaymentImage(savedImagePath);
                payment.setImageSize(getFileSize(savedImagePath));
                paymentRepository.save(payment);
                seller.setSubscriptionPlanId(subscriptionPlanId);
                seller.setPlanExpiresAt(LocalDateTime.now().plusMonths(subscriptionPlan.getName().getMonth()));
                sellerRepository1.save(seller);
                bot.sendPhoto(user.getChatId(), savedImagePath, msg.updatedSubscriptionPlan(user.getLanguage(), subscriptionPlan));
                start(user);
            } else {
                Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
                if (seller == null) {
                    bot.sendMessage(user.getChatId(), msg.failedCheckCreatingImage(user.getLanguage()), true);
                    return;
                }
                Payment payment = new Payment();
                payment.setStatus(PaymentStatus.CONFIRM);
                payment.setAmount(subscriptionPlan.getPrice());
                payment.setSeller(seller);
                if (seller.getBalance().compareTo(subscriptionPlan.getPrice()) < 0) {
                    bot.sendMessage(user.getChatId(), msg.accountIsLittle(user.getLanguage()));
                    return;
                }
                payment.setTransactionId(generateTransactionId());
                payment.setDescription("Yangi to'lov keldi");
                payment.setPaymentType(PaymentType.EXPENSE);
                payment.setImageName("img");
                payment.setCreatedAt(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());
                payment.setPaymentPurpose(PaymentPurpose.SUBSCRIPTION_RENEWAL);
                User u = userRepository.findById(user.getUserid()).orElse(null);
                if (u == null) return;
                String savedImagePath = drawPaymentImage(
                        payment.getAmount(),
                        u.getFullName(),
                        payment.getTransactionId(),
                        LocalDateTime.now(),
                        telegramProperties.getSellers().getBot().getUsername(),
                        subscriptionPlan.getName());
                if (savedImagePath == null) {
                    bot.sendMessage(user.getChatId(), msg.failedCheckCreatingImage(user.getLanguage()), true);
                    return;
                }

                savedImagePath = (telegramProperties.getBaseUrl() + "/" + savedImagePath);
                payment.setPaymentImage(savedImagePath);
                payment.setImageSize(getFileSize(savedImagePath));
                paymentRepository.save(payment);
                seller.setSubscriptionPlanId(subscriptionPlanId);
                seller.setPlanExpiresAt(LocalDateTime.now().plusMonths(subscriptionPlan.getName().getMonth()));
                seller.setBalance(seller.getBalance().subtract(subscriptionPlan.getPrice()));
                sellerRepository1.save(seller);
                bot.sendPhoto(user.getChatId(), savedImagePath, msg.updatedSubscriptionPlan(user.getLanguage(), subscriptionPlan));
                start(user);
            }
        }
    }

    private String drawPaymentImage(BigDecimal amount, String fullName, String transactionId,
                                    LocalDateTime createdAt, String botUsername,
                                    SubscriptionPlanType subscriptionPlan) {
        if (subscriptionPlan == SubscriptionPlanType.ACTIVE_ALWAYS) ;
        LocalDateTime planExpiresAt = LocalDateTime.now().plusMonths(subscriptionPlan.getMonth());
        String folder = "uploads/seller/checks";

        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // O'lchamni biroz kattalashtiramiz
        int width = 1080;
        int height = 1450; // Subscription uchun joy qo'shdik

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Premium quality rendering
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 1. MODERN GRADIENT BACKGROUND
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(250, 251, 255),
                width, height, new Color(243, 244, 248)
        );
        g.setPaint(bgGradient);
        g.fillRect(0, 0, width, height);

        // Abstract background elements
        g.setColor(new Color(99, 102, 241, 8));

        // Large circles
        g.fillOval(-300, -300, 800, 800);
        g.fillOval(width - 500, height - 500, 1000, 1000);

        // Grid lines
        g.setColor(new Color(99, 102, 241, 5));
        g.setStroke(new BasicStroke(1));
        for (int i = 0; i < width; i += 40) {
            g.drawLine(i, 0, i, height);
        }

        // 2. MAIN CARD
        int cardWidth = 920;
        int cardHeight = 1300;
        int cardX = (width - cardWidth) / 2;
        int cardY = 50;

        // Card shadow
        for (int i = 0; i < 20; i++) {
            g.setColor(new Color(0, 0, 0, 8 - i / 3));
            g.fillRoundRect(cardX - i, cardY - i, cardWidth + i * 2, cardHeight + i * 2, 50, 50);
        }

        // Main card
        g.setColor(Color.WHITE);
        g.fillRoundRect(cardX, cardY, cardWidth, cardHeight, 50, 50);

        // Card border
        g.setColor(new Color(229, 231, 235));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(cardX, cardY, cardWidth, cardHeight, 50, 50);

        // 3. HEADER SECTION
        int headerY = cardY + 60;

        // Success icon
        g.setColor(new Color(34, 197, 94));
        g.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D checkmark = new Path2D.Float();
        checkmark.moveTo(cardX + 80, headerY + 40);
        checkmark.lineTo(cardX + 110, headerY + 70);
        checkmark.lineTo(cardX + 160, headerY);
        g.draw(checkmark);

        // Title
        g.setFont(new Font("Segoe UI", Font.BOLD, 52));
        g.setColor(new Color(17, 24, 39));
        String title = "Payment Successful";
        g.drawString(title, cardX + 200, headerY + 50);

        // Subtitle
        g.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        g.setColor(new Color(107, 114, 128));
        String subtitle = "Your payment has been processed";
        g.drawString(subtitle, cardX + 200, headerY + 90);

        // Header line
        g.setColor(new Color(229, 231, 235));
        g.setStroke(new BasicStroke(3));
        g.drawLine(cardX + 60, headerY + 130, cardX + cardWidth - 60, headerY + 130);

        // 4. AMOUNT SECTION
        int amountY = headerY + 200;

        // Amount label
        g.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        g.setColor(new Color(107, 114, 128));
        String amountLabel = "AMOUNT PAID";
        g.drawString(amountLabel, cardX + (cardWidth - g.getFontMetrics().stringWidth(amountLabel)) / 2, amountY);

        // Amount value
        g.setFont(new Font("Segoe UI", Font.BOLD, 80));
        g.setColor(new Color(17, 24, 39));

        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String formattedAmount = formatter.format(amount);
        String amountText = formattedAmount.substring(0, formattedAmount.indexOf(",")) + " UZS";
//        String amountText = formattedAmount + " UZS";
        int amountTextWidth = g.getFontMetrics().stringWidth(amountText);
        g.drawString(amountText, cardX + (cardWidth - amountTextWidth) / 2, amountY + 110);

        // Amount underline
        g.setColor(new Color(99, 102, 241));
        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int underlineStart = cardX + (cardWidth - amountTextWidth) / 2 - 25;
        int underlineEnd = cardX + (cardWidth - amountTextWidth) / 2 + amountTextWidth + 25;
        g.drawLine(underlineStart, amountY + 125, underlineEnd, amountY + 125);

        // 5. SUBSCRIPTION PLAN SECTION (YANGI QO'SHILGAN)
        int planY = amountY + 180;

        // Plan card
        g.setColor(new Color(99, 102, 241, 10));
        g.fillRoundRect(cardX + 60, planY, cardWidth - 120, 120, 25, 25);

        g.setColor(new Color(99, 102, 241, 30));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(cardX + 60, planY, cardWidth - 120, 120, 25, 25);

        // Plan icon
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        g.setColor(new Color(99, 102, 241));
        g.drawString("⭐", cardX + 90, planY + 75);

        // Plan text
        g.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g.setColor(new Color(17, 24, 39));

        String planText = getPlanDisplayText(subscriptionPlan);
        g.drawString(planText, cardX + 150, planY + 50);

        // Plan expiry
        if (planExpiresAt != null) {
            g.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            g.setColor(new Color(107, 114, 128));

            String expiryText = "Valid until: " +
                    planExpiresAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            g.drawString(expiryText, cardX + 150, planY + 90);
        }

        // 6. TRANSACTION DETAILS
        int detailsY = planY + 160;

        // Details title
        g.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g.setColor(new Color(17, 24, 39));
        g.drawString("Transaction Details", cardX + 80, detailsY);

        // Details separator
        g.setColor(new Color(229, 231, 235));
        g.setStroke(new BasicStroke(2));
        g.drawLine(cardX + 80, detailsY + 20, cardX + cardWidth - 80, detailsY + 20);

        // Details items
        int detailStartY = detailsY + 70;
        int detailSpacing = 75;

        // Customer
        drawModernDetailRow(g, "👤", "Customer:", fullName,
                cardX + 80, detailStartY, cardWidth - 160);

        // Transaction ID
        drawModernDetailRow(g, "🔑", "Transaction ID:", transactionId,
                cardX + 80, detailStartY + detailSpacing, cardWidth - 160);

        // Date & Time
        String formattedDate = createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"));
        drawModernDetailRow(g, "📅", "Date & Time:", formattedDate,
                cardX + 80, detailStartY + detailSpacing * 2, cardWidth - 160);

        // Payment Gateway (TO'G'RILANDI)
        String cleanBotUsername = botUsername.startsWith("@") ? botUsername : "@" + botUsername;
        if (cleanBotUsername.contains("https://")) {
            cleanBotUsername = cleanBotUsername.replace("https://t.me/", "@");
        }
        drawModernDetailRow(g, "🤖", "Payment Gateway:", cleanBotUsername.substring(1),
                cardX + 80, detailStartY + detailSpacing * 3, cardWidth - 160);

        // 7. STATUS SECTION
        int statusY = detailStartY + detailSpacing * 4 + 40;

        // Status background
        GradientPaint statusBg = new GradientPaint(
                cardX + 60, statusY,
                new Color(220, 252, 231),
                cardX + cardWidth - 60, statusY + 80,
                new Color(187, 247, 208)
        );
        g.setPaint(statusBg);
        g.fillRoundRect(cardX + 60, statusY, cardWidth - 120, 80, 40, 40);

        // Status border
        g.setColor(new Color(34, 197, 94));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(cardX + 60, statusY, cardWidth - 120, 80, 40, 40);

        // Status text
        g.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g.setColor(new Color(22, 163, 74));
        String statusText = "✓ TRANSACTION COMPLETED";
        g.drawString(statusText, cardX + (cardWidth - g.getFontMetrics().stringWidth(statusText)) / 2, statusY + 52);

        // 8. FOOTER
        int footerY = cardY + cardHeight - 70;

        g.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        g.setColor(new Color(107, 114, 128));

        // Footer line 1
        String footer1 = "This is an automated receipt";
        g.drawString(footer1, cardX + (cardWidth - g.getFontMetrics().stringWidth(footer1)) / 2, footerY);

        // Footer line 2
        String footer2 = "No signature required • " + LocalDateTime.now().getYear();
        g.drawString(footer2, cardX + (cardWidth - g.getFontMetrics().stringWidth(footer2)) / 2, footerY + 35);

        // 9. BOTTOM DECORATION
        g.setColor(new Color(99, 102, 241, 20));
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int bottomLineY = cardY + cardHeight - 20;
        g.drawLine(cardX + 100, bottomLineY, cardX + cardWidth - 100, bottomLineY);

        // Decorative dots
        g.fillOval(cardX + 90, bottomLineY - 10, 20, 20);
        g.fillOval(cardX + cardWidth - 110, bottomLineY - 10, 20, 20);

        g.dispose();

        // Save file
        String fileName = "receipt_" + System.currentTimeMillis() + ".png";
        String filePath = folder + "/" + fileName;

        try {
            ImageIO.write(image, "png", new File(filePath));
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method for drawing detail rows
    private void drawModernDetailRow(Graphics2D g, String icon, String label, String value,
                                     int x, int y, int maxWidth) {
        // Icon background
        g.setColor(new Color(99, 102, 241, 15));
        g.fillOval(x, y - 25, 50, 50);

        // Icon
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        g.setColor(new Color(99, 102, 241));
        g.drawString(icon, x + 11, y + 10);

        // Label
        g.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g.setColor(new Color(75, 85, 99));
        g.drawString(label, x + 65, y + 10);

        // Value
        g.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        g.setColor(new Color(17, 24, 39));

        // Calculate label width for alignment
        int labelWidth = g.getFontMetrics().stringWidth(label);

        // Check if value needs wrapping
        String displayValue = value;
        FontMetrics fm = g.getFontMetrics();
        int valueWidth = fm.stringWidth(value);
        int availableWidth = maxWidth - (65 + labelWidth + 20);

        if (valueWidth > availableWidth) {
            // Try to split or truncate
            if (value.contains("http")) {
                // For URLs, just show domain
                displayValue = cleanUrlForDisplay(value);
            } else if (value.length() > 25) {
                displayValue = value.substring(0, 22) + "...";
            }
        }

        g.drawString(displayValue, x + 65 + labelWidth + 15, y + 10);

        // Bottom separator
        g.setColor(new Color(229, 231, 235, 100));
        g.setStroke(new BasicStroke(1));
        g.drawLine(x, y + 25, x + maxWidth, y + 25);
    }

    // Helper to clean URL for display
    private String cleanUrlForDisplay(String url) {
        if (url.contains("https://t.me/")) {
            return url.substring("https://t.me/".length());
        } else if (url.startsWith("@")) {
            return url.substring(1);
        } else if (url.contains("t.me/")) {
            return url.substring(url.indexOf("t.me/") + 5);
        }
        return url.length() > 25 ? url.substring(0, 22) + "..." : url;
    }

    // Helper to get display text for subscription plan
    private String getPlanDisplayText(SubscriptionPlanType plan) {
        if (plan == null) return "No Plan";

        return switch (plan) {
            case MONTH_1 -> "1 Month Plan";
            case MONTH_2 -> "2 Months Plan";
            case MONTH_3 -> "3 Months Plan";
            case MONTH_6 -> "6 Months Plan";
            case MONTH_12 -> "1 Year Plan";
            case TRIAL -> "Trial Plan";
            case ACTIVE_ALWAYS -> "Lifetime Plan";
            case EXPIRED -> "Expired Plan";
            default -> plan.toString();
        };
    }

    public boolean checkSeller(BotSeller user) {
        Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
        if (seller == null) return true;
        Seller checkSeller = sellerRepository1.checkActivePlanSeller(seller.getPkey()).orElse(null);
        if (checkSeller == null) {
            SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(seller.getSubscriptionPlanId()).orElse(null);
            if (subscriptionPlan == null) return true;
            if (subscriptionPlan.getName() != SubscriptionPlanType.EXPIRED) {
                return true;
            }
            if (user.getEventCode() == SellerEventCode.EXPIRED) {
                return true;
            }
            bot.sendMessage(user.getChatId(), msg.subscriptionExpiredMessage(user.getLanguage()), kyb.subscriptionExpiredBtn(user.getLanguage()));
            eventCode(user.getChatId(), SellerEventCode.EXPIRED);
            return false;
        }
        return true;
    }

    public void menu(BotSeller user, List<String> photoFileIds) {
        String fileId = photoFileIds.get(photoFileIds.size() - 1);
        if (user.getEventCodeLocation() == 24) {
            String savedImagePath = savePaymentImage(bot.getBotToken(), fileId);
            Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
            if (seller == null) return;
            if (savedImagePath != null) {
                seller.setCardImageUrl(telegramProperties.getBaseUrl() + "/" + savedImagePath);
                sellerRepository1.save(seller);
                bot.sendMessage(user.getChatId(), msg.changed(user.getLanguage()));
                bot.sendPhoto(user.getChatId(), seller.getCardImageUrl(), msg.cardInfo(user.getLanguage(), seller), kyb.cardEdit(user.getLanguage()));
                user.setEventCodeLocation(24);
                sellerRepository.save(user);
            }
            return;
        }
        if (user.getEventCodeLocation() == 2) {

            String savedImagePath = savePaymentImage(bot.getBotToken(), fileId);
            Seller seller = sellerRepository1.findByUserid(user.getUserid()).orElse(null);
            if (savedImagePath == null || seller == null) {
                bot.sendMessage(user.getChatId(), msg.failedUploadCheck(user.getLanguage()), true);
                return;
            }
            Payment payment = new Payment();
            payment.setStatus(PaymentStatus.NEW);
            payment.setAmount(null);
            payment.setSeller(seller);
            payment.setTransactionId(generateTransactionId());
            payment.setDescription("Yangi to'lov keldi");
            payment.setPaymentImage(telegramProperties.getBaseUrl() + "/" + savedImagePath);
            payment.setPaymentType(PaymentType.EXPENSE);
            payment.setImageSize(getFileSize(telegramProperties.getBaseUrl() + "/" + savedImagePath));
            payment.setImageName("payment-image");
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            payment.setPaymentPurpose(PaymentPurpose.ACCOUNT_TOPUP);
            paymentRepository.save(payment);
            bot.sendPhoto(user.getChatId(), payment.getPaymentImage(),
                    msg.senderCheckForChecking(user.getLanguage(), payment.getTransactionId()),
                    kyb.menu(user.getLanguage(), shopWebbAppUrl + "/" + user.getChatId(), user.getChatId())
            );
            try {
                user.setEventCodeLocation(0);
                sellerRepository.save(user);
                menu(user, sellerButton.menu(user.getLanguage(), null, null).get(4).getText());
            } catch (Exception ignored) {

            }
            return;
        }
    }
}