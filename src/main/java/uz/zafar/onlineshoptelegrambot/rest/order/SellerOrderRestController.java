package uz.zafar.onlineshoptelegrambot.rest.order;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;
import uz.zafar.onlineshoptelegrambot.bot.kyb.user.UserKyb;
import uz.zafar.onlineshoptelegrambot.bot.msg.UserMsg;
import uz.zafar.onlineshoptelegrambot.config.TelegramProperties;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.OrderItem;
import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.repositories.OrderItemRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.ShopOrderRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.order.response.OrderItemResponse;
import uz.zafar.onlineshoptelegrambot.dto.order.response.ShopOrderResponse;
import uz.zafar.onlineshoptelegrambot.dto.shop.response.ShopResponse;
import uz.zafar.onlineshoptelegrambot.mapper.ProductTypeMapper;
import uz.zafar.onlineshoptelegrambot.mapper.ShopMapper;
import uz.zafar.onlineshoptelegrambot.service.ApiLocationService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("api/seller/order")
public class SellerOrderRestController {
    private final BotSellerRepository botSellerRepository;
    private final SellerRepository sellerRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final ShopRepository shopRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductTypeMapper productTypeMapper;
    private final TelegramBot sellerBot;
    private final TelegramBot userBot;
    private final ShopMapper shopMapper;
    private final ApiLocationService locationService;
    private final UserMsg userMsg;
    private final UserKyb userKyb;
    private final TelegramProperties telegramProperties;
    private final ProductTypeRepository productTypeRepository;

    public SellerOrderRestController(BotSellerRepository botSellerRepository,
                                     SellerRepository sellerRepository,
                                     ShopOrderRepository shopOrderRepository,
                                     ShopRepository shopRepository, OrderItemRepository orderItemRepository,
                                     ProductTypeMapper productTypeMapper,
                                     @Qualifier("seller") TelegramBot sellerBot,
                                     @Qualifier("customer") TelegramBot userBot, ShopMapper shopMapper, ApiLocationService locationService, UserMsg userMsg, UserKyb userKyb, TelegramProperties telegramProperties, ProductTypeRepository productTypeRepository) {
        this.botSellerRepository = botSellerRepository;
        this.sellerRepository = sellerRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.shopRepository = shopRepository;
        this.orderItemRepository = orderItemRepository;
        this.productTypeMapper = productTypeMapper;
        this.sellerBot = sellerBot;
        this.userBot = userBot;
        this.shopMapper = shopMapper;
        this.locationService = locationService;
        this.userMsg = userMsg;
        this.userKyb = userKyb;
        this.telegramProperties = telegramProperties;
        this.productTypeRepository = productTypeRepository;
    }

    @PostMapping("change-status/{status}/{orderId}")
    public ResponseDto<?> changeStatus(@PathVariable("status") ShopOrderStatus status, @PathVariable("orderId") UUID orderId) {
        ShopOrder order = shopOrderRepository.findById(orderId).orElse(null);
        if (order == null) return ResponseDto.error(ErrorCode.ERROR);
        order.setStatus(status);
        if (status == ShopOrderStatus.CANCELLED) {
            List<OrderItem> list = orderItemRepository.findByShopOrder(order);
            for (OrderItem item : list) {
                ProductType productType = item.getProductType();
                productType.setStock(item.getQuantity() + productType.getStock());
                productTypeRepository.save(productType);
            }
        }
        order.setUpdatedAt(LocalDateTime.now());
        shopOrderRepository.save(order);
        return ResponseDto.success();
    }

    @PostMapping("{chatId}/send-contact/{orderId}")
    public ResponseDto<?> sendContact(
            @PathVariable("orderId") UUID orderId,
            @PathVariable("chatId") Long chatId
    ) {
        ShopOrder order = shopOrderRepository.findById(orderId).orElse(null);
        if (order == null) return ResponseDto.error(ErrorCode.ERROR);
        BotCustomer customer = order.getCustomer();
        sellerBot.sendContact(
                chatId, customer.getTelegramPhone(),
                customer.getFirstName(), customer.getLastName()
        );
        return ResponseDto.success();
    }

    @GetMapping("/{chatId}/get-all-orders")
    public ResponseEntity<?> allOrders(@PathVariable("chatId") Long chatId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseEntity.ok(
                ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER)
        );
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseEntity.ok(
                ResponseDto.error(ErrorCode.NOT_FOUND_SELLER)
        );
        List<Shop> shops = shopRepository.findAllBySellerId(seller.getPkey());
        List<ShopWithOrderResponse> response = new ArrayList<>();
        for (Shop shop : shops) {
            ShopWithOrderResponse x = new ShopWithOrderResponse();
            x.setShop(shopMapper.toResponse(shop));
            List<ShopOrder> orders = shopOrderRepository.findAllByShopId(shop.getPkey());
            List<ShopOrderResponse> responseOrders = new ArrayList<>();
            for (ShopOrder order : orders) {
                ShopOrderResponse y = new ShopOrderResponse();
                y.setId(order.getPkey());
                y.setShop(shopMapper.toResponse(shop));
                y.setSeller(order.getSeller());
                y.setPhone(order.getPhone());
                y.setStatus(order.getStatus());
                y.setDeliveryType(order.getDeliveryType());
                y.setTotalAmount(order.getTotalAmount());
                y.setCreatedAt(order.getCreatedAt());
                y.setUpdatedAt(order.getUpdatedAt());
                y.setClient(order.getCustomer());
                y.setDeliveryPrice(order.getDeliveryPrice() == null ? BigDecimal.ZERO : order.getDeliveryPrice());
                y.setLatitude(order.getLatitude());
                y.setLongitude(order.getLongitude());
                y.setAddress(order.getAddress());
                List<OrderItem> list = orderItemRepository.findByShopOrder(order);
                List<OrderItemResponse> r = new ArrayList<>();
                for (OrderItem orderItem : list) {
                    r.add(new OrderItemResponse(
                            orderItem.getPkey(),
                            order.getPkey(),
                            productTypeMapper.toResponse(orderItem.getProductType()),
                            orderItem.getQuantity(),
                            orderItem.getPrice(),
                            orderItem.getTotalPrice()
                    ));
                }
                y.setItems(r);
                responseOrders.add(y);
            }
            x.setOrders(responseOrders);
            response.add(x);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/order-fish/{orderId}")
    public ResponseDto<?> getOrderFish(@PathVariable("orderId") UUID orderId) {
        ShopOrder order = shopOrderRepository.findById(orderId).orElse(null);
        if (order == null) return ResponseDto.error(ErrorCode.ERROR);
        if (order.getPaymentImageUrl() == null) {
            return ResponseDto.error(ErrorCode.ERROR);
        }

        return ResponseDto.success(new OrderFishImage(
                order.getPaymentImageUrl(), orderId
        ));
    }

    @PostMapping("confirmed/{orderId}")
    public ResponseDto<?> confirmedPayment(@PathVariable("orderId") UUID orderId) {
        ShopOrder order = shopOrderRepository.findById(orderId).orElse(null);
        if (order == null) return ResponseDto.error(ErrorCode.ERROR);
        order.setStatus(ShopOrderStatus.PAYMENT_COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());
        shopOrderRepository.save(order);
        BotCustomer user = order.getCustomer();
        userBot.sendPhoto(
                user.getChatId(),
                order.getPaymentImageUrl(),
                userMsg.confirmFish(user.getLanguage(), order)
        );
        return ResponseDto.success();
    }

    @PostMapping("cancel/{orderId}")
    public ResponseDto<?> cancelPayment(@PathVariable("orderId") UUID orderId) {
        ShopOrder order = shopOrderRepository.findById(orderId).orElse(null);
        if (order == null) return ResponseDto.error(ErrorCode.ERROR);
        order.setStatus(ShopOrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        shopOrderRepository.save(order);
        BotCustomer user = order.getCustomer();
        userBot.sendMessage(
                user.getChatId(),
                userMsg.cancelOrder(user.getLanguage(), order)
        );
        return ResponseDto.success();
    }

    @PostMapping("/{chatId}/get-location/{latitude}/{longitude}")
    public ResponseDto<?> sendLocation(
            @PathVariable("chatId") Long chatId,
            @PathVariable("latitude") Double latitude,
            @PathVariable("longitude") Double longitude
    ) {
        sellerBot.sendVenue(chatId, latitude, longitude, "location", locationService.getAddress(latitude, longitude));
        return ResponseDto.success();
    }


    public static class ShopWithOrderResponse {
        private ShopResponse shop;
        private List<ShopOrderResponse> orders;

        public ShopResponse getShop() {
            return shop;
        }

        public void setShop(ShopResponse shop) {
            this.shop = shop;
        }

        public List<ShopOrderResponse> getOrders() {
            return orders;
        }

        public void setOrders(List<ShopOrderResponse> orders) {
            this.orders = orders;
        }
    }

    public static class OrderFishImage {
        private String imageUrl;
        private UUID shopOrderId;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public OrderFishImage(String imageUrl, UUID shopOrderId) {
            this.imageUrl = imageUrl;
            this.shopOrderId = shopOrderId;
        }

        public UUID getShopOrderId() {
            return shopOrderId;
        }

        public void setShopOrderId(UUID shopOrderId) {
            this.shopOrderId = shopOrderId;
        }
    }
}
