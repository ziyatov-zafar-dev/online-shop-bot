package uz.zafar.onlineshoptelegrambot.dto.admin;

import uz.zafar.onlineshoptelegrambot.dto.order.response.ShopOrderStatusInfo;
import uz.zafar.onlineshoptelegrambot.dto.payment.PaymentResponse;
import uz.zafar.onlineshoptelegrambot.dto.shop.response.ShopResponse;

import java.math.BigInteger;
import java.util.List;

public class Statistics {
    private long sellersCount;
    private long sellerBotUsersCount;
    private long customerBotUsersCount;
    private long likesCount;
    private long commentsCount;
    private OrderStatistics orderStatistics;
    private BigInteger totalOrders;
    private long todayOrdersCount;
    private long lastMonthOrdersCount;
    private long lastWeekOrdersCount;
    private ShopStatistics shopStatistics;
    private PaymentStatistics paymentStatistics;
    private ProductStatistics productStatistics;
    private CategoryStatistics categoryStatistics;

    public CategoryStatistics getCategoryStatistics() {
        return categoryStatistics;
    }

    public void setCategoryStatistics(CategoryStatistics categoryStatistics) {
        this.categoryStatistics = categoryStatistics;
    }

    public static class CategoryStatistics {
        private long parentCategoriesCount;
        private long subcategoriesCount;
        private long categoriesCount;

        public long getCategoriesCount() {
            return categoriesCount;
        }

        public void setCategoriesCount(long categoriesCount) {
            this.categoriesCount = categoriesCount;
        }

        public long getParentCategoriesCount() {
            return parentCategoriesCount;
        }

        public void setParentCategoriesCount(long parentCategoriesCount) {
            this.parentCategoriesCount = parentCategoriesCount;
        }

        public long getSubcategoriesCount() {
            return subcategoriesCount;
        }

        public void setSubcategoriesCount(long subcategoriesCount) {
            this.subcategoriesCount = subcategoriesCount;
        }
    }

    public long getSellersCount() {
        return sellersCount;
    }

    public void setSellersCount(long sellersCount) {
        this.sellersCount = sellersCount;
    }

    public long getSellerBotUsersCount() {
        return sellerBotUsersCount;
    }

    public void setSellerBotUsersCount(long sellerBotUsersCount) {
        this.sellerBotUsersCount = sellerBotUsersCount;
    }

    public long getCustomerBotUsersCount() {
        return customerBotUsersCount;
    }

    public void setCustomerBotUsersCount(long customerBotUsersCount) {
        this.customerBotUsersCount = customerBotUsersCount;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public OrderStatistics getOrderStatistics() {
        return orderStatistics;
    }

    public void setOrderStatistics(OrderStatistics orderStatistics) {
        this.orderStatistics = orderStatistics;
    }

    public BigInteger getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(BigInteger totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTodayOrdersCount() {
        return todayOrdersCount;
    }

    public void setTodayOrdersCount(long todayOrdersCount) {
        this.todayOrdersCount = todayOrdersCount;
    }

    public long getLastMonthOrdersCount() {
        return lastMonthOrdersCount;
    }

    public void setLastMonthOrdersCount(long lastMonthOrdersCount) {
        this.lastMonthOrdersCount = lastMonthOrdersCount;
    }

    public long getLastWeekOrdersCount() {
        return lastWeekOrdersCount;
    }

    public void setLastWeekOrdersCount(long lastWeekOrdersCount) {
        this.lastWeekOrdersCount = lastWeekOrdersCount;
    }

    public ShopStatistics getShopStatistics() {
        return shopStatistics;
    }

    public void setShopStatistics(ShopStatistics shopStatistics) {
        this.shopStatistics = shopStatistics;
    }

    public PaymentStatistics getPaymentStatistics() {
        return paymentStatistics;
    }

    public void setPaymentStatistics(PaymentStatistics paymentStatistics) {
        this.paymentStatistics = paymentStatistics;
    }

    public ProductStatistics getProductStatistics() {
        return productStatistics;
    }

    public void setProductStatistics(ProductStatistics productStatistics) {
        this.productStatistics = productStatistics;
    }

    public static class ProductStatistics {
        private BigInteger allProductsCount;
        private BigInteger pendingProductsCount;
        private BigInteger confirmedProductsCount;
        private BigInteger rejectedProductsCount;
        private BigInteger inStockProductsCount;
        private BigInteger productsWithPhotoCount;
        private BigInteger discountedProductsCount;
        private BigInteger nonDiscountedProductsCount;
        private BigInteger likedProductsCount;
        private BigInteger todayAddedProductsCount;
        private long todayCreatedProductsCount;
        private long lastWeekCreatedProductsCount;
        private long lastMonthCreatedProductsCount;
        private long totalProductTypesCount;
        private long activeProductTypesCount;
        private long deletedProductTypesCount;

        private BigInteger inStockProductTypesCount;
        private BigInteger percentageDiscountCount;
        private BigInteger fixedDiscountCount;
        private long totalImagesCount;
        private long activeImagesCount;
        private long deletedImagesCount;

        public BigInteger getAllProductsCount() {
            return allProductsCount;
        }

        public void setAllProductsCount(BigInteger allProductsCount) {
            this.allProductsCount = allProductsCount;
        }

        public BigInteger getPendingProductsCount() {
            return pendingProductsCount;
        }

        public void setPendingProductsCount(BigInteger pendingProductsCount) {
            this.pendingProductsCount = pendingProductsCount;
        }

        public BigInteger getConfirmedProductsCount() {
            return confirmedProductsCount;
        }

        public void setConfirmedProductsCount(BigInteger confirmedProductsCount) {
            this.confirmedProductsCount = confirmedProductsCount;
        }

        public BigInteger getRejectedProductsCount() {
            return rejectedProductsCount;
        }

        public void setRejectedProductsCount(BigInteger rejectedProductsCount) {
            this.rejectedProductsCount = rejectedProductsCount;
        }

        public BigInteger getInStockProductsCount() {
            return inStockProductsCount;
        }

        public void setInStockProductsCount(BigInteger inStockProductsCount) {
            this.inStockProductsCount = inStockProductsCount;
        }

        public BigInteger getProductsWithPhotoCount() {
            return productsWithPhotoCount;
        }

        public void setProductsWithPhotoCount(BigInteger productsWithPhotoCount) {
            this.productsWithPhotoCount = productsWithPhotoCount;
        }

        public BigInteger getDiscountedProductsCount() {
            return discountedProductsCount;
        }

        public void setDiscountedProductsCount(BigInteger discountedProductsCount) {
            this.discountedProductsCount = discountedProductsCount;
        }

        public BigInteger getNonDiscountedProductsCount() {
            return nonDiscountedProductsCount;
        }

        public void setNonDiscountedProductsCount(BigInteger nonDiscountedProductsCount) {
            this.nonDiscountedProductsCount = nonDiscountedProductsCount;
        }

        public BigInteger getLikedProductsCount() {
            return likedProductsCount;
        }

        public void setLikedProductsCount(BigInteger likedProductsCount) {
            this.likedProductsCount = likedProductsCount;
        }

        public BigInteger getTodayAddedProductsCount() {
            return todayAddedProductsCount;
        }

        public void setTodayAddedProductsCount(BigInteger todayAddedProductsCount) {
            this.todayAddedProductsCount = todayAddedProductsCount;
        }

        public long getTodayCreatedProductsCount() {
            return todayCreatedProductsCount;
        }

        public void setTodayCreatedProductsCount(long todayCreatedProductsCount) {
            this.todayCreatedProductsCount = todayCreatedProductsCount;
        }

        public long getLastWeekCreatedProductsCount() {
            return lastWeekCreatedProductsCount;
        }

        public void setLastWeekCreatedProductsCount(long lastWeekCreatedProductsCount) {
            this.lastWeekCreatedProductsCount = lastWeekCreatedProductsCount;
        }

        public long getLastMonthCreatedProductsCount() {
            return lastMonthCreatedProductsCount;
        }

        public void setLastMonthCreatedProductsCount(long lastMonthCreatedProductsCount) {
            this.lastMonthCreatedProductsCount = lastMonthCreatedProductsCount;
        }

        public long getTotalProductTypesCount() {
            return totalProductTypesCount;
        }

        public void setTotalProductTypesCount(long totalProductTypesCount) {
            this.totalProductTypesCount = totalProductTypesCount;
        }

        public long getActiveProductTypesCount() {
            return activeProductTypesCount;
        }

        public void setActiveProductTypesCount(long activeProductTypesCount) {
            this.activeProductTypesCount = activeProductTypesCount;
        }

        public long getDeletedProductTypesCount() {
            return deletedProductTypesCount;
        }

        public void setDeletedProductTypesCount(long deletedProductTypesCount) {
            this.deletedProductTypesCount = deletedProductTypesCount;
        }

        public BigInteger getInStockProductTypesCount() {
            return inStockProductTypesCount;
        }

        public void setInStockProductTypesCount(BigInteger inStockProductTypesCount) {
            this.inStockProductTypesCount = inStockProductTypesCount;
        }

        public BigInteger getPercentageDiscountCount() {
            return percentageDiscountCount;
        }

        public void setPercentageDiscountCount(BigInteger percentageDiscountCount) {
            this.percentageDiscountCount = percentageDiscountCount;
        }

        public BigInteger getFixedDiscountCount() {
            return fixedDiscountCount;
        }

        public void setFixedDiscountCount(BigInteger fixedDiscountCount) {
            this.fixedDiscountCount = fixedDiscountCount;
        }

        public long getTotalImagesCount() {
            return totalImagesCount;
        }

        public void setTotalImagesCount(long totalImagesCount) {
            this.totalImagesCount = totalImagesCount;
        }

        public long getActiveImagesCount() {
            return activeImagesCount;
        }

        public void setActiveImagesCount(long activeImagesCount) {
            this.activeImagesCount = activeImagesCount;
        }

        public long getDeletedImagesCount() {
            return deletedImagesCount;
        }

        public void setDeletedImagesCount(long deletedImagesCount) {
            this.deletedImagesCount = deletedImagesCount;
        }
    }

    public static class PaymentStatistics {
        private List<PaymentResponse> allPayments;
        private long paymentsCount;

        private List<PaymentResponse> accountTopupPayments;
        private long accountTopupPaymentsCount;
        private List<PaymentResponse> accountRenewalPayments;
        private long accountRenewalPaymentsCount;

        private List<PaymentResponse> accountTopupStatusNewPayments;
        private long accountTopupStatusNewPaymentsCount;
        private List<PaymentResponse> accountRenewalStatusNewPayments;
        private long accountRenewalStatusNewPaymentsCount;

        private List<PaymentResponse> accountTopupCancelStatusPayments;
        private long accountTopupCancelStatusPaymentsCount;
        private List<PaymentResponse> accountRenewalConfirmStatusPayments;
        private long accountRenewalConfirmStatusPaymentsCount;

        public List<PaymentResponse> getAllPayments() {
            return allPayments;
        }

        public void setAllPayments(List<PaymentResponse> allPayments) {
            this.allPayments = allPayments;
        }

        public long getPaymentsCount() {
            return paymentsCount;
        }

        public void setPaymentsCount(long paymentsCount) {
            this.paymentsCount = paymentsCount;
        }

        public List<PaymentResponse> getAccountTopupPayments() {
            return accountTopupPayments;
        }

        public void setAccountTopupPayments(List<PaymentResponse> accountTopupPayments) {
            this.accountTopupPayments = accountTopupPayments;
        }

        public long getAccountTopupPaymentsCount() {
            return accountTopupPaymentsCount;
        }

        public void setAccountTopupPaymentsCount(long accountTopupPaymentsCount) {
            this.accountTopupPaymentsCount = accountTopupPaymentsCount;
        }

        public List<PaymentResponse> getAccountRenewalPayments() {
            return accountRenewalPayments;
        }

        public void setAccountRenewalPayments(List<PaymentResponse> accountRenewalPayments) {
            this.accountRenewalPayments = accountRenewalPayments;
        }

        public long getAccountRenewalPaymentsCount() {
            return accountRenewalPaymentsCount;
        }

        public void setAccountRenewalPaymentsCount(long accountRenewalPaymentsCount) {
            this.accountRenewalPaymentsCount = accountRenewalPaymentsCount;
        }

        public List<PaymentResponse> getAccountTopupStatusNewPayments() {
            return accountTopupStatusNewPayments;
        }

        public void setAccountTopupStatusNewPayments(List<PaymentResponse> accountTopupStatusNewPayments) {
            this.accountTopupStatusNewPayments = accountTopupStatusNewPayments;
        }

        public long getAccountTopupStatusNewPaymentsCount() {
            return accountTopupStatusNewPaymentsCount;
        }

        public void setAccountTopupStatusNewPaymentsCount(long accountTopupStatusNewPaymentsCount) {
            this.accountTopupStatusNewPaymentsCount = accountTopupStatusNewPaymentsCount;
        }

        public List<PaymentResponse> getAccountRenewalStatusNewPayments() {
            return accountRenewalStatusNewPayments;
        }

        public void setAccountRenewalStatusNewPayments(List<PaymentResponse> accountRenewalStatusNewPayments) {
            this.accountRenewalStatusNewPayments = accountRenewalStatusNewPayments;
        }

        public long getAccountRenewalStatusNewPaymentsCount() {
            return accountRenewalStatusNewPaymentsCount;
        }

        public void setAccountRenewalStatusNewPaymentsCount(long accountRenewalStatusNewPaymentsCount) {
            this.accountRenewalStatusNewPaymentsCount = accountRenewalStatusNewPaymentsCount;
        }

        public List<PaymentResponse> getAccountTopupCancelStatusPayments() {
            return accountTopupCancelStatusPayments;
        }

        public void setAccountTopupCancelStatusPayments(List<PaymentResponse> accountTopupCancelStatusPayments) {
            this.accountTopupCancelStatusPayments = accountTopupCancelStatusPayments;
        }

        public long getAccountTopupCancelStatusPaymentsCount() {
            return accountTopupCancelStatusPaymentsCount;
        }

        public void setAccountTopupCancelStatusPaymentsCount(long accountTopupCancelStatusPaymentsCount) {
            this.accountTopupCancelStatusPaymentsCount = accountTopupCancelStatusPaymentsCount;
        }

        public List<PaymentResponse> getAccountRenewalConfirmStatusPayments() {
            return accountRenewalConfirmStatusPayments;
        }

        public void setAccountRenewalConfirmStatusPayments(List<PaymentResponse> accountRenewalConfirmStatusPayments) {
            this.accountRenewalConfirmStatusPayments = accountRenewalConfirmStatusPayments;
        }

        public long getAccountRenewalConfirmStatusPaymentsCount() {
            return accountRenewalConfirmStatusPaymentsCount;
        }

        public void setAccountRenewalConfirmStatusPaymentsCount(long accountRenewalConfirmStatusPaymentsCount) {
            this.accountRenewalConfirmStatusPaymentsCount = accountRenewalConfirmStatusPaymentsCount;
        }
    }

    public static class ShopStatistics {
        private List<ShopResponse> allShops;
        private List<ShopResponse> confirmedShops;
        private List<ShopResponse> rejectedShops;
        private List<ShopResponse> pendingShops;
        private long shopsCount;
        private long confirmedShopsCount;
        private long rejectedShopsCount;
        private long pendingShopsCount;

        public List<ShopResponse> getAllShops() {
            return allShops;
        }

        public void setAllShops(List<ShopResponse> allShops) {
            this.allShops = allShops;
        }

        public List<ShopResponse> getConfirmedShops() {
            return confirmedShops;
        }

        public void setConfirmedShops(List<ShopResponse> confirmedShops) {
            this.confirmedShops = confirmedShops;
        }

        public List<ShopResponse> getRejectedShops() {
            return rejectedShops;
        }

        public void setRejectedShops(List<ShopResponse> rejectedShops) {
            this.rejectedShops = rejectedShops;
        }

        public List<ShopResponse> getPendingShops() {
            return pendingShops;
        }

        public void setPendingShops(List<ShopResponse> pendingShops) {
            this.pendingShops = pendingShops;
        }

        public long getShopsCount() {
            return shopsCount;
        }

        public void setShopsCount(long shopsCount) {
            this.shopsCount = shopsCount;
        }

        public long getConfirmedShopsCount() {
            return confirmedShopsCount;
        }

        public void setConfirmedShopsCount(long confirmedShopsCount) {
            this.confirmedShopsCount = confirmedShopsCount;
        }

        public long getRejectedShopsCount() {
            return rejectedShopsCount;
        }

        public void setRejectedShopsCount(long rejectedShopsCount) {
            this.rejectedShopsCount = rejectedShopsCount;
        }

        public long getPendingShopsCount() {
            return pendingShopsCount;
        }

        public void setPendingShopsCount(long pendingShopsCount) {
            this.pendingShopsCount = pendingShopsCount;
        }
    }

    public static class OrderStatistics {
        private List<OrderStatusInfoStatistics> orderStatusesStatistics;

        public List<OrderStatusInfoStatistics> getOrderStatusesStatistics() {
            return orderStatusesStatistics;
        }

        public void setOrderStatusesStatistics(List<OrderStatusInfoStatistics> orderStatusesStatistics) {
            this.orderStatusesStatistics = orderStatusesStatistics;
        }
    }

    public static class OrderStatusInfoStatistics {
        private long count;
        private ShopOrderStatusInfo status;

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public ShopOrderStatusInfo getStatus() {
            return status;
        }

        public void setStatus(ShopOrderStatusInfo status) {
            this.status = status;
        }
    }
}