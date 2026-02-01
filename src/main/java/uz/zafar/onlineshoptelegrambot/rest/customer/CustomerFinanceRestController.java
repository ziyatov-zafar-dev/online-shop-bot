package uz.zafar.onlineshoptelegrambot.rest.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.order.OrderItem;
import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.repositories.OrderItemRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.ShopOrderRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.order.response.OrderItemResponse;
import uz.zafar.onlineshoptelegrambot.dto.order.response.ShopOrderResponse;
import uz.zafar.onlineshoptelegrambot.mapper.ProductTypeMapper;
import uz.zafar.onlineshoptelegrambot.mapper.ShopMapper;
import uz.zafar.onlineshoptelegrambot.rest.order.SellerOrderRestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("api/customer/finance")
public class CustomerFinanceRestController {
    private final BotSellerRepository botSellerRepository;
    private final SellerRepository sellerRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final ProductTypeMapper productTypeMapper;

    public CustomerFinanceRestController(BotSellerRepository botSellerRepository, SellerRepository sellerRepository, ShopOrderRepository shopOrderRepository, OrderItemRepository orderItemRepository, ShopRepository shopRepository, ShopMapper shopMapper, ProductTypeMapper productTypeMapper) {
        this.botSellerRepository = botSellerRepository;
        this.sellerRepository = sellerRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
        this.productTypeMapper = productTypeMapper;
    }

    @GetMapping("/{chatId}/get-all-finance")
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
        List<SellerOrderRestController.ShopWithOrderResponse> response = new ArrayList<>();
        for (Shop shop : shops) {
            SellerOrderRestController.ShopWithOrderResponse x = new SellerOrderRestController.ShopWithOrderResponse();
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
}
