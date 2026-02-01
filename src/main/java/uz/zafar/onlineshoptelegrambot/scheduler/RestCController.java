package uz.zafar.onlineshoptelegrambot.scheduler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.order.Basket;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.DeliveryType;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BasketRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.gson.AddressDto;
import uz.zafar.onlineshoptelegrambot.dto.gson.LocationStatus;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateOrderItemRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateShopOrderRequestDto;
import uz.zafar.onlineshoptelegrambot.service.ApiLocationService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/customer/test")
public class RestCController {
    private final BasketRepository basketRepository;
    private final BotCustomerRepository botCustomerRepository;
    private final ApiLocationService locationService;
    private final ShopRepository shopRepository;

    public RestCController(BasketRepository basketRepository, BotCustomerRepository botCustomerRepository, ApiLocationService locationService, ShopRepository shopRepository) {
        this.basketRepository = basketRepository;
        this.botCustomerRepository = botCustomerRepository;
        this.locationService = locationService;
        this.shopRepository = shopRepository;
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<?> example(@PathVariable("chatId") Long chatId) {
        BotCustomer botCustomer = botCustomerRepository.checkUser(chatId).orElse(null);
        if (botCustomer == null) return ResponseEntity.ok(ResponseDto.error(ErrorCode.ERROR));
        return ResponseEntity.ok(refreshCreateOrder(
                botCustomer
        ));
    }

    private List<CreateShopOrderRequestDto> refreshCreateOrder(BotCustomer user) {
        List<CreateShopOrderRequestDto> orderRequest = getOrders(user);
        for (CreateShopOrderRequestDto req : orderRequest) {
            if (user.getDeliveryType().equals("PICKUP")) req.setDeliveryType(DeliveryType.PICKUP);
            else {
                Shop shop = shopRepository.findById(req.getShopId()).orElse(null);
                if (shop == null) continue;
                LocationStatus successDataCustomer = locationService.getAddressWithStatus(user.getLatitude(), user.getLongitude());
                LocationStatus successDataShop = locationService.getAddressWithStatus(shop.getLatitude(), shop.getLongitude());
                if (successDataShop.isSuccess() && successDataCustomer.isSuccess()) {
                    AddressDto currentCustomerAddress = successDataCustomer.getAddress();
                    AddressDto shopAddress = successDataCustomer.getAddress();
                    DeliveryType deliveryType = (shopAddress.getCityEnum() != null && currentCustomerAddress.getCityEnum() != null && shopAddress.getCityEnum() == currentCustomerAddress.getCityEnum()) ?
                            DeliveryType.DELIVERY_INSIDE : DeliveryType.DELIVERY_OUTSIDE;
                    if (shopAddress.getCityEnum() == null || currentCustomerAddress.getCityEnum() != null)
                        deliveryType = DeliveryType.DELIVERY_OUTSIDE;
                    req.setDeliveryType(deliveryType);
                    req.setPhone(user.getHelperPhone());
                } else return List.of();
            }
            req.setPhone(user.getHelperPhone());
        }
        return orderRequest;
    }

    @GetMapping("/location/{lat}/{lon}")
    public ResponseEntity<?> exampleLocation(@PathVariable("lat") double lat, @PathVariable("lon") double lon) {
        return ResponseEntity.ok(locationService.getAddressWithStatus(lat, lon));
    }

    private List<CreateShopOrderRequestDto> getOrders(BotCustomer user) {
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
            order.setPhone(null);
            order.setDeliveryType(null); // yoki default

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
            order.setTotalAmount(totalAmount);

            orders.add(order);
        }

        return orders;
    }
}
