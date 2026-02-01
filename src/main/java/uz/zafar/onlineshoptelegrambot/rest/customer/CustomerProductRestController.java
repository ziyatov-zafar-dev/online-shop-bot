package uz.zafar.onlineshoptelegrambot.rest.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.config.TelegramProperties;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.common.Discount;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.DiscountType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.Basket;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.BasketType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.DeliveryType;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BasketRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.CategoryRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.customer.CustomerCategoryResponse;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.gson.AddressDto;
import uz.zafar.onlineshoptelegrambot.dto.gson.LocationStatus;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateOrderItemRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateShopOrderRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.product.response.CategoryResponse;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductResponse;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductTypeResponse;
import uz.zafar.onlineshoptelegrambot.mapper.ProductMapper;
import uz.zafar.onlineshoptelegrambot.mapper.ProductTypeMapper;
import uz.zafar.onlineshoptelegrambot.service.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/product")
public class CustomerProductRestController {
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final ProductMapper productMapper;
    private final TelegramProperties telegramProperties;
    private final ProductService productService;
    private final BotCustomerRepository botCustomerRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeMapper productTypeMapper;
    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;

    public CustomerProductRestController(CategoryRepository categoryRepository, ShopRepository shopRepository, ProductMapper productMapper, TelegramProperties telegramProperties, ProductService productService, BotCustomerRepository botCustomerRepository, ProductTypeRepository productTypeRepository, ProductTypeMapper productTypeMapper, BasketRepository basketRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.shopRepository = shopRepository;
        this.productMapper = productMapper;
        this.telegramProperties = telegramProperties;
        this.productService = productService;
        this.botCustomerRepository = botCustomerRepository;
        this.productTypeRepository = productTypeRepository;
        this.productTypeMapper = productTypeMapper;
        this.basketRepository = basketRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("category/parent-categories")
    public List<CustomerCategoryResponse> parent() {
        List<Category> list = categoryRepository.getParentCategories();
        List<CustomerCategoryResponse> categories = new ArrayList<>();
        for (Category category : list) {
            categories.add(toResponseCategory(category));
        }
        return categories;
    }

    @GetMapping("category/list")
    public List<CustomerCategoryResponse> list() {
        List<Category> list = categoryRepository.findAllLeafCategories();
        List<CustomerCategoryResponse> categories = new ArrayList<>();
        for (Category category : list) {
            if (productRepository.findAllByCategoryId(category.getPkey()).isEmpty()) continue;
            categories.add(toResponseCategory(category));
        }
        return categories;
    }

    @GetMapping("category/{categoryId}/children")
    public List<CustomerCategoryResponse> children(@PathVariable("categoryId") UUID cid) {
        if (categoryRepository.findById(cid).isEmpty()) {
            return List.of();
        }
        List<Category> list = categoryRepository.findActiveChildrenByParentId(cid);
        List<CustomerCategoryResponse> categories = new ArrayList<>();
        for (Category category : list) {
            categories.add(toResponseCategory(category));
        }
        return categories;
    }


    @GetMapping("products-by-category-id/{categoryId}")
    public ResponseDto<?> products(@PathVariable("categoryId") UUID categoryId) {
        return productService.findAllByCategoryId(categoryId);
    }

    @GetMapping("product-by-id/{productId}")
    public ResponseDto<?> productId(@PathVariable("productId") UUID productId) {
        return productService.findByProductId(productId);
    }

    @GetMapping("user-by-chat-id/{chatId}")
    public ResponseDto<?> getUser(@PathVariable("chatId") Long chatId) {
        BotCustomer botCustomer = botCustomerRepository.checkUser(chatId).orElse(null);
        if (botCustomer == null) return ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER);
        return ResponseDto.success(botCustomer);
    }

    @GetMapping("search")
    public ResponseDto<?> search(@RequestParam("q") String query) {
        return productService.search(query);
    }

    private CustomerCategoryResponse toResponseCategory(Category category) {
        return new CustomerCategoryResponse(
                category.getPkey(),
                category.getNameUz(),
                category.getNameCyr(),
                category.getNameEn(),
                category.getNameRu(),
                category.getDescriptionUz(),
                category.getDescriptionCyr(),
                category.getDescriptionRu(),
                category.getDescriptionEn(),
                category.getOrderNumber(),
                category.getImageUrl(),
                category.getParent() == null ? null : category.getParent().getPkey(),
                category.getCreatedAt().toString(),
                category.getUpdatedAt().toString(),
                !category.getChildren().isEmpty()
        );
    }

    private BasketResponse toBasketResponse(Basket b) {
        BasketResponse basketResponse = new BasketResponse();
        basketResponse.setId(b.getPkey());
        basketResponse.setProduct(productMapper.toResponse(b.getProductType().getProduct()));
        basketResponse.setProductType(productTypeMapper.toResponse(b.getProductType()));
        basketResponse.setPrice(b.getPrice());
        basketResponse.setTotalPrice(b.getTotalPrice());
        basketResponse.setQuantity(b.getQuantity());
        return basketResponse;
    }

    @GetMapping("my-baskets/{chatId}")
    public ResponseDto<?> myBaskets(@PathVariable("chatId") Long chatId) {
        BotCustomer botCustomer = botCustomerRepository.checkUser(chatId).orElse(null);
        if (botCustomer == null) return ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER);
        List<Basket> baskets = basketRepository.myBaskets(botCustomer.getPkey());
        List<BasketResponse> list = new ArrayList<>();
        for (Basket basket : baskets) {
            list.add(toBasketResponse(basket));
        }
        return ResponseDto.success(list);
    }

    @GetMapping("redirect-basket")
    public ResponseDto<?> redirectBasket() {
        return ResponseDto.success(telegramProperties.getUsers().getBot().getUsername() + "?start=basket");
    }

    @PostMapping("decrement/{chatId}")
    public ResponseDto<?> decrement(@PathVariable("chatId") Long chatId, @RequestParam("productTypeId") UUID productTypeId) {
        BotCustomer user = botCustomerRepository.checkUser(chatId).orElse(null);
        if (user == null)
            return ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER);
        ProductType productType = productTypeRepository.findById(productTypeId).orElse(null);
        if (productType == null)
            return ResponseDto.error(ErrorCode.ERROR);
        Basket basket = basketRepository
                .findByCustomerIdAndTypeId(user.getPkey(), productTypeId)
                .orElse(null);
        if (basket == null)
            return ResponseDto.error(ErrorCode.ERROR);
        if (basket.getQuantity() <= 1) {
            basket.setType(BasketType.FINISHED);
        } else basket.setQuantity(basket.getQuantity() - 1);
        basketRepository.save(basket);
        return ResponseDto.success(basket);
    }

    @GetMapping("shops")
    public ResponseEntity<?> shops() {
        return ResponseEntity.ok(shopRepository.activeShops());
    }

    @PostMapping("increment/{chatId}")
    public ResponseDto<?> increment(
            @PathVariable("chatId") Long chatId,
            @RequestParam("productTypeId") UUID productTypeId
    ) {
        BotCustomer user = botCustomerRepository.checkUser(chatId).orElse(null);
        if (user == null)
            return ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER);
        ProductType productType = productTypeRepository.findById(productTypeId).orElse(null);
        if (productType == null)
            return ResponseDto.error(ErrorCode.ERROR);
        Basket basket = basketRepository
                .findByCustomerIdAndTypeId(user.getPkey(), productTypeId)
                .orElse(null);
        if (basket == null)
            return ResponseDto.error(ErrorCode.ERROR);
        if (basket.getQuantity() >= productType.getStock()) {
            return ResponseDto.error(ErrorCode.OUT_OF_STOCK);
        }
        basket.setQuantity(basket.getQuantity() + 1);
        basketRepository.save(basket);
        return ResponseDto.success(basket);
    }


    @PostMapping("add-basket/{chatId}")
    public ResponseDto<?> addBasket(@PathVariable("chatId") Long chatId, @RequestParam("quantity") int quantity, @RequestParam("productTypeId") UUID productTypeId) {
        ProductType productType = productTypeRepository.findById(productTypeId).orElse(null);
        BotCustomer user = botCustomerRepository.checkUser(chatId).orElse(null);
        if (user == null) return ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER);
        if (productType == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        if (quantity > productType.getStock()) {
            return ResponseDto.error(ErrorCode.ERROR);
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
        return ResponseDto.success();
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

    /*private List<CreateShopOrderRequestDto> refreshCreateOrder(BotCustomer user) {
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
    }*/
    /*private List<CreateShopOrderRequestDto> createOrderRequest(BotCustomer user) {
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
    }*/
    public static class BasketResponse {
        private UUID id;
        private ProductTypeResponse productType;
        private ProductResponse product;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal totalPrice;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public ProductTypeResponse getProductType() {
            return productType;
        }

        public void setProductType(ProductTypeResponse productType) {
            this.productType = productType;
        }

        public ProductResponse getProduct() {
            return product;
        }

        public void setProduct(ProductResponse product) {
            this.product = product;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }
    }
}
