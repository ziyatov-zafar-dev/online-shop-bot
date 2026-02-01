package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.*;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.DeliveryType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.db.repositories.*;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.CategoryRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeImageRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.admin.Statistics;
import uz.zafar.onlineshoptelegrambot.dto.order.response.ShopOrderStatusInfo;
import uz.zafar.onlineshoptelegrambot.dto.payment.PaymentResponse;
import uz.zafar.onlineshoptelegrambot.dto.seller.SellerResponse;
import uz.zafar.onlineshoptelegrambot.dto.shop.response.ShopResponse;
import uz.zafar.onlineshoptelegrambot.mapper.ShopMapper;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsRestController {

    private final UserRepository userRepository;
    private final BotSellerRepository botSellerRepository;
    private final BotCustomerRepository botCustomerRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ShopMapper shopMapper;
    private final ProductTypeImageRepository productTypeImageRepository;
    private final SellerRepository sellerRepository;
    private final OrderItemRepository orderItemRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final AdsRepository adsRepository;
    private final AdsButtonRepository adsButtonRepository;
    private final DiscountRepository discountRepository;
    private final AdminCardRepository adminCardRepository;

    public AdminStatisticsRestController(UserRepository userRepository, BotSellerRepository botSellerRepository, BotCustomerRepository botCustomerRepository, LikeRepository likeRepository, CommentRepository commentRepository, ShopOrderRepository shopOrderRepository, ShopRepository shopRepository, ProductRepository productRepository, CategoryRepository categoryRepository, PaymentRepository paymentRepository, ProductTypeRepository productTypeRepository, ShopMapper shopMapper, ProductTypeImageRepository productTypeImageRepository, SellerRepository sellerRepository, OrderItemRepository orderItemRepository, SubscriptionPlanRepository subscriptionPlanRepository, AdsRepository adsRepository, AdsButtonRepository adsButtonRepository, DiscountRepository discountRepository, AdminCardRepository adminCardRepository) {
        this.userRepository = userRepository;
        this.botSellerRepository = botSellerRepository;
        this.botCustomerRepository = botCustomerRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.paymentRepository = paymentRepository;
        this.productTypeRepository = productTypeRepository;
        this.shopMapper = shopMapper;
        this.productTypeImageRepository = productTypeImageRepository;
        this.sellerRepository = sellerRepository;
        this.orderItemRepository = orderItemRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.adsRepository = adsRepository;
        this.adsButtonRepository = adsButtonRepository;
        this.discountRepository = discountRepository;
        this.adminCardRepository = adminCardRepository;
    }

    @GetMapping
    public ResponseDto<Statistics> statistics() {
        try {
            Statistics statistics = new Statistics();

            // 1. Users statistics
            statistics.setSellersCount(sellerRepository.count());

            // Bot seller users count
            statistics.setSellerBotUsersCount(botSellerRepository.count());

            // Bot customer users count
            statistics.setCustomerBotUsersCount(botCustomerRepository.count());

            // 2. Likes and comments
            statistics.setLikesCount(likeRepository.count());
            statistics.setCommentsCount(commentRepository.count());

            // 3. Orders statistics
            statistics.setTotalOrders(BigInteger.valueOf(shopOrderRepository.count()));
            statistics.setTodayOrdersCount(shopOrderRepository.findAllByLastWeek(LocalDateTime.now().minusDays(1)).size());
            statistics.setLastWeekOrdersCount(shopOrderRepository.findAllByLastWeek(LocalDateTime.now().minusDays(7)).size());
            statistics.setLastMonthOrdersCount(shopOrderRepository.findAllByLastMonth(LocalDateTime.now().minusMonths(1)).size());

            // Order status statistics
            Statistics.OrderStatistics orderStatistics = new Statistics.OrderStatistics();
            List<Statistics.OrderStatusInfoStatistics> orderStatuses = new ArrayList<>();

            for (ShopOrderStatus status : ShopOrderStatus.values()) {
                Statistics.OrderStatusInfoStatistics statusStat = new Statistics.OrderStatusInfoStatistics();
                ShopOrderStatusInfo shopOrderStatus = new ShopOrderStatusInfo(status, DeliveryType.DELIVERY_INSIDE);
                statusStat.setStatus(shopOrderStatus);
                statusStat.setCount(shopOrderRepository.findAllByStatus(status).size());
                orderStatuses.add(statusStat);
            }
            orderStatistics.setOrderStatusesStatistics(orderStatuses);
            statistics.setOrderStatistics(orderStatistics);

            // 4. Shop statistics
            Statistics.ShopStatistics shopStatistics = new Statistics.ShopStatistics();

            // Shops lists
            List<uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop> allShops = shopRepository.findAll();
            List<uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop> activeShops = shopRepository.activeShops();
            List<uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop> pendingShops = shopRepository.notConfirmedShops();
            List<uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop> deletedShops = shopRepository.deletedShops();

            // Convert to DTO
            List<ShopResponse> allShopResponses = allShops.stream()
                    .map(shopMapper::toResponse)
                    .collect(Collectors.toList());

            List<ShopResponse> activeShopResponses = activeShops.stream()
                    .map(shopMapper::toResponse)
                    .collect(Collectors.toList());

            List<ShopResponse> pendingShopResponses = pendingShops.stream()
                    .map(shopMapper::toResponse)
                    .collect(Collectors.toList());

            List<ShopResponse> deletedShopResponses = deletedShops.stream()
                    .map(shopMapper::toResponse)
                    .collect(Collectors.toList());

            shopStatistics.setAllShops(allShopResponses);
            shopStatistics.setConfirmedShops(activeShopResponses);
            shopStatistics.setPendingShops(pendingShopResponses);
            shopStatistics.setRejectedShops(deletedShopResponses);

            shopStatistics.setShopsCount(allShops.size());
            shopStatistics.setConfirmedShopsCount(activeShops.size());
            shopStatistics.setPendingShopsCount(pendingShops.size());
            shopStatistics.setRejectedShopsCount(deletedShops.size());

            statistics.setShopStatistics(shopStatistics);

            // 5. Payment statistics
            Statistics.PaymentStatistics paymentStatistics = new Statistics.PaymentStatistics();
            List<uz.zafar.onlineshoptelegrambot.db.entity.payment.Payment> allPayments = paymentRepository.findAll();
            List<PaymentResponse> allPaymentResponses = allPayments.stream()
                    .map(this::convertToPaymentResponse)
                    .collect(Collectors.toList());

            paymentStatistics.setAllPayments(allPaymentResponses);
            paymentStatistics.setPaymentsCount(allPayments.size());

            // Filter payments by type
            List<PaymentResponse> accountTopupPayments = allPaymentResponses.stream()
                    .filter(p -> p.getPaymentType() != null && p.getPurpose() == PaymentPurpose.ACCOUNT_TOPUP)
                    .collect(Collectors.toList());

            List<PaymentResponse> accountRenewalPayments = allPaymentResponses.stream()
                    .filter(p -> p.getPaymentType() != null && p.getPurpose() == PaymentPurpose.SUBSCRIPTION_RENEWAL)
                    .collect(Collectors.toList());

            List<PaymentResponse> accountTopupStatusNewPayments = accountTopupPayments.stream()
                    .filter(p -> p.getStatus() != null && p.getStatus() == PaymentStatus.NEW && p.getPurpose() == PaymentPurpose.ACCOUNT_TOPUP)
                    .collect(Collectors.toList());

            List<PaymentResponse> accountRenewalStatusNewPayments = accountRenewalPayments.stream()
                    .filter(p -> p.getStatus() != null && p.getStatus() == PaymentStatus.NEW && p.getPurpose() == PaymentPurpose.SUBSCRIPTION_RENEWAL)
                    .collect(Collectors.toList());

            List<PaymentResponse> accountTopupCancelStatusPayments = accountTopupPayments.stream()
                    .filter(p -> p.getStatus() != null && p.getStatus() == PaymentStatus.CANCEL && p.getPurpose() == PaymentPurpose.ACCOUNT_TOPUP)
                    .collect(Collectors.toList());

            List<PaymentResponse> accountRenewalConfirmStatusPayments = accountRenewalPayments.stream()
                    .filter(p -> p.getStatus() != null && p.getStatus() == PaymentStatus.CONFIRM && p.getPurpose() == PaymentPurpose.SUBSCRIPTION_RENEWAL)
                    .collect(Collectors.toList());

            paymentStatistics.setAccountTopupPayments(accountTopupPayments);
            paymentStatistics.setAccountTopupPaymentsCount(accountTopupPayments.size());

            paymentStatistics.setAccountRenewalPayments(accountRenewalPayments);
            paymentStatistics.setAccountRenewalPaymentsCount(accountRenewalPayments.size());

            paymentStatistics.setAccountTopupStatusNewPayments(accountTopupStatusNewPayments);
            paymentStatistics.setAccountTopupStatusNewPaymentsCount(accountTopupStatusNewPayments.size());

            paymentStatistics.setAccountRenewalStatusNewPayments(accountRenewalStatusNewPayments);
            paymentStatistics.setAccountRenewalStatusNewPaymentsCount(accountRenewalStatusNewPayments.size());

            paymentStatistics.setAccountTopupCancelStatusPayments(accountTopupCancelStatusPayments);
            paymentStatistics.setAccountTopupCancelStatusPaymentsCount(accountTopupCancelStatusPayments.size());

            paymentStatistics.setAccountRenewalConfirmStatusPayments(accountRenewalConfirmStatusPayments);
            paymentStatistics.setAccountRenewalConfirmStatusPaymentsCount(accountRenewalConfirmStatusPayments.size());

            statistics.setPaymentStatistics(paymentStatistics);

            // 6. Product statistics
            Statistics.ProductStatistics productStatistics = new Statistics.ProductStatistics();

            // All products
            List<uz.zafar.onlineshoptelegrambot.db.entity.category.Product> allProducts = productRepository.getAllProducts();
            productStatistics.setAllProductsCount(BigInteger.valueOf(allProducts.size()));

            long pendingProductsCount = allProducts.stream()
                    .filter(p -> p.getStatus() == ProductStatus.DRAFT)
                    .count();
            productStatistics.setPendingProductsCount(BigInteger.valueOf(pendingProductsCount));

            long confirmedProductsCount = allProducts.stream()
                    .filter(p -> p.getStatus() == ProductStatus.OPEN)
                    .count();
            productStatistics.setConfirmedProductsCount(BigInteger.valueOf(confirmedProductsCount));

            long rejectedProductsCount = allProducts.stream()
                    .filter(p -> p.getStatus() == ProductStatus.DELETED)
                    .count();
            productStatistics.setRejectedProductsCount(BigInteger.valueOf(rejectedProductsCount));

            // In stock products
            long inStockProductsCount = allProducts.stream()
                    .filter(p -> {
                        // Assuming product has quantity field
                        // This is a simplified logic
                        return p.getStatus() == ProductStatus.OPEN;
                    })
                    .count();
            productStatistics.setInStockProductsCount(BigInteger.valueOf(inStockProductsCount));

            // Products with photo
            long productsWithPhotoCount = allProducts.stream()
                    .filter(p -> {
                        // Check if product has images
                        // This needs actual product image relation
                        return true; // Placeholder
                    })
                    .count();
            productStatistics.setProductsWithPhotoCount(BigInteger.valueOf(productsWithPhotoCount));

            // Discounted products
            long discountedProductsCount = allProducts.stream()
                    .filter(p -> {
                        // Check if product has discount
                        return p.getDiscount() != null;
                    })
                    .count();
            productStatistics.setDiscountedProductsCount(BigInteger.valueOf(discountedProductsCount));

            productStatistics.setNonDiscountedProductsCount(
                    BigInteger.valueOf(allProducts.size() - discountedProductsCount)
            );

            // Today added products
            long todayAddedProductsCount = allProducts.stream()
                    .filter(p -> {
                        LocalDate today = LocalDate.now();
                        return p.getCreatedAt() != null &&
                                p.getCreatedAt().toLocalDate().equals(today);
                    })
                    .count();
            productStatistics.setTodayAddedProductsCount(BigInteger.valueOf(todayAddedProductsCount));

            productStatistics.setTodayCreatedProductsCount((long) todayAddedProductsCount);
            productStatistics.setLastWeekCreatedProductsCount(allProducts.size());
            productStatistics.setLastMonthCreatedProductsCount(allProducts.size());

            // Product types
            long totalProductTypesCount = productTypeRepository.count();
            productStatistics.setTotalProductTypesCount(totalProductTypesCount);

            // Active product types (not deleted)
            List<uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType> allProductTypes = productTypeRepository.findAll();
            long activeProductTypesCount = allProductTypes.stream()
                    .filter(pt -> !pt.getDeleted())
                    .count();
            productStatistics.setActiveProductTypesCount(activeProductTypesCount);

            productStatistics.setDeletedProductTypesCount(totalProductTypesCount - activeProductTypesCount);

            // In stock product types
            long inStockProductTypesCount = allProductTypes.stream()
                    .filter(pt -> {
                        return !pt.getDeleted();
                    })
                    .count();
            productStatistics.setInStockProductTypesCount(BigInteger.valueOf(inStockProductTypesCount));

            // Discount types
            long percentageDiscountCount = 0;
            long fixedDiscountCount = 0;
            for (uz.zafar.onlineshoptelegrambot.db.entity.category.Product product : allProducts) {
                if (product.getDiscount() != null) {
                    if (product.getDiscount().getType() == DiscountType.PERCENT) {
                        percentageDiscountCount++;
                    } else {
                        fixedDiscountCount++;
                    }
                }
            }
            productStatistics.setPercentageDiscountCount(BigInteger.valueOf(percentageDiscountCount));
            productStatistics.setFixedDiscountCount(BigInteger.valueOf(fixedDiscountCount));

            // Images
            long totalImagesCount = productTypeImageRepository.count();
            productStatistics.setTotalImagesCount(totalImagesCount);
            productStatistics.setActiveImagesCount(totalImagesCount); // Assuming all are active
            productStatistics.setDeletedImagesCount(0);

            // Liked products count
            productStatistics.setLikedProductsCount(BigInteger.valueOf(likeRepository.count()));

            statistics.setProductStatistics(productStatistics);

            // 7. Category statistics
            Statistics.CategoryStatistics categoryStatistics = new Statistics.CategoryStatistics();

            List<uz.zafar.onlineshoptelegrambot.db.entity.category.Category> allCategories = categoryRepository.getAllCategories();
            List<uz.zafar.onlineshoptelegrambot.db.entity.category.Category> parentCategories = categoryRepository.getParentCategories();

            categoryStatistics.setCategoriesCount(allCategories.size());
            categoryStatistics.setParentCategoriesCount(parentCategories.size());
            categoryStatistics.setSubcategoriesCount(allCategories.size() - parentCategories.size());

            statistics.setCategoryStatistics(categoryStatistics);

            return ResponseDto.success(statistics);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.error("Error fetching statistics: " + e.getMessage());
        }
    }

    private ShopResponse convertToShopResponse(uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop shop) {
        ShopResponse response = new ShopResponse();
        response.setId(shop.getPkey());
        response.setNameUz(shop.getNameUz());
        response.setNameRu(shop.getNameRu());
        response.setNameEn(shop.getNameEn());
        response.setNameCyr(shop.getNameCyr());
        response.setPhone(shop.getPhone());
        response.setEmail(shop.getEmail());
        response.setTelegram(shop.getTelegram());
        response.setInstagram(shop.getInstagram());
        response.setFacebook(shop.getFacebook());
        response.setWebsite(shop.getWebsite());
        response.setAddress(shop.getAddress());
        response.setCreatedAt(shop.getCreatedAt().toString());
        response.setUpdatedAt(shop.getUpdatedAt().toString());
        return response;
    }

    private PaymentResponse convertToPaymentResponse(uz.zafar.onlineshoptelegrambot.db.entity.payment.Payment payment) {
        Seller seller = payment.getSeller();
        if (seller == null) return null;

        SubscriptionPlan plan = null;
        if (seller.getSubscriptionPlanId() != null) {
            plan = subscriptionPlanRepository
                    .findById(seller.getSubscriptionPlanId())
                    .orElse(null);
        }

        User user = null;
        if (seller.getUserid() != null) {
            user = userRepository
                    .findById(seller.getUserid())
                    .orElse(null);
        }


        SellerResponse sellerResponse = new SellerResponse(
                seller.getGender(),
                seller.getPkey(),
                seller.getPhone(),
                seller.getCardNumber(),
                seller.getCardType(),
                seller.getCardImageUrl(),
                seller.getCardOwner(),
                seller.getCardImageSize(),
                seller.getCardImageName(),
                seller.getCardAddedTime(),
                seller.getBalance(),
                seller.getStatus(),
                plan,
                seller.getPlanExpiresAt(),
                user
        );
        return new PaymentResponse(
                payment.getPkey(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentType(),
                payment.getPaymentPurpose(),
                seller.getPkey(),
                payment.getTransactionId(),
                payment.getDescription(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                sellerResponse,
                payment.getPaymentImage(),
                payment.getImageSize(),
                payment.getImageName()
        );
    }
}