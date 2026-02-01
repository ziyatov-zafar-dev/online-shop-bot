package uz.zafar.onlineshoptelegrambot.botservice;

import org.apache.commons.codec.language.bm.Lang;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;
import uz.zafar.onlineshoptelegrambot.bot.kyb.user.UserButton;
import uz.zafar.onlineshoptelegrambot.bot.kyb.user.UserKyb;
import uz.zafar.onlineshoptelegrambot.bot.msg.UserMsg;
import uz.zafar.onlineshoptelegrambot.config.TelegramProperties;
import uz.zafar.onlineshoptelegrambot.db.entity.AboutWe;
import uz.zafar.onlineshoptelegrambot.db.entity.SellerVideo;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.CustomerLocation;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.enums.CustomerEventCode;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;
import uz.zafar.onlineshoptelegrambot.db.entity.comment.Comment;
import uz.zafar.onlineshoptelegrambot.db.entity.common.Discount;
import uz.zafar.onlineshoptelegrambot.db.entity.contact.ContactWe;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.DiscountType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.Language;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SellerStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.order.Basket;
import uz.zafar.onlineshoptelegrambot.db.entity.order.OrderItem;
import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.BasketType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.DeliveryType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.repositories.*;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BasketRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.CustomerLocationRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.CategoryRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeImageRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.bot.kyb.Button;
import uz.zafar.onlineshoptelegrambot.dto.gson.AddressDto;
import uz.zafar.onlineshoptelegrambot.dto.gson.LocationStatus;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateOrderItemRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateShopOrderRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.order.response.ShopOrderResponse;
import uz.zafar.onlineshoptelegrambot.service.ApiLocationService;
import uz.zafar.onlineshoptelegrambot.service.OrderService;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsersTelegramBotFunction {


    private final TelegramBot bot;
    private final UserMsg userMsg;
    private final BotCustomerRepository botCustomerRepository;
    private final UserKyb userKyb;
    private final UserButton userButton;
    private final UserRepository userRepository;
    private final CustomerLocationRepository customerLocationRepository;
    private final ApiLocationService locationService;
    private final CategoryRepository categoryRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductRepository productRepository;
    private final ProductTypeImageRepository productTypeImageRepository;
    private final BasketRepository basketRepository;
    private final ShopRepository shopRepository;
    private final OrderService orderService;
    private final ShopOrderRepository shopOrderRepository;
    private final BotSellerRepository botSellerRepository;
    private final TelegramBot sellerBot;
    private final TelegramProperties telegramProperties;
    private final OrderItemRepository orderItemRepository;
    private final AboutWeRepository aboutWeRepository;
    private final ContactWeRepository contactWeRepository;
    private final SellerVideoRepository sellerVideoRepository;

    public UsersTelegramBotFunction(@Qualifier("customer") TelegramBot bot, UserMsg userMsg, BotCustomerRepository botCustomerRepository, UserKyb userKyb, UserButton userButton, UserRepository userRepository, CustomerLocationRepository customerLocationRepository, ApiLocationService locationService, CategoryRepository categoryRepository, ProductTypeRepository productTypeRepository, ProductRepository productRepository, ProductTypeImageRepository productTypeImageRepository, BasketRepository basketRepository, ShopRepository shopRepository, OrderService orderService, ShopOrderRepository shopOrderRepository, BotSellerRepository botSellerRepository,
                                    @Qualifier("seller") TelegramBot sellerBot, TelegramProperties telegramProperties, OrderItemRepository orderItemRepository, AboutWeRepository aboutWeRepository, ContactWeRepository contactWeRepository, SellerVideoRepository sellerVideoRepository) {
        this.bot = bot;
        this.userMsg = userMsg;
        this.botCustomerRepository = botCustomerRepository;
        this.userKyb = userKyb;
        this.userButton = userButton;
        this.userRepository = userRepository;
        this.customerLocationRepository = customerLocationRepository;
        this.locationService = locationService;
        this.categoryRepository = categoryRepository;
        this.productTypeRepository = productTypeRepository;
        this.productRepository = productRepository;
        this.productTypeImageRepository = productTypeImageRepository;
        this.basketRepository = basketRepository;
        this.shopRepository = shopRepository;
        this.orderService = orderService;
        this.shopOrderRepository = shopOrderRepository;
        this.botSellerRepository = botSellerRepository;
        this.sellerBot = sellerBot;
        this.telegramProperties = telegramProperties;
        this.orderItemRepository = orderItemRepository;
        this.aboutWeRepository = aboutWeRepository;
        this.contactWeRepository = contactWeRepository;
        this.sellerVideoRepository = sellerVideoRepository;
    }

    public void start(Long chatId, String firstName, String lastName, String username) {

        BotCustomer user = this.refreshUser(chatId, firstName, lastName, username);
        clearBasket(user);
        if (user.getLanguage() == null) {
            bot.sendMessage(user.getChatId(), userMsg.welcomeChooseLang(Language.UZBEK), true);
            bot.sendMessage(user.getChatId(), userMsg.welcomeChooseLang(Language.CYRILLIC), true);
            bot.sendMessage(user.getChatId(), userMsg.welcomeChooseLang(Language.RUSSIAN), true);
            bot.sendMessage(user.getChatId(), userMsg.welcomeChooseLang(Language.ENGLISH), userKyb.requestLang());
            user.setEventCode(CustomerEventCode.REQUEST_LANG);
            botCustomerRepository.save(user);
            return;
        }
        user.setEventCode(CustomerEventCode.MENU);
        user.setEventCodeLocation(0);
        botCustomerRepository.save(user);
        bot.sendMessage(user.getChatId(), userMsg.menu(user.getLanguage()), userKyb.menu(user.getLanguage()));
    }

    public BotCustomer refreshUser(Long chatId, String firstName, String lastName, String username) {
        BotCustomer user = getUser(chatId);
        if (user != null) {
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user = botCustomerRepository.save(user);
        } else {
            user = new BotCustomer();
            user.setChatId(chatId);
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user = botCustomerRepository.save(user);
        }
        return user;
    }

    public BotCustomer getUser(Long chatId) {
        return botCustomerRepository.checkUser(chatId).orElse(null);
    }

    public void requestLang(BotCustomer user, String text, Integer messageId) {
        if (text.equals(userButton.requestLang().get(0).getText())) user.setLanguage(Language.UZBEK);
        else if (text.equals(userButton.requestLang().get(1).getText())) user.setLanguage(Language.CYRILLIC);
        else if (text.equals(userButton.requestLang().get(2).getText())) user.setLanguage(Language.RUSSIAN);
        else if (text.equals(userButton.requestLang().get(3).getText())) user.setLanguage(Language.ENGLISH);
        else {
            if (messageId != null) bot.deleteMessage(user.getChatId(), messageId);
            return;
        }
        user.setEventCode(CustomerEventCode.MENU);
        botCustomerRepository.save(user);
        bot.sendMessage(user.getChatId(), userMsg.successfullyChangedLang(user.getLanguage()));
        start(user.getChatId(), user.getFirstName(), user.getLastName(), user.getUsername());
    }

    public String extractAddress(String text) {
        if (text == null) return null;
        return text.replaceFirst("🏠\\s*", "");
    }

    public void menu(BotCustomer user, String text, int messageId) {
        Integer location = user.getEventCodeLocation();
        if (location == 0) {
            clearBasket(user);
            List<String> menu = userButton.menu(user.getLanguage()).stream()
                    .map(Button::getText).toList();
            if (userButton.backBtn(user.getLanguage()).get(0).getText().equals(text)) {
                start(user);
                return;
            }
            if (menu.get(0).equals(text)) {
                if (user.getTelegramPhone() == null) {
                    user.setEventCodeLocation(1);
                    botCustomerRepository.save(user);
                    bot.sendMessage(user.getChatId(), userMsg.requestContact(user.getLanguage()), userKyb.requestContact(user.getLanguage()));
                } else {
                    user.setEventCodeLocation(2);
                    botCustomerRepository.save(user);
                    List<CustomerLocation> locations = customerLocationRepository.myLocation(user.getPkey());
                    bot.sendMessage(user.getChatId(),
                            userMsg.sendLocationOrChooseMyLocation(locations.isEmpty(), user.getLanguage()),
                            userKyb.myLocations(user.getLanguage(), locations));
                }
                return;
            }
            if (menu.get(1).equals(text)) {
                List<ShopOrder> orders = shopOrderRepository.myOrders(user.getPkey()).stream()
                        .filter(order -> (order.getStatus() != ShopOrderStatus.COMPLETED && order.getStatus() != ShopOrderStatus.CANCELLED))
                        .toList();
                if (orders.isEmpty()) {
                    bot.sendMessage(user.getChatId(), userMsg.emptyOrders(user.getLanguage()), userKyb.menu(user.getLanguage()));
                    return;
                }
                bot.sendMessage(user.getChatId(), "ㅤㅤ", userKyb.backBtn(user.getLanguage()));
                for (ShopOrder order : orders) {
                    if (order != null) {
                        bot.sendMessage(user.getChatId(),
                                userMsg.orderInformation(user.getLanguage(), order),
                                userKyb.orderInformation(user.getLanguage(), order.getStatus(), order.getPkey())
                        );
                    }
                }
                user.setEventCode(CustomerEventCode.ORDER);
                user.setEventCodeLocation(0);
                botCustomerRepository.save(user);
                return;
            }
            if (menu.get(2).equals(text)) {
                String videoUrl;

                Language l = user.getLanguage();
                SellerVideo video = sellerVideoRepository.findAll().get(0);
                bot.sendVideo(user.getChatId(),switch (l) {
                    case UZBEK -> video.getVideoUrlUz();
                    case CYRILLIC -> video.getVideoUrlCyr();
                    case RUSSIAN -> video.getVideoUrlRu();
                    case ENGLISH -> video.getVideoUrlEn();
                },null,userKyb.becomingSellerKyb(user.getLanguage(),telegramProperties.getSellers().getBot().getUsername()));
                return;
            }
            if (menu.get(3).equals(text)) {
                user.setEventCodeLocation(10);
                bot.sendMessage(user.getChatId(), userMsg.changeLanguage(user.getLanguage()), userKyb.requestLang());
                botCustomerRepository.save(user);
                return;
            }
            if (menu.get(4).equals(text)) {
                Language l = user.getLanguage();
                AboutWe we = aboutWeRepository.findAll().get(0);
                bot.sendMessage(user.getChatId(),
                        l == Language.UZBEK ? we.getUz() : (
                                l == Language.CYRILLIC ? we.getCyr() :
                                        (l == Language.ENGLISH ? we.getEn() : we.getRu())
                        ), userKyb.backBtn(user.getLanguage())
                );
                return;
            }
            if (menu.get(5).equals(text)) {
                List<ContactWe> contacts = contactWeRepository.allContacts();
                bot.sendMessage(user.getChatId(), userMsg.contactUsMsg(user.getLanguage()), userKyb.backBtn(user.getLanguage()));
                for (ContactWe contact : contacts) {
                    bot.sendContact(user.getChatId(), contact.getPhone(), contact.getFullName(), null);
                }
                return;
            }
        } else if (location == 10) {
            List<String> buttons = userButton.requestLang().stream()
                    .map(Button::getText)
                    .toList();
            Language language;
            if (text.equals(buttons.get(0))) language = Language.UZBEK;
            else if (text.equals(buttons.get(1))) language = Language.CYRILLIC;
            else if (text.equals(buttons.get(2))) language = Language.RUSSIAN;
            else if (text.equals(buttons.get(3))) language = Language.ENGLISH;
            else {
                bot.sendMessage(user.getChatId(), userMsg.wrongBtn(user.getLanguage()), userKyb.requestLang());
                return;
            }
            user.setLanguage(language);
            bot.sendMessage(user.getChatId(), userMsg.successfullyChangedLang(user.getLanguage()));
            start(user);
            botCustomerRepository.save(user);
        } else if (location == 1 || location == 2) {
            if (userButton.contactAndBackButtons(user.getLanguage()).get(1).getText().equals(text)) {
                start(user);
                return;
            }
            CustomerLocation customerLocation = customerLocationRepository.locationByAddress(extractAddress(text), user.getPkey()).orElse(null);
            if (customerLocation != null && location == 2) {
                user.setLatitude(customerLocation.getLatitude());
                user.setLongitude(customerLocation.getLongitude());
                user.setEventCodeLocation(4);
                botCustomerRepository.save(user);
                bot.sendMessage(user.getChatId(), userMsg.getCategories(user.getLanguage()), userKyb.getAllCategories(user.getChatId(), user.getLanguage(),
                        categoryRepository.getAllCategories().stream().filter(
                                category -> category.getParent() == null
                        ).toList()));
                return;
            }
            bot.sendMessage(user.getChatId(), userMsg.wrongBtn(user.getLanguage()), location == 1 ? userKyb.requestContact(user.getLanguage()) : userKyb.myLocations(
                    user.getLanguage(), customerLocationRepository.myLocation(user.getPkey())
            ));
        } else if (location == 3) {
            if (userButton.isAddedToMyLocation(user.getLanguage()).get(2).getText().equals(text)) {
                List<CustomerLocation> locations = customerLocationRepository.myLocation(user.getPkey());
                user.setEventCodeLocation(2);
                botCustomerRepository.save(user);
                bot.sendMessage(user.getChatId(),
                        userMsg.sendLocationOrChooseMyLocation(locations.isEmpty(), user.getLanguage()),
                        userKyb.myLocations(user.getLanguage(), locations));
                return;
            }

            if (userButton.isAddedToMyLocation(user.getLanguage()).get(0).getText().equals(text)) {
                user.setEventCodeLocation(4);
                botCustomerRepository.save(user);
                bot.sendMessage(user.getChatId(), userMsg.getCategories(user.getLanguage()), userKyb.getAllCategories(user.getChatId(), user.getLanguage(),
                        categoryRepository.getAllCategories().stream().filter(
                                category -> category.getParent() == null
                        ).toList()));
                return;
            }
            if (userButton.isAddedToMyLocation(user.getLanguage()).get(1).getText().equals(text)) {
                String address = locationService.getAddress(
                        user.getLatitude(), user.getLongitude()
                ).trim();
                if (
                        customerLocationRepository.locationByAddress(extractAddress(address), user.getPkey()).isEmpty() &&
                                customerLocationRepository.myLocation(user.getPkey()).size() <= 90
                ) {
                    CustomerLocation customerLocation = new CustomerLocation();
                    customerLocation.setCustomerId(user.getPkey());
                    customerLocation.setAddress(address);
                    customerLocation.setLatitude(user.getLatitude());
                    customerLocation.setLongitude(user.getLongitude());
                    customerLocationRepository.save(customerLocation);
                }
                bot.sendMessage(user.getChatId(), userMsg.addedMyLocation(user.getLanguage()), userKyb.isAddedToMyLocation(user.getLanguage()));
                return;
            }
            bot.sendMessage(user.getChatId(), userMsg.wrongBtn(user.getLanguage()), userKyb.isAddedToMyLocation(user.getLanguage()));
        } else if (location == 4) {
            if (text.equals(basketBtn(user.getLanguage()))) {
                handleBasket(user);
                return;
            }
            Category category = categoryRepository.findByName(text).orElse(null);
            if (category != null) {
                List<Category> categories = category.getChildren()
                        .stream()
                        .filter(Category::getActive)
                        .toList();
                if (categories.isEmpty()) {
                    List<Product> products = productRepository.findAllByCategoryId(category.getPkey());
                    if (products.isEmpty()) {
                        try {
                            if (category.getParent() == null) {
                                bot.sendMessage(user.getChatId(),
                                        userMsg.categoryIsProductsIsNullOrEmpty(user.getLanguage()));
                                return;
                            }
                            bot.sendMessage(user.getChatId(),
                                    userMsg.emptyProducts(user.getLanguage(), category), userKyb.getAllCategories(user.getChatId(),
                                            user.getLanguage(),
                                            category.getParent().getChildren()
                                    ));
                        } catch (Exception e) {
                            start(user);
                        }
                        return;
                    }
                    bot.sendPhoto(user.getChatId(), category.getImageUrl(),
                            userMsg.categoryInformation(user.getLanguage(), category, products.size()),
                            userKyb.getProductByCategory(user.getChatId(), products, user.getLanguage(), 0));
                    return;
                }
                user.setCategoryId(category.getPkey());
                botCustomerRepository.save(user);
                bot.sendMessage(user.getChatId(), userMsg.getCategories(user.getLanguage()), userKyb.getAllCategories(user.getChatId(), user.getLanguage(), categories));
                return;
            }

/*
            if (text.equals(categoryBackBtn(user.getLanguage()))) {
                if (user.getCategoryId() != null) {
                    category = categoryRepository.findById(user.getCategoryId()).orElse(null);
                    if (category == null) return;
                    if (category.getParent() == null) {
                        List<CustomerLocation> locations = customerLocationRepository.myLocation(user.getPkey());
                        user.setEventCodeLocation(2);
                        botCustomerRepository.save(user);
                        bot.sendMessage(user.getChatId(),
                                userMsg.sendLocationOrChooseMyLocation(locations.isEmpty(), user.getLanguage()),
                                userKyb.myLocations(user.getLanguage(), locations));
                    } else {
                        List<Category> categories = category.getParent().getChildren().stream().filter(Category::getActive).toList();
                        bot.sendMessage(user.getChatId(), userMsg.getCategories(user.getLanguage()), userKyb.getAllCategories(
                                user.getLanguage(), categories
                        ));
                    }
                } else {
                    List<CustomerLocation> locations = customerLocationRepository.myLocation(user.getPkey());
                    user.setEventCodeLocation(2);
                    botCustomerRepository.save(user);
                    bot.sendMessage(user.getChatId(),
                            userMsg.sendLocationOrChooseMyLocation(locations.isEmpty(), user.getLanguage()),
                            userKyb.myLocations(user.getLanguage(), locations));
                }
                return;
            }
*/
            if (text.equals(categoryBackBtn(user.getLanguage()))) {

                if (user.getCategoryId() != null) {
                    category = categoryRepository.findById(user.getCategoryId()).orElse(null);

                    // Agar kategoriya topilmasa, hech narsa qilmaymiz
                    if (category == null) return;

                    if (category.getParent() == null) {
                        /*List<CustomerLocation> locations = customerLocationRepository.myLocation(user.getPkey());
                        user.setEventCodeLocation(2);
                        botCustomerRepository.save(user);
                        bot.sendMessage(
                                user.getChatId(),
                                userMsg.sendLocationOrChooseMyLocation(locations.isEmpty(), user.getLanguage()),
                                userKyb.myLocations(user.getLanguage(), locations)
                        );*/
                        List<Category> categories = categoryRepository.getParentCategories();
                        bot.sendMessage(
                                user.getChatId(),
                                userMsg.getCategories(user.getLanguage()),
                                userKyb.getAllCategories(user.getChatId(), user.getLanguage(), categories)
                        );

                        user.setCategoryId(null);
                        botCustomerRepository.save(user);
                    } else {
                        // Parent mavjud bo'lsa → parentning bolalarini ko'rsatish
                        List<Category> categories = category.getParent().getChildren()
                                .stream()
                                .filter(Category::getActive)
                                .toList();
                        bot.sendMessage(
                                user.getChatId(),
                                userMsg.getCategories(user.getLanguage()),
                                userKyb.getAllCategories(user.getChatId(), user.getLanguage(), categories)
                        );

                        // Orqaga qaytishda parent kategoriyani saqlash
                        user.setCategoryId(category.getParent().getPkey());
                        botCustomerRepository.save(user);
                    }

                } else {
                    // Agar foydalanuvchi hech kategoriya tanlamagan bo'lsa → asosiy menyuga qaytish
                    List<CustomerLocation> locations = customerLocationRepository.myLocation(user.getPkey());
                    user.setEventCodeLocation(2);
                    clearBasket(user);
                    botCustomerRepository.save(user);
                    bot.sendMessage(
                            user.getChatId(),
                            userMsg.sendLocationOrChooseMyLocation(locations.isEmpty(), user.getLanguage()),
                            userKyb.myLocations(user.getLanguage(), locations)
                    );
                }

                return;
            }

            List<Category> categories;
            if (user.getCategoryId() == null)
                categories = categoryRepository.getAllCategories();
            else {
                category = categoryRepository.findById(user.getCategoryId()).orElse(null);
                if (category == null) return;
                categories = category.getChildren()
                        .stream()
                        .filter(Category::getActive)
                        .toList();
            }
            bot.sendMessage(user.getChatId(), userMsg.wrongBtn(user.getLanguage()), userKyb.getAllCategories(user.getChatId(), user.getLanguage(), categories));
        }
        if (location == 5 || location == 6) {
            if (text.equals(backText(user.getLanguage()))) {
                user.setEventCodeLocation(4);
                botCustomerRepository.save(user);
                bot.sendMessage(user.getChatId(), "ㅤㅤ", true);
                handleBasket(user);
                return;
            }
            bot.sendMessage(user.getChatId(), userMsg.wrongBtn(user.getLanguage()),
                    location == 5 ? userKyb.requestContact(user.getLanguage()) : userKyb.myLocations(
                            user.getLanguage(),
                            customerLocationRepository.myLocation(user.getPkey())
                    ));
            return;
        }
        if (location == 7) {
            if (text.equals(backText(user.getLanguage()))) {
                bot.sendMessage(user.getChatId(), "ㅤㅤ", true);
                bot.sendMessage(user.getChatId(), userMsg.chooseDeliveryType(user.getLanguage()), userKyb.chooseDeliveryType(user.getLanguage()));
                user.setEventCodeLocation(4);
                botCustomerRepository.save(user);
                return;
            }
            user.setHelperPhone(text);
            user.setEventCodeLocation(8);
            botCustomerRepository.save(user);
            bot.sendMessage(user.getChatId(), "⏳");
            List<CreateShopOrderRequestDto> req = refreshCreateOrder(user);
            String message = userMsg.isSuccessCreateOrder(
                    user, req
            );
            ReplyKeyboardMarkup markup = userKyb.isSuccessCreateOrder(user.getLanguage());
            bot.deleteMessage(user.getChatId(), messageId);
            bot.sendMessageLongText(user.getChatId(), message, markup);
            return;
        }
        if (location == 8) {
            if (text.equals(backText(user.getLanguage()))) {
                bot.sendMessage(
                        user.getChatId(), userMsg.enterPhoneNumberForOrder(user.getLanguage()),
                        userKyb.enterPhoneNumberForOrderWithBackBtn(user.getLanguage(), user.getTelegramPhone())
                );
                user.setEventCodeLocation(7);
                botCustomerRepository.save(user);
                return;
            }
            if (inArray(text, List.of("✅ Тасдиқлаш", "✅ Подтвердить", "✅ Confirm", "✅ Tasdiqlash"))) {
                bot.sendMessage(user.getChatId(), "⏳");
                List<CreateShopOrderRequestDto> request = refreshCreateOrder(user);
                ResponseDto<List<ShopOrderResponse>> response = orderService.createOrder(request);
                if (!response.isSuccess()) {
                    bot.sendMessage(user.getChatId(), user.getLanguage() == Language.UZBEK ? response.getMessage().getMessageUz() : (user.getLanguage() == Language.CYRILLIC ? response.getMessage().getMessageCyrillic() : (user.getLanguage() == Language.RUSSIAN ? response.getMessage().getMessageRu() : response.getMessage().getMessageEn())));
                    return;
                }
                for (ShopOrderResponse x : response.getData()) {
                    Shop shop = shopRepository.findById(x.getShop().getId()).orElse(null);
                    if (shop == null) continue;
                    List<BotSeller> sellers = botSellerRepository.findAllByUserId(shop.getSeller().getUserid());
                    if (sellers != null && !sellers.isEmpty()) for (BotSeller seller : sellers) {
                        sellerBot.sendMessage(seller.getChatId(), userMsg.newOrder(
                                seller.getLanguage(), x.getId()
                        ));
                    }
                }
                List<ShopOrder> orders = new ArrayList<>();
                for (ShopOrderResponse datum : response.getData()) {
                    ShopOrder order = shopOrderRepository.findById(datum.getId()).orElse(null);
                    if (order != null) orders.add(order);
                }
                if (orders.isEmpty()) {
                    bot.sendMessage(user.getChatId(), userMsg.emptyOrders(user.getLanguage()), userKyb.menu(user.getLanguage()));
                    return;
                }
                bot.sendMessage(user.getChatId(), "ㅤㅤ", userKyb.backBtn(user.getLanguage()));
                for (ShopOrder order : orders) {
                    if (order != null) {
                        bot.sendMessageLongText(user.getChatId(),
                                userMsg.orderInformation(user.getLanguage(), order),
                                userKyb.orderInformation(user.getLanguage(), order.getStatus(), order.getPkey())
                        );
                    }
                }
                user.setEventCode(CustomerEventCode.ORDER);
                user.setEventCodeLocation(0);
                botCustomerRepository.save(user);
                return;
            }
            if (inArray(text, List.of("❌ Бекор қилиш", "❌ Отменить", "❌ Cancel", "❌ Bekor qilish"))) {
                bot.sendMessage(user.getChatId(), userMsg.cancelOrder(user.getLanguage()));
                start(user);
                return;
            }
            bot.sendMessageLongText(user.getChatId(), userMsg.wrongBtn(user.getLanguage()) + "\n\n" + userMsg.isSuccessCreateOrder(
                    user, refreshCreateOrder(user)
            ), userKyb.isSuccessCreateOrder(user.getLanguage()));
            return;
        }

    }

    private boolean inArray(String text, List<String> words) {
        for (String word : words) {
            if (word.equals(text))
                return true;
        }
        return false;
    }

    private List<CreateShopOrderRequestDto> refreshCreateOrder(BotCustomer user) {
        List<CreateShopOrderRequestDto> orderRequest = createOrderRequest(user);

        for (CreateShopOrderRequestDto req : orderRequest) {
            BigDecimal price = BigDecimal.ZERO;
            if (user.getDeliveryType().equals("PICKUP")) req.setDeliveryType(DeliveryType.PICKUP);
            else {
                Shop shop = shopRepository.findById(req.getShopId()).orElse(null);
                if (shop == null) continue;
                LocationStatus successDataCustomer = locationService.getAddressWithStatus(user.getLatitude(), user.getLongitude());
                LocationStatus successDataShop = locationService.getAddressWithStatus(shop.getLatitude(), shop.getLongitude());
                if (successDataShop.isSuccess() && successDataCustomer.isSuccess()) {
                    AddressDto currentCustomerAddress = successDataCustomer.getAddress();
                    AddressDto shopAddress = successDataShop.getAddress();
                    DeliveryType deliveryType;
                    req.setDeliveryType(null);
                    if (shopAddress.getCityEnum() == null || currentCustomerAddress.getCityEnum() == null)
                        deliveryType = DeliveryType.DELIVERY_OUTSIDE;
                    else
                        deliveryType = shopAddress.getCityEnum() == currentCustomerAddress.getCityEnum() ?
                                DeliveryType.DELIVERY_INSIDE : DeliveryType.DELIVERY_OUTSIDE;
                    if (deliveryType == DeliveryType.DELIVERY_INSIDE) {
                        if (shop.getHasDelivery()) {
                            req.setDeliveryType(deliveryType);
                        }
                    }
                    if (deliveryType == DeliveryType.DELIVERY_OUTSIDE) {
                        if (shop.getHasDelivery()) {
                            req.setDeliveryType(deliveryType);
                        }
                    }
                } else return List.of();
            }
            req.setPhone(user.getHelperPhone());

        }
        return orderRequest;
    }

    private void handleBasket(BotCustomer user) {

        List<Basket> baskets = basketRepository.myBaskets(user.getPkey());
        bot.sendMessage(
                user.getChatId(),
                userMsg.myBaskets(user.getLanguage(), baskets), userKyb.myBaskets(user.getLanguage(), baskets)
        );
    }

    private String categoryBackBtn(Language language) {
        return switch (language) {
            case UZBEK -> "🔙 Orqaga";
            case CYRILLIC -> "🔙 Орқага";
            case RUSSIAN -> "🔙 Назад";
            case ENGLISH -> "🔙 Back";
        };
    }

    private String backText(Language language) {
        return switch (language) {
            case UZBEK -> "🔙 Orqaga";
            case CYRILLIC -> "🔙 Орқага";
            case RUSSIAN -> "🔙 Назад";
            case ENGLISH -> "🔙 Back";
        };
    }

    private String basketBtn(Language language) {
        return switch (language) {
            case UZBEK -> "📥 Savat";
            case CYRILLIC -> "📥 Сават";
            case RUSSIAN -> "📥 Корзина";
            case ENGLISH -> "📥 Cart";
        };
    }

    private void start(BotCustomer user) {
        start(user.getChatId(), user.getFirstName(), user.getLastName(), user.getUsername());
    }

    public void requestContact(BotCustomer user, String phoneNumber, Long contactUserId) {
        Integer location = user.getEventCodeLocation();
        if (location == 1) {
            if (user.getChatId().equals(contactUserId)) {
                phoneNumber = (phoneNumber.charAt(0) == '+' ? phoneNumber : ("+" + phoneNumber));
                user.setTelegramPhone(phoneNumber);
                user.setEventCodeLocation(2);
                botCustomerRepository.save(user);
                bot.sendMessage(user.getChatId(), userMsg.savedContact(user.getLanguage()));
                List<CustomerLocation> locations = customerLocationRepository.myLocation(user.getPkey());
                bot.sendMessage(user.getChatId(),
                        userMsg.sendLocationOrChooseMyLocation(locations.isEmpty(), user.getLanguage()),
                        userKyb.myLocations(user.getLanguage(), locations));
            } else
                bot.sendMessage(user.getChatId(), userMsg.wrongBtn(user.getLanguage()), userKyb.requestContact(user.getLanguage()));
            return;
        }
        if (location == 5) {
            if (!user.getChatId().equals(contactUserId)) {
                bot.sendMessage(user.getChatId(), userMsg.wrongBtn(user.getLanguage()), userKyb.requestContact(user.getLanguage()));
                return;
            }
            phoneNumber = (phoneNumber.charAt(0) == '+' ? phoneNumber : ("+" + phoneNumber));
            user.setTelegramPhone(phoneNumber);
            user.setEventCodeLocation(4);
            botCustomerRepository.save(user);
            bot.sendMessage(user.getChatId(), userMsg.savedContact(user.getLanguage()), true);
            menu(user, "PLACE_ORDER", null, null);
        }
    }

    public void chooseLocation(BotCustomer user, Double latitude, Double longitude) {
        Integer location = user.getEventCodeLocation();
        if (location == 2) {
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            user.setEventCodeLocation(3);
            botCustomerRepository.save(user);
            bot.sendMessage(user.getChatId(),
                    userMsg.isAddedMyLocation(
                            user.getLanguage(), locationService.getAddress(latitude, longitude)
                    ),
                    userKyb.isAddedToMyLocation(user.getLanguage())
            );
            return;
        }
        if (location == 6) {
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            user.setEventCodeLocation(4);
            botCustomerRepository.save(user);
            bot.sendMessage(user.getChatId(), userMsg.savedContact(user.getLanguage()), true);
            menu(user, "PLACE_ORDER", null, null);
        }
    }

    public void menu(BotCustomer user, String data, Integer messageId, String callbackQueryId) {
        Integer location = user.getEventCodeLocation();
        if (location == 4) {
            if (data.equals("CART")) {
                bot.deleteMessage(user.getChatId(), messageId);
                handleBasket(user);
                return;
            }
            if (data.equals("TO_BACK_BASKET")) {
                List<Basket> baskets = basketRepository.myBaskets(user.getPkey());
                bot.editMessageText(user.getChatId(), messageId, userMsg.myBaskets(
                        user.getLanguage(), baskets
                ), userKyb.myBaskets(
                        user.getLanguage(), baskets
                ));
                return;
            }
            if (data.startsWith("DELIVERY_TYPE_")) {
                String deliveryType = data.split("_")[2];
                user.setDeliveryType(deliveryType);

                bot.deleteMessage(user.getChatId(), messageId);
                bot.sendMessage(
                        user.getChatId(), userMsg.enterPhoneNumberForOrder(user.getLanguage()),
                        userKyb.enterPhoneNumberForOrderWithBackBtn(user.getLanguage(), user.getTelegramPhone())
                );
                user.setEventCodeLocation(7);
                botCustomerRepository.save(user);
                   /* LocationStatus successData = locationService.getAddressWithStatus(user.getLatitude(), user.getLongitude());
                    if (successData.isSuccess()) {
                        AddressDto currentCustomerAddress = successData.getAddress();
                        List<CreateShopOrderRequestDto> orderRequest = createOrderRequest(user);
                        for (CreateShopOrderRequestDto q : orderRequest) {
                            Shop shop = shopRepository.findById(q.getShopId()).orElse(null);
                            if (shop == null) continue;
                            DeliveryType type = locationService.getAddressWithStatus(
                                    shop.getLatitude(), shop.getLongitude()
                            ).getAddress().getCityEnum() == currentCustomerAddress.getCityEnum() ? DeliveryType.DELIVERY_INSIDE : DeliveryType.DELIVERY_OUTSIDE;
                            q.setDeliveryType(type);
                        }

                    } else return;*/
                return;
            }
            if (data.equals("PLACE_ORDER")) {
                if (user.getTelegramPhone() == null) {
                    user.setEventCodeLocation(5);
                    if (messageId != null) bot.deleteMessage(user.getChatId(), messageId);
                    bot.sendMessage(user.getChatId(), userMsg.requestContact(user.getLanguage()), userKyb.requestContact(user.getLanguage()));
                    botCustomerRepository.save(user);
                    return;
                }
                if (user.getLatitude() == null || user.getLongitude() == null) {
                    user.setEventCodeLocation(6);
                    if (messageId != null) bot.deleteMessage(user.getChatId(), messageId);
                    List<CustomerLocation> locations = customerLocationRepository.myLocation(user.getPkey());
                    bot.sendMessage(user.getChatId(), userMsg.sendLocationOrChooseMyLocation(locations.isEmpty(), user.getLanguage()), userKyb.myLocations(user.getLanguage(), locations));
                    botCustomerRepository.save(user);
                    return;
                }
                //davomi bor
                if (messageId == null) bot.sendMessage(user.getChatId(), userMsg.chooseDeliveryType(
                        user.getLanguage()
                ), userKyb.chooseDeliveryType(
                        user.getLanguage()
                ));
                else
                    bot.editMessageText(user.getChatId(), messageId, userMsg.chooseDeliveryType(
                                    user.getLanguage()
                            ), userKyb.chooseDeliveryType(user.getLanguage())
                    );
                return;
            }
            if (data.startsWith("PAGE_")) {
                int page = Integer.parseInt(data.split("_")[1]);
                UUID categoryId = UUID.fromString(data.split("_")[2]);
                Category category = categoryRepository.findById(categoryId).orElse(null);
                if (category == null) return;
                List<Product> products = productRepository.findAllByCategoryId(category.getPkey());
                bot.editMessagePhoto(user.getChatId(), messageId, category.getImageUrl(), userMsg.categoryInformation(
                        user.getLanguage(), category, products.size()
                ), userKyb.getProductByCategory(user.getChatId(),
                        products, user.getLanguage(), page
                ));
                return;
            }


            if (data.startsWith("PRODUCT_")) {
                UUID productId = UUID.fromString(data.split("_")[1]);
                Product product = productRepository.findById(productId).orElse(null);
                if (product == null) return;
                ProductType productType = getFirstProductType(product);

                bot.editMessagePhoto(user.getChatId(), messageId, getMainImage(productType).getImageUrl(),
                        userMsg.fullInformationProductAndProductTypes(
                                user.getLanguage(), product, getProductTypesFromProduct(product), getFirstProductType(product)
                        ), userKyb.getProductsAndBasket(user.getLanguage(), getProductTypesFromProduct(product), getFirstProductType(product), 1));
                return;
            }
            if (data.startsWith("view_images_")) {
                bot.deleteMessage(user.getChatId(), messageId);
                UUID productTypeId = UUID.fromString(data.split("_")[2]);
                ProductType productType = productTypeRepository.findById(productTypeId).orElse(null);
                if (productType == null) return;
                List<String> urls = productType.getImages().stream().filter(productTypeImage -> !productTypeImage.getDeleted()).map(ProductTypeImage::getImageUrl).toList();
                bot.sendMediaGroup(user.getChatId(), urls, userKyb.backBtn(user.getLanguage(), productTypeId));
                Product product = productRepository.findById(productType.getProduct().getPkey()).orElse(null);
                if (product == null) return;
                bot.sendPhoto(user.getChatId(), getMainImage(productType).getImageUrl(),
                        userMsg.fullInformationProductAndProductTypes(
                                user.getLanguage(), product, getProductTypesFromProduct(product), productType
                        ), userKyb.getProductsAndBasket(user.getLanguage(), getProductTypesFromProduct(product), productType, 1));
                return;
            }
            if (data.startsWith("back_product_")) {
                UUID productId = UUID.fromString(data.split("_")[2]);
                Product product = productRepository.findById(productId).orElse(null);
                if (product == null) return;
                List<Product> products = productRepository.findAllByCategoryId(product.getCategory().getPkey());
                bot.editMessagePhoto(user.getChatId(), messageId, product.getCategory().getImageUrl(), userMsg.categoryInformation(
                        user.getLanguage(), product.getCategory(), products.size()
                ), userKyb.getProductByCategory(user.getChatId(), products, user.getLanguage(), 0));
                user.setEventCodeLocation(4);
                botCustomerRepository.save(user);
                return;
            }
            if (data.startsWith("select_variant") || data.startsWith("out_of_stock_")) {
                UUID productTypeId = UUID.fromString(data.split("_")[2]);
                ProductType productType = productTypeRepository.findById(productTypeId).orElse(null);
                if (productType == null) return;
                List<ProductType> productTypes = productTypeRepository.findAllByProductId(productType.getProduct().getPkey());
                bot.editMessagePhoto(user.getChatId(), messageId, getMainImage(productType).getImageUrl(),
                        userMsg.fullInformationProductAndProductTypes(
                                user.getLanguage(), productType.getProduct(), productTypes, productType
                        ), userKyb.getProductsAndBasket(user.getLanguage(), productTypes, productType, 1));
            }
            if (data.startsWith("decrease_quantity_") || data.startsWith("increase_quantity_")) {
                int count = Integer.parseInt(data.split("_")[2]);
                UUID productTypeId = UUID.fromString((data.split("_")[3]));
                if (data.startsWith("decrease_quantity_") && count == 0) {
                    bot.answerCallbackQuery(callbackQueryId, userMsg.minimumCountOfProducts(user.getLanguage()), true);
                    return;
                }
                ProductType productType = productTypeRepository.findById(productTypeId).orElse(null);
                if (productType == null) return;
                if (count - 1 == productType.getStock() && data.startsWith("increase_quantity_")) {
                    bot.answerCallbackQuery(callbackQueryId, userMsg.productIsFinished(
                            user.getLanguage(), productType.getStock(), count
                    ), true);
                    return;
                }
                bot.editMessagePhoto(user.getChatId(), messageId, getMainImage(productType).getImageUrl(),
                        userMsg.fullInformationProductAndProductTypes(
                                user.getLanguage(), productType.getProduct(), getProductTypesFromProduct(productType.getProduct()), productType
                        ), userKyb.getProductsAndBasket(
                                user.getLanguage(), getProductTypesFromProduct(productType.getProduct()), productType, count
                        ));
            }
            if (data.startsWith("add_current_basket_")) {
                int quantity = Integer.parseInt(data.split("_")[3]);
                UUID productTypeId = UUID.fromString((data.split("_")[4]));
                ProductType productType = productTypeRepository.findById(productTypeId).orElse(null);
                if (productType == null) return;
                if (quantity > productType.getStock()) {
                    bot.answerCallbackQuery(callbackQueryId, userMsg.productIsFinished(user.getLanguage(), productType.getStock(), quantity), true);
                    return;
                }
                Basket basket = basketRepository.checkBasket(user.getPkey(), productTypeId).orElse(null);
                BigDecimal totalPrice;
                if (basket == null) {
                    basket = new Basket();
                    basket.setCustomer(user);
                    basket.setProductType(productType);
                    basket.setType(BasketType.DRAFT);
                    basket.setQuantity(quantity);
                    totalPrice = getProductTypePrice(productType).multiply(BigDecimal.valueOf(quantity));
                } else {
                    basket.setQuantity(quantity + basket.getQuantity());
                    totalPrice = getProductTypePrice(productType).multiply(BigDecimal.valueOf(basket.getQuantity()));
                }
                basket.setPrice(getProductTypePrice(productType));
                basket.setTotalPrice(totalPrice);
                basketRepository.save(basket);
                Category category = productType.getProduct().getCategory();
                List<Product> products = productRepository.findAllByCategoryId(category.getPkey());
                bot.answerCallbackQuery(callbackQueryId, userMsg.addedToBasket(user.getLanguage()), true);
                bot.editMessagePhoto(user.getChatId(), messageId, category.getImageUrl(), userMsg.categoryInformation(
                        user.getLanguage(), category, products.size()
                ), userKyb.getProductByCategory(user.getChatId(),
                        products, user.getLanguage(), 0
                ));
                return;
            }


            if (data.equals("BACK_TO_MAIN") || data.equals("CLEAR_BASKET") || data.equals("CONTINUE_SHOPPING")) {
                if (data.equals("CLEAR_BASKET")) {
                    bot.answerCallbackQuery(callbackQueryId, userMsg.clearBasket(user.getLanguage()), true);
                    clearBasket(user);
                }
                bot.deleteMessage(user.getChatId(), messageId);
                List<Category> categories;
                if (user.getCategoryId() == null) {
                    categories = categoryRepository.getAllCategories();
                } else {
                    Category category = categoryRepository.findById(user.getCategoryId()).orElse(null);
                    if (category == null) return;
                    categories = category.getChildren().stream()
                            .filter(Category::getActive)
                            .toList();
                }
                bot.sendMessage(user.getChatId(), userMsg.productText(user.getLanguage()), userKyb.getAllCategories(user.getChatId(),
                        user.getLanguage(), categories
                ));
                return;
            }
/*
            if (data.startsWith("DECREASE_")) {
                UUID basketId = UUID.fromString(data.split("_")[1]);
                Basket basket = basketRepository.findById(basketId).orElse(null);
                if (basket == null) return;
                if (basket.getProductType().getStock() < basket.getQuantity()) {
                    basket.setQuantity(basket.getProductType().getStock());
                }else{
                    if (basket.getQuantity() == 1) basket.setType(BasketType.FINISHED);
                    else {
                        basket.setQuantity(basket.getQuantity() - 1);
                    }
                }

                basketRepository.save(basket);
                List<Basket> baskets = basketRepository.myBaskets(user.getPkey());
                bot.editMessageText(user.getChatId(), messageId, userMsg.myBaskets(user.getLanguage(), baskets), userKyb.myBaskets(user.getLanguage(), baskets));
            }
*/
            if (data.startsWith("DECREASE_")) {

                String[] parts = data.split("_");
                if (parts.length != 2) {
                    return; // noto‘g‘ri callback format
                }

                UUID basketId;
                try {
                    basketId = UUID.fromString(parts[1]);
                } catch (IllegalArgumentException e) {
                    return; // noto‘g‘ri UUID
                }

                Basket basket = basketRepository.findById(basketId).orElse(null);
                if (basket == null || basket.getType() == BasketType.FINISHED) {
                    return;
                }

                int currentQuantity = basket.getQuantity();
                int stock = basket.getProductType().getStock();

                if (currentQuantity > stock) {
                    bot.answerCallbackQuery(callbackQueryId, userMsg.productIsFinished(user.getLanguage(), stock, currentQuantity), true);
                    basket.setQuantity(stock);
                }
                // Agar 1 bo‘lsa → FINISHED
                else if (currentQuantity <= 1) {
                    basket.setType(BasketType.FINISHED);
                }
                // Oddiy decrease
                else {
                    basket.setQuantity(currentQuantity - 1);
                }

                basketRepository.save(basket);

                List<Basket> baskets = basketRepository.myBaskets(user.getPkey());

                bot.editMessageText(
                        user.getChatId(),
                        messageId,
                        userMsg.myBaskets(user.getLanguage(), baskets),
                        userKyb.myBaskets(user.getLanguage(), baskets)
                );
            }
/*
            if (data.startsWith("INCREASE_")) {
                UUID basketId = UUID.fromString(data.split("_")[1]);
                Basket basket = basketRepository.findById(basketId).orElse(null);
                if (basket == null) return;
                if (basket.getProductType().getStock() <= basket.getQuantity()) {
                    basket.setQuantity(basket.getProductType().getStock());
                } else {
                    basket.setQuantity(basket.getQuantity() + 1);
                }
                basketRepository.save(basket);
                List<Basket> baskets = basketRepository.myBaskets(user.getPkey());
                bot.editMessageText(user.getChatId(), messageId, userMsg.myBaskets(user.getLanguage(), baskets), userKyb.myBaskets(user.getLanguage(), baskets));
                return;
            }
*/
            if (data.startsWith("INCREASE_")) {

                String[] parts = data.split("_");
                if (parts.length != 2) {
                    return; // noto‘g‘ri callback format
                }

                UUID basketId;
                try {
                    basketId = UUID.fromString(parts[1]);
                } catch (IllegalArgumentException e) {
                    return; // noto‘g‘ri UUID
                }

                Basket basket = basketRepository.findById(basketId).orElse(null);
                if (basket == null || basket.getType() == BasketType.FINISHED) {
                    return;
                }

                int currentQuantity = basket.getQuantity();
                int stock = basket.getProductType().getStock();

                // Stock limitdan oshmasin
                if (currentQuantity < stock) {
                    basket.setQuantity(currentQuantity + 1);
                } else {
                    bot.answerCallbackQuery(callbackQueryId, userMsg.productIsFinished(user.getLanguage(), stock, currentQuantity), true);
                    basket.setQuantity(stock); // defensive
                }

                basketRepository.save(basket);

                List<Basket> baskets = basketRepository.myBaskets(user.getPkey());

                bot.editMessageText(
                        user.getChatId(),
                        messageId,
                        userMsg.myBaskets(user.getLanguage(), baskets),
                        userKyb.myBaskets(user.getLanguage(), baskets)
                );
            }
            if (data.startsWith("QUANTITY_")) {
                bot.answerCallbackQuery(callbackQueryId, data.split("_")[1], false);
                return;
            }
        }

    }

    private BigDecimal getProductTypePrice(ProductType productType) {
        if (productType == null) return BigDecimal.ZERO;

        BigDecimal basePrice = productType.getPrice();
        if (basePrice == null) basePrice = BigDecimal.ZERO;
        Product product = productType.getProduct();
        if (product != null && product.getDiscount() != null) {
            Discount discount = product.getDiscount();
            if (discount.getType() == DiscountType.FIXED) {
                basePrice = basePrice.subtract(discount.getValue());
            } else if (discount.getType() == DiscountType.PERCENT) {
                BigDecimal percent = discount.getValue().divide(BigDecimal.valueOf(100));
                basePrice = basePrice.subtract(basePrice.multiply(percent));
            }
        }

        if (basePrice.compareTo(BigDecimal.ZERO) < 0) {
            basePrice = BigDecimal.ZERO;
        }
        return basePrice;
    }


    private ProductType getFirstProductType(Product product) {
        return productTypeRepository.findAllByProductId(product.getPkey()).get(0);
    }

    private ProductTypeImage getMainImage(ProductType productType) {
        List<ProductTypeImage> list = productType.getImages().stream().filter(productTypeImage -> !productTypeImage.getDeleted()).toList();
        for (ProductTypeImage image : list) {
            if (image.getMain()) return image;
        }
        return list.get(0);
    }

    private List<ProductType> getProductTypesFromProduct(Product product) {
        return productTypeRepository.findAllByProductId(product.getPkey());
    }

    public void viewProduct(BotCustomer user, UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;
        if (product.getShop().getSeller().getPlanExpiresAt() == null ||
                product.getShop().getSeller().getPlanExpiresAt().isBefore(LocalDateTime.now()) ||
                product.getStatus() != ProductStatus.OPEN
        ) {
            List<Category> categories = product.getCategory().getParent().getChildren().stream().filter(Category::getActive).toList();
            bot.sendMessage(user.getChatId(), userMsg.notActiveProduct(product, user.getLanguage()),
                    userKyb.getAllCategories(user.getChatId(), user.getLanguage(), categories));
            user.setEventCodeLocation(4);
            user.setEventCode(CustomerEventCode.MENU);
            botCustomerRepository.save(user);
            return;
        }

        List<Category> categories;
        if (product.getCategory().getParent() != null) {
            categories = product.getCategory().getParent().getChildren().stream().filter(Category::getActive).toList();
        } else {
            categories = categoryRepository.getAllCategories();
        }
        bot.sendMessage(user.getChatId(), userMsg.productText(user.getLanguage()), userKyb.getAllCategories(user.getChatId(), user.getLanguage(), categories));
        ProductType productType = getFirstProductType(product);
        bot.sendPhoto(user.getChatId(), getMainImage(productType).getImageUrl(),
                userMsg.fullInformationProductAndProductTypes(
                        user.getLanguage(), product, getProductTypesFromProduct(product), getFirstProductType(product)
                ), userKyb.getProductsAndBasket(user.getLanguage(), getProductTypesFromProduct(product), getFirstProductType(product), 1));
        user.setEventCodeLocation(4);
        user.setLatitude(null);
        user.setLongitude(null);
        user.setEventCode(CustomerEventCode.MENU);
        botCustomerRepository.save(user);
    }

    private void clearBasket(BotCustomer user) {
        for (Basket basket : basketRepository.myBaskets(user.getPkey())) {
            basket.setType(BasketType.FINISHED);
            basketRepository.save(basket);
        }
    }

    public void exception(BotCustomer user, String message) {
        bot.sendMessage(user.getChatId(), message);
    }

    public void exception(Long chatId, String message) {
        bot.sendMessage(chatId, message);
    }

    private List<CreateShopOrderRequestDto> createOrderRequest(BotCustomer user) {
        List<Basket> baskets = basketRepository.myBaskets(user.getPkey());
        if (baskets == null || baskets.isEmpty()) {
            return List.of();
        }
        Map<UUID, List<Basket>> groupedByShop = baskets.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getProductType()
                                .getProduct()
                                .getShop()
                                .getPkey()
                ));
        List<CreateShopOrderRequestDto> orders = new ArrayList<>();
        for (Map.Entry<UUID, List<Basket>> entry : groupedByShop.entrySet()) {
            UUID shopId = entry.getKey();
            List<Basket> shopBaskets = entry.getValue();
            CreateShopOrderRequestDto order = new CreateShopOrderRequestDto();
            order.setShopId(shopId);
            order.setChatId(user.getChatId());
            order.setPhone(user.getHelperPhone());
            order.setDeliveryType(null);
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<CreateOrderItemRequestDto> items = new ArrayList<>();
            for (Basket basket : shopBaskets) {
                CreateOrderItemRequestDto item = new CreateOrderItemRequestDto();
                item.setProductTypeId(basket.getProductType().getPkey());
                item.setQuantity(basket.getQuantity());
                item.setPrice(basket.getPrice());
                item.setTotalPrice(basket.getTotalPrice());
                items.add(item);
                totalAmount = totalAmount.add(basket.getTotalPrice());
            }
            order.setItems(items);
            order.setLatitude(user.getLatitude());
            order.setLongitude(user.getLongitude());
            order.setAddress(locationService.getAddress(
                    user.getLatitude(), user.getLongitude()
            ));
            order.setTotalAmount(totalAmount);
            orders.add(order);
        }

        return orders;
    }

    public void order(BotCustomer user, String text) {
        Integer location = user.getEventCodeLocation();
        if (location == 0) {
//            if (text.equals(backText(user.getLanguage()))) {
            start(user);
//                return;
//            }
        } else if (location == 1) {
            if (text.equals(backText(user.getLanguage()))) {
                start(user);
                return;
            }
            bot.sendMessage(user.getChatId(), userMsg.requiredImage(user.getLanguage()), userKyb.backBtn(user.getLanguage()));
        }
    }

    public void order(BotCustomer user, String data, Integer messageId, String callbackQueryId) {
        Integer location = user.getEventCodeLocation();
        if (location == 0) {
            UUID orderId = UUID.fromString(data.split(":")[1]);
            ShopOrder order = shopOrderRepository.findById(orderId).orElse(null);
            if (order == null)
                return;
            switch (data.split(":")[0]) {
                case "pay_order" -> {
                    bot.answerCallbackQuery(callbackQueryId, "Ok", false);
                    bot.sendPhoto(user.getChatId(), order.getSeller().getCardImageUrl(), userMsg.payPrice(
                            user.getLanguage(), order, order.getSeller()
                    ), userKyb.backBtn(user.getLanguage()));
                    user.setShopOrderId(orderId);
                    user.setEventCodeLocation(1);
                    botCustomerRepository.save(user);
                    return;

                }
                case "cancel_order" -> {
                    bot.editMessageText(user.getChatId(), messageId,
                            userMsg.isCancelOrderRequest(
                                    user.getLanguage(), order
                            ), userKyb.isCancelOrderRequest(
                                    user.getLanguage(), order
                            )
                    );
                    return;
                }
                case "go_to_shop" -> {
                    Shop shop = order.getShop();
                    bot.editMessageText(user.getChatId(), messageId,
                            userMsg.shopInfo(user.getLanguage(), shop)
                            , userKyb.shopInfo(user.getLanguage(), order));
                    return;
                }
                case "CONFIRM_CANCEL_ORDER" -> {
                    order.setStatus(ShopOrderStatus.CANCELLED);
                    List<OrderItem> list = orderItemRepository.findByShopOrder(order);
                    for (OrderItem item : list) {
                        ProductType productType = item.getProductType();
                        productType.setStock(item.getQuantity() + productType.getStock());
                        productTypeRepository.save(productType);
                    }
                    shopOrderRepository.save(order);
                    Seller seller = order.getShop().getSeller();
                    bot.answerCallbackQuery(callbackQueryId, userMsg.cancelledOrder(user.getLanguage()), true);
                    bot.deleteMessage(user.getChatId(), messageId);
                    for (BotSeller botSeller : botSellerRepository.findAllByUserId(seller.getUserid())) {
                        sellerBot.sendMessage(user.getChatId(), userMsg.cancelOrderToSeller(
                                botSeller.getLanguage(), order
                        ));
                    }
                    return;
                }
                case "DELETE_ACTION" -> {
                    bot.answerCallbackQuery(callbackQueryId, "Ok", false);
                    bot.deleteMessage(user.getChatId(), messageId);
                }
                case "SHOP_GET_LOCATION" -> {
                    String shopName = switch (user.getLanguage()) {
                        case UZBEK -> order.getShop().getNameUz();
                        case RUSSIAN -> order.getShop().getNameRu();
                        case ENGLISH -> order.getShop().getNameEn();
                        case CYRILLIC -> order.getShop().getNameCyr();
                    };
                    bot.answerCallbackQuery(callbackQueryId, "Ok", false);
                    bot.sendVenue(
                            user.getChatId(),
                            order.getShop().getLatitude(),
                            order.getShop().getLongitude(),
                            shopName, order.getShop().getAddress(),
                            userKyb.deleteBtn(user.getLanguage(), order.getPkey())
                    );
                }
                case "BACK_TO_ORDER" -> {
                    bot.editMessageText(user.getChatId(), messageId,
                            userMsg.orderInformation(
                                    user.getLanguage(), order
                            ), userKyb.orderInformation(
                                    user.getLanguage(), order.getStatus(), order.getPkey()
                            )
                    );

                    return;
                }
                case "message_seller" -> {
                    bot.answerCallbackQuery(callbackQueryId, "Ok", false);
                    for (BotSeller botSeller : botSellerRepository.findAllByUserId(order.getSeller().getUserid())) {
                        bot.sendContact(user.getChatId(), botSeller.getTelegramPhone(), botSeller.getFirstName(), botSeller.getLastName());
                    }
                    return;
                }
            }
            bot.answerCallbackQuery(callbackQueryId, data, true);
        }
    }

    public void order(BotCustomer user, List<String> photoFileIds) {
        String fileId = photoFileIds.get(photoFileIds.size() - 1);
        String savedImage = savePaymentImage(bot.getBotToken(), fileId);
        if (savedImage == null) {
            bot.sendMessage(user.getChatId(), userMsg.notUploadedImage(user.getLanguage()), userKyb.backBtn(user.getLanguage()));
            return;
        }
        ShopOrder order = shopOrderRepository.findById(user.getShopOrderId()).orElse(null);
        if (order == null) return;
        order.setPaymentImageUrl(telegramProperties.getBaseUrl() + "/" + savedImage);
        order.setUpdatedAt(LocalDateTime.now());
        shopOrderRepository.save(order);
        bot.sendMessage(user.getChatId(), userMsg.savedImage(user.getLanguage()), true);
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

            File dir = new File("uploads/payment");
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
            return "uploads/payment/" + filename;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void basket(BotCustomer user) {
        if (user.getTelegramPhone() == null) {
            user.setEventCodeLocation(5);
            bot.sendMessage(user.getChatId(), userMsg.requestContact(user.getLanguage()), userKyb.requestContact(user.getLanguage()));
            botCustomerRepository.save(user);
            return;
        }
        if (user.getLatitude() == null || user.getLongitude() == null) {
            user.setEventCodeLocation(6);
            List<CustomerLocation> locations = customerLocationRepository.myLocation(user.getPkey());
            bot.sendMessage(user.getChatId(), userMsg.sendLocationOrChooseMyLocation(locations.isEmpty(), user.getLanguage()), userKyb.myLocations(user.getLanguage(), locations));
            botCustomerRepository.save(user);
            return;
        }
        bot.sendMessage(user.getChatId(), userMsg.chooseDeliveryType(
                user.getLanguage()
        ), userKyb.chooseDeliveryType(
                user.getLanguage()
        ));
        user.setEventCodeLocation(4);
        botCustomerRepository.save(user);
        return;
    }
}
