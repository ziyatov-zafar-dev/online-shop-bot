package uz.zafar.onlineshoptelegrambot.service.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.zafar.onlineshoptelegrambot.botservice.UsersTelegramBotFunction;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.OrderItem;
import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.DeliveryType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.repositories.OrderItemRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.ShopOrderRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateOrderItemRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateShopOrderRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.order.response.OrderItemResponse;
import uz.zafar.onlineshoptelegrambot.dto.order.response.ShopOrderResponse;
import uz.zafar.onlineshoptelegrambot.dto.shop.response.ShopResponse;
import uz.zafar.onlineshoptelegrambot.mapper.ProductMapper;
import uz.zafar.onlineshoptelegrambot.mapper.ProductTypeMapper;
import uz.zafar.onlineshoptelegrambot.service.ApiLocationService;
import uz.zafar.onlineshoptelegrambot.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final ShopOrderRepository shopOrderRepository;
    private final ShopRepository shopRepository;
    private final SellerRepository sellerRepository;
    private final ApiLocationService locationService;
    private final ProductTypeRepository productTypeRepository;
    private final BotCustomerRepository botCustomerRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductTypeMapper productTypeMapper;
    private final ProductMapper productMapper;

    public OrderServiceImpl(ShopOrderRepository shopOrderRepository, ShopRepository shopRepository, SellerRepository sellerRepository, ApiLocationService locationService, ProductTypeRepository productTypeRepository, BotCustomerRepository botCustomerRepository, OrderItemRepository orderItemRepository, ProductTypeMapper productTypeMapper, ProductMapper productMapper) {
        this.shopOrderRepository = shopOrderRepository;
        this.shopRepository = shopRepository;
        this.sellerRepository = sellerRepository;
        this.locationService = locationService;
        this.productTypeRepository = productTypeRepository;
        this.botCustomerRepository = botCustomerRepository;
        this.orderItemRepository = orderItemRepository;
        this.productTypeMapper = productTypeMapper;
        this.productMapper = productMapper;
    }

    @Override
    public ResponseDto<List<ShopOrderResponse>> myClientOrders(Long chatId) {

        BotCustomer customer = botCustomerRepository
                .checkUser(chatId)
                .orElse(null);

        if (customer == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER);
        }

        List<ShopOrder> orders =
                shopOrderRepository.findAllByCustomer_Pkey(customer.getPkey());

        if (orders.isEmpty()) {
            return ResponseDto.success(List.of());
        }

        List<ShopOrderResponse> responseList = orders.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseDto.success(responseList);
    }


    @Override
    public ResponseDto<List<ShopOrderResponse>> createOrder(
            List<CreateShopOrderRequestDto> requests
    ) {
        if (requests == null || requests.isEmpty()) {
            return ResponseDto.error(ErrorCode.ERROR);
        }
        List<ShopOrderResponse> responseList = new ArrayList<>();
        for (CreateShopOrderRequestDto request : requests) {
            if (request.getDeliveryType() == null) request.setDeliveryType(DeliveryType.PICKUP);
            BotCustomer customer = botCustomerRepository
                    .checkUser(request.getChatId())
                    .orElse(null);
            if (customer == null) {
                return ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER);
            }
            Shop shop = shopRepository
                    .findById(request.getShopId())
                    .orElse(null);
            if (shop == null) {
                return ResponseDto.error(ErrorCode.NOT_FOUND_SHOP);
            }

            Seller seller = shop.getSeller();
            if (seller == null) {
                return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
            }

            ShopOrder shopOrder = new ShopOrder();
            shopOrder.setShop(shop);
            shopOrder.setSeller(seller);
            shopOrder.setCustomer(customer);
            shopOrder.setDeliveryType(request.getDeliveryType());
            shopOrder.setStatus(ShopOrderStatus.NEW);
            shopOrder.setCreatedAt(LocalDateTime.now());
            shopOrder.setUpdatedAt(LocalDateTime.now());
            ShopOrder savedOrder = shopOrderRepository.save(shopOrder);

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CreateOrderItemRequestDto itemDto : request.getItems()) {

                ProductType productType = productTypeRepository
                        .findById(itemDto.getProductTypeId())
                        .orElse(null);
                if (productType == null) {
                    return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
                }

                OrderItem item = new OrderItem();
                item.setShopOrder(savedOrder);
                item.setProductType(productType);
                item.setQuantity(itemDto.getQuantity());
                item.setPrice(itemDto.getPrice());
                item.setTotalPrice(itemDto.getTotalPrice());
                totalAmount = totalAmount.add(itemDto.getTotalPrice());
                productType.setStock(productType.getStock() - item.getQuantity());
                productTypeRepository.save(productType);
                orderItemRepository.save(item);
            }
            savedOrder.setTotalAmount(totalAmount);
            savedOrder.setPhone(request.getPhone());
            savedOrder.setLatitude(request.getLatitude());
            savedOrder.setLongitude(request.getLongitude());
            String address = locationService.getAddress(savedOrder.getLatitude(), savedOrder.getLongitude());
            savedOrder.setAddress(address.equals("error") ? null : address);
            savedOrder.setDeliveryPrice(
                    savedOrder.getDeliveryType() == DeliveryType.PICKUP ? BigDecimal.ZERO :
                            savedOrder.getDeliveryType() == DeliveryType.DELIVERY_INSIDE ? savedOrder.getShop().getDeliveryPrice() :
                                    savedOrder.getShop().getDeliveryOutsidePrice()
            );
            responseList.add(mapToResponse(shopOrderRepository.save(savedOrder)));
        }
        return ResponseDto.success(responseList);
    }


    private ShopOrderResponse mapToResponse(ShopOrder order) {
        ShopOrderResponse response = new ShopOrderResponse();
        response.setId(order.getPkey());
        Shop shop = order.getShop();
        response.setShop(new ShopResponse(
                shop.getPkey(),
                shop.getBrandUrl(),
                shop.getNameUz(),
                shop.getNameCyr(),
                shop.getNameEn(),
                shop.getNameRu(),
                shop.getDescriptionUz(),
                shop.getDescriptionCyr(),
                shop.getDescriptionRu(),
                shop.getDescriptionEn(),
                shop.getLogoUrl(),
                shop.getWorkStartTime(),
                shop.getWorkEndTime(),
                shop.getWorkDays(),
                shop.getPhone(),
                shop.getEmail(),
                shop.getTelegram(),
                shop.getInstagram(),
                shop.getFacebook(),
                shop.getWebsite(),
                shop.getHasPhone(),
                shop.getHasEmail(),
                shop.getHasTelegram(),
                shop.getHasInstagram(),
                shop.getHasFacebook(),
                shop.getHasWebsite(),
                shop.getCreatedAt().toString(),
                shop.getUpdatedAt().toString(),
                shop.getLatitude(),
                shop.getLongitude(),
                shop.getAddress(),
                shop.getDeliveryInfoUz(),
                shop.getDeliveryInfoRu(),
                shop.getDeliveryInfoEn(),
                shop.getDeliveryInfoCyr(),
                shop.getDeliveryPrice(),
                shop.getHasDelivery(),
                shop.getDeliveryOutsidePrice(),
                shop.getHasOutsideDelivery(),
                shop.getSeller().getPkey()
        ));
        response.setSeller(order.getSeller());
        response.setStatus(order.getStatus());
        response.setPhone(order.getPhone());
        response.setAddress(order.getAddress());
//        response.setAddress(locationService.getAddress(order.getLatitude(), order.getLongitude()));
        response.setDeliveryType(order.getDeliveryType());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        response.setItems(
                orderItemRepository.findByShopOrder(order)
                        .stream()
                        .map(this::toOrderItemResponse)
                        .toList()
        );
        response.setClient(order.getCustomer());
        if (order.getDeliveryType() != DeliveryType.PICKUP) {
            response.setDeliveryPrice(
                    order.getDeliveryType() == DeliveryType.DELIVERY_INSIDE ? shop.getDeliveryPrice()
                            : shop.getDeliveryOutsidePrice()
            );
        } else response.setDeliveryPrice(BigDecimal.ZERO);
        return response;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        OrderItemResponse res = new OrderItemResponse(
                item.getPkey(),
                item.getShopOrder().getPkey(),
                productTypeMapper.toResponse(item.getProductType()),
                item.getQuantity(),
                item.getPrice(),
                item.getTotalPrice()
        );
        res.setProduct(productMapper.toResponse(item.getProductType().getProduct()));
        return res;
    }

    @Override
    public ResponseDto<List<ShopOrderResponse>> allOrders() {
        List<ShopOrder> orders =
                shopOrderRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "createdAt")
                );
        if (orders.isEmpty()) {
            return ResponseDto.success(List.of());
        }
        List<ShopOrderResponse> responseList = orders.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseDto.success(responseList);
    }
}
