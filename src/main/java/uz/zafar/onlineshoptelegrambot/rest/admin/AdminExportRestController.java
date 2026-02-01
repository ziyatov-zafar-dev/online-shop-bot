package uz.zafar.onlineshoptelegrambot.rest.admin;


import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.config.TelegramProperties;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus;

import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;
import uz.zafar.onlineshoptelegrambot.db.entity.order.OrderItem;
import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;
import uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus;
import uz.zafar.onlineshoptelegrambot.db.repositories.*;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.CategoryRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeImageRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/export")
public class AdminExportRestController {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat PDF_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,##0.00");

    private final UserRepository userRepository;
    private final BotSellerRepository botSellerRepository;
    private final BotCustomerRepository botCustomerRepository;
    private final LikeRepository likeRepository;
    private final TelegramProperties telegramProperties;
    private final CommentRepository commentRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeImageRepository productTypeImageRepository;
    private final SellerRepository sellerRepository;
    private final OrderItemRepository orderItemRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final AdsRepository adsRepository;
    private final AdsButtonRepository adsButtonRepository;
    private final DiscountRepository discountRepository;
    private final AdminCardRepository adminCardRepository;

    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    public AdminExportRestController(UserRepository userRepository, BotSellerRepository botSellerRepository, BotCustomerRepository botCustomerRepository, LikeRepository likeRepository, TelegramProperties telegramProperties, CommentRepository commentRepository, ShopOrderRepository shopOrderRepository, ShopRepository shopRepository, ProductRepository productRepository, CategoryRepository categoryRepository, PaymentRepository paymentRepository, ProductTypeRepository productTypeRepository, ProductTypeImageRepository productTypeImageRepository, SellerRepository sellerRepository, OrderItemRepository orderItemRepository, SubscriptionPlanRepository subscriptionPlanRepository, AdsRepository adsRepository, AdsButtonRepository adsButtonRepository, DiscountRepository discountRepository, AdminCardRepository adminCardRepository) {
        this.userRepository = userRepository;
        this.botSellerRepository = botSellerRepository;
        this.botCustomerRepository = botCustomerRepository;
        this.likeRepository = likeRepository;
        this.telegramProperties = telegramProperties;
        this.commentRepository = commentRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.paymentRepository = paymentRepository;
        this.productTypeRepository = productTypeRepository;
        this.productTypeImageRepository = productTypeImageRepository;
        this.sellerRepository = sellerRepository;
        this.orderItemRepository = orderItemRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.adsRepository = adsRepository;
        this.adsButtonRepository = adsButtonRepository;
        this.discountRepository = discountRepository;
        this.adminCardRepository = adminCardRepository;
    }

    @GetMapping("/excel/orders")
    public ResponseEntity<ByteArrayResource> exportOrdersToExcel(
            @RequestParam(required = false,name = "s") String startDate,
            @RequestParam(required = false,name = "e") String endDate,
            @RequestParam(required = false) String status) {
        try {
            List<uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder> allOrders = shopOrderRepository.findAll();
            List<uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder> orders = allOrders;
            if (status != null && !status.isEmpty()) {
                try {
                    uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus orderStatus =
                            uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus.valueOf(status.toUpperCase());
                    orders = allOrders.stream()
                            .filter(order -> order.getStatus() == orderStatus)
                            .toList();
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid status parameter. Valid values: " +
                            Arrays.toString(uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus.values()));
                }
            }

            // Sana oralig'i bo'yicha filter
            LocalDate start = null;
            LocalDate end = null;

            // Agar startDate null bo'lsa, eng boshidan (1970-01-01)
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate);
            } else {
                // Eng qadimgi sanani topish yoki 1970 yil boshidan
                start = orders.stream()
                        .map(order -> order.getCreatedAt())
                        .filter(Objects::nonNull)
                        .min(LocalDateTime::compareTo)
                        .map(LocalDateTime::toLocalDate)
                        .orElse(LocalDate.of(1970, 1, 1));
            }

            // Agar endDate null bo'lsa, hozirgacha (bugungi kun)
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate);
            } else {
                end = LocalDate.now();
            }

            // Sana oralig'iga filter qo'llash
            final LocalDate finalStart = start;
            final LocalDate finalEnd = end;

            orders = orders.stream()
                    .filter(order -> {
                        LocalDateTime orderDate = order.getCreatedAt();
                        if (orderDate == null) {
                            return false;
                        }
                        LocalDate date = orderDate.toLocalDate();
                        return !date.isBefore(finalStart) && !date.isAfter(finalEnd);
                    })
                    .toList();

            // Create Excel workbook
            Workbook workbook = new XSSFWorkbook();

            // 1. Orders sheet
            Sheet ordersSheet = workbook.createSheet("Orders");

            // Create header
            Row headerRow = ordersSheet.createRow(0);
            String[] headers = {
                    "ID", "Shop Name", "Customer", "Phone", "Status",
                    "Delivery Type", "Total Amount", "Delivery Price",
                    "Address", "Latitude", "Longitude", "Payment Image URL",
                    "Created At", "Updated At"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data
            int rowNum = 1;
            for (uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder order : orders) {
                Row row = ordersSheet.createRow(rowNum++);

                row.createCell(0).setCellValue(order.getPkey() != null ? order.getPkey().toString() : "");
                row.createCell(1).setCellValue(order.getShop() != null ?
                        (order.getShop().getNameUz() != null ? order.getShop().getNameUz() : "") : "");
                row.createCell(2).setCellValue(order.getCustomer() != null ?
                        (order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName()) : "");
                row.createCell(3).setCellValue(order.getPhone() != null ? order.getPhone() : "");

                // Status description with delivery type
                String statusDescription = order.getStatus() != null ? order.getStatus().name() : "UNKNOWN";
                if (order.getDeliveryType() != null && order.getStatus() != null) {
                    try {
                        switch (order.getStatus()) {
                            case NEW:
                                statusDescription = order.getStatus().getDescriptionUz(order.getDeliveryType());
                                break;
                            case PAYMENT_COMPLETED:
                                statusDescription = order.getStatus().getDescriptionUz(order.getDeliveryType());
                                break;
                            case ACCEPTED:
                                statusDescription = order.getStatus().getDescriptionUz(order.getDeliveryType());
                                break;
                            case PREPARING:
                                statusDescription = order.getStatus().getDescriptionUz(order.getDeliveryType());
                                break;
                            case SENT:
                                statusDescription = order.getStatus().getDescriptionUz(order.getDeliveryType());
                                break;
                            case COMPLETED:
                                statusDescription = order.getStatus().getDescriptionUz(order.getDeliveryType());
                                break;
                            case CANCELLED:
                                statusDescription = order.getStatus().getDescriptionUz(order.getDeliveryType());
                                break;
                        }
                    } catch (Exception e) {
                        // Agar description olishda xato bo'lsa, oddiy nomini qo'yish
                        statusDescription = order.getStatus().name();
                    }
                }
                row.createCell(4).setCellValue(statusDescription);

                row.createCell(5).setCellValue(order.getDeliveryType() != null ? order.getDeliveryType().name() : "");
                row.createCell(6).setCellValue(order.getTotalAmount() != null ?
                        order.getTotalAmount().doubleValue() : 0.0);
                row.createCell(7).setCellValue(order.getDeliveryPrice() != null ?
                        order.getDeliveryPrice().doubleValue() : 0.0);
                row.createCell(8).setCellValue(order.getAddress() != null ? order.getAddress() : "");
                row.createCell(9).setCellValue(order.getLatitude() != null ? order.getLatitude() : 0.0);
                row.createCell(10).setCellValue(order.getLongitude() != null ? order.getLongitude() : 0.0);
                row.createCell(11).setCellValue(order.getPaymentImageUrl() != null ? order.getPaymentImageUrl() : "");
                row.createCell(12).setCellValue(order.getCreatedAt() != null ?
                        order.getCreatedAt().format(DATE_FORMATTER) : "");
                row.createCell(13).setCellValue(order.getUpdatedAt() != null ?
                        order.getUpdatedAt().format(DATE_FORMATTER) : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                ordersSheet.autoSizeColumn(i);
            }

            // 2. Order Items sheet
            Sheet itemsSheet = workbook.createSheet("Order Items");

            String[] itemHeaders = {
                    "Order ID", "Product Name", "Product Type", "Quantity",
                    "Price", "Total Price"
            };

            Row itemHeaderRow = itemsSheet.createRow(0);
            for (int i = 0; i < itemHeaders.length; i++) {
                Cell cell = itemHeaderRow.createCell(i);
                cell.setCellValue(itemHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int itemRowNum = 1;
            for (uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder order : orders) {
                List<uz.zafar.onlineshoptelegrambot.db.entity.order.OrderItem> orderItems =
                        orderItemRepository.findByShopOrder(order);

                for (uz.zafar.onlineshoptelegrambot.db.entity.order.OrderItem item : orderItems) {
                    Row row = itemsSheet.createRow(itemRowNum++);

                    row.createCell(0).setCellValue(order.getPkey() != null ? order.getPkey().toString() : "");

                    if (item.getProductType() != null && item.getProductType().getProduct() != null) {
                        row.createCell(1).setCellValue(
                                item.getProductType().getProduct().getNameUz() != null ?
                                        item.getProductType().getProduct().getNameUz() : "");
                    } else {
                        row.createCell(1).setCellValue("");
                    }

                    row.createCell(2).setCellValue(item.getProductType() != null ?
                            item.getProductType().getNameUz() != null ?
                                    item.getProductType().getNameUz() : "" : "");

                    row.createCell(3).setCellValue(item.getQuantity() != null ? item.getQuantity() : 0);
                    row.createCell(4).setCellValue(item.getPrice() != null ? item.getPrice().doubleValue() : 0.0);
                    row.createCell(5).setCellValue(item.getTotalPrice() != null ? item.getTotalPrice().doubleValue() : 0.0);
                }
            }

            // Auto-size columns for items sheet
            for (int i = 0; i < itemHeaders.length; i++) {
                itemsSheet.autoSizeColumn(i);
            }

            // 3. Summary sheet
            Sheet summarySheet = workbook.createSheet("Summary");

            // Summary data - ShopOrderStatus enumidagi barcha statuslar uchun
            Object[][] summaryData = {
                    {"Total Orders", orders.size()},
                    {"Total Amount", orders.stream()
                            .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue()},
                    {"NEW Orders", orders.stream()
                            .filter(order -> order.getStatus() == uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus.NEW).count()},
                    {"PAYMENT_COMPLETED Orders", orders.stream()
                            .filter(order -> order.getStatus() == uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus.PAYMENT_COMPLETED).count()},
                    {"ACCEPTED Orders", orders.stream()
                            .filter(order -> order.getStatus() == uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus.ACCEPTED).count()},
                    {"PREPARING Orders", orders.stream()
                            .filter(order -> order.getStatus() == uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus.PREPARING).count()},
                    {"SENT Orders", orders.stream()
                            .filter(order -> order.getStatus() == uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus.SENT).count()},
                    {"COMPLETED Orders", orders.stream()
                            .filter(order -> order.getStatus() == uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus.COMPLETED).count()},
                    {"CANCELLED Orders", orders.stream()
                            .filter(order -> order.getStatus() == uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus.CANCELLED).count()}
            };

            for (int i = 0; i < summaryData.length; i++) {
                Row row = summarySheet.createRow(i);
                row.createCell(0).setCellValue(summaryData[i][0].toString());
                if (summaryData[i][1] instanceof Number) {
                    row.createCell(1).setCellValue(((Number) summaryData[i][1]).doubleValue());
                } else {
                    row.createCell(1).setCellValue(summaryData[i][1].toString());
                }
            }

            // Auto-size summary columns
            summarySheet.autoSizeColumn(0);
            summarySheet.autoSizeColumn(1);

            // 4. Additional statistics sheet
            Sheet statsSheet = workbook.createSheet("Statistics");

            // Calculate additional statistics
            BigDecimal totalDeliveryPrice = orders.stream()
                    .map(order -> order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalOrderAmount = orders.stream()
                    .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal averageOrderValue = orders.isEmpty() ? BigDecimal.ZERO :
                    totalOrderAmount.divide(BigDecimal.valueOf(orders.size()), 2, java.math.RoundingMode.HALF_UP);

            long ordersWithDelivery = orders.stream()
                    .filter(order -> order.getDeliveryType() != null)
                    .count();

            // Date range description
            String dateRangeDescription;
            if (startDate == null && endDate == null) {
                dateRangeDescription = "All time (from earliest to latest)";
            } else if (startDate == null) {
                dateRangeDescription = "From beginning to " + endDate;
            } else if (endDate == null) {
                dateRangeDescription = "From " + startDate + " to now";
            } else {
                dateRangeDescription = startDate + " to " + endDate;
            }

            Object[][] statsData = {
                    {"Total Orders", orders.size()},
                    {"Total Order Amount", totalOrderAmount.doubleValue()},
                    {"Total Delivery Price", totalDeliveryPrice.doubleValue()},
                    {"Total Revenue", totalOrderAmount.add(totalDeliveryPrice).doubleValue()},
                    {"Average Order Value", averageOrderValue.doubleValue()},
                    {"Orders with Delivery", ordersWithDelivery},
                    {"Orders without Delivery", orders.size() - ordersWithDelivery},
                    {"Date Range", dateRangeDescription},
                    {"Status Filter", status != null && !status.isEmpty() ? status : "All statuses"},
                    {"Report Generated", LocalDateTime.now().format(DATE_FORMATTER)}
            };

            for (int i = 0; i < statsData.length; i++) {
                Row row = statsSheet.createRow(i);
                row.createCell(0).setCellValue(statsData[i][0].toString());
                if (statsData[i][1] instanceof Number) {
                    row.createCell(1).setCellValue(((Number) statsData[i][1]).doubleValue());
                } else {
                    row.createCell(1).setCellValue(statsData[i][1].toString());
                }
            }

            statsSheet.autoSizeColumn(0);
            statsSheet.autoSizeColumn(1);

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            byte[] bytes = outputStream.toByteArray();
            String fileName = "orders_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (IllegalArgumentException e1) {
            // Invalid status parameter
            throw new RuntimeException("Invalid status parameter. Valid values: " +
                    Arrays.toString(uz.zafar.onlineshoptelegrambot.db.entity.order.enums.ShopOrderStatus.values()), e1);
        } catch (IOException e2) {
            throw new RuntimeException("Error exporting orders to Excel", e2);
        }
    }
    @GetMapping("/excel/products")
    public ResponseEntity<ByteArrayResource> exportProductsToExcel(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String categoryId) {

        try {
            List<uz.zafar.onlineshoptelegrambot.db.entity.category.Product> products;

            if (status != null && !status.isEmpty()) {
                ProductStatus productStatus = ProductStatus.valueOf(status.toUpperCase());
                products = productRepository.getAllProducts().stream()
                        .filter(product -> product.getStatus() == productStatus)
                        .toList();
            } else {
                products = productRepository.getAllProducts();
            }

            // Filter by category if provided
            if (categoryId != null && !categoryId.isEmpty()) {
                products = products.stream()
                        .filter(product -> product.getCategory() != null &&
                                product.getCategory().getPkey().toString().equals(categoryId))
                        .toList();
            }

            // Create Excel workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Products");

            // Create header
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "ID", "Name (UZ)", "Name (RU)", "Name (EN)", "Name (CYR)",
                    "SKU", "Category", "Shop", "Status", "Min Price",
                    "Max Price", "Average Price", "Total Stock", "Product Types Count",
                    "Images Count", "Likes", "Comments", "Has Discount",
                    "Discount Type", "Discount Value", "Created At", "Updated At", "Description (UZ)"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data
            int rowNum = 1;
            for (uz.zafar.onlineshoptelegrambot.db.entity.category.Product product : products) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(product.getPkey() != null ? product.getPkey().toString() : "");
                row.createCell(1).setCellValue(product.getNameUz() != null ? product.getNameUz() : "");
                row.createCell(2).setCellValue(product.getNameRu() != null ? product.getNameRu() : "");
                row.createCell(3).setCellValue(product.getNameEn() != null ? product.getNameEn() : "");
                row.createCell(4).setCellValue(product.getNameCyr() != null ? product.getNameCyr() : "");
                row.createCell(5).setCellValue(product.getSku() != null ? product.getSku() : "");

                row.createCell(6).setCellValue(product.getCategory() != null ?
                        product.getCategory().getNameUz() != null ?
                                product.getCategory().getNameUz() : "" : "");

                row.createCell(7).setCellValue(product.getShop() != null ?
                        product.getShop().getNameUz() != null ?
                                product.getShop().getNameUz() : "" : "");

                row.createCell(8).setCellValue(product.getStatus() != null ? product.getStatus().name() : "");

                // Get product types for price calculations
                List<uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType> productTypes =
                        productTypeRepository.findAllByProductId(product.getPkey());

                // Calculate prices
                BigDecimal minPrice = BigDecimal.ZERO;
                BigDecimal maxPrice = BigDecimal.ZERO;
                BigDecimal avgPrice = BigDecimal.ZERO;
                long totalStock = 0;
                int imagesCount = 0;
                boolean hasDiscount = false;
                String discountType = "";
                String discountValue = "";

                if (!productTypes.isEmpty()) {
                    // Find min and max prices
                    minPrice = productTypes.stream()
                            .filter(pt -> !pt.getDeleted())
                            .map(pt -> pt.getPrice())
                            .min(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

                    maxPrice = productTypes.stream()
                            .filter(pt -> !pt.getDeleted())
                            .map(pt -> pt.getPrice())
                            .max(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

                    // Calculate average price
                    BigDecimal totalPrice = productTypes.stream()
                            .filter(pt -> !pt.getDeleted())
                            .map(pt -> pt.getPrice())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    long activeTypesCount = productTypes.stream()
                            .filter(pt -> !pt.getDeleted())
                            .count();

                    avgPrice = activeTypesCount > 0 ?
                            totalPrice.divide(BigDecimal.valueOf(activeTypesCount), 2, java.math.RoundingMode.HALF_UP) :
                            BigDecimal.ZERO;

                    // Calculate total stock
                    totalStock = productTypes.stream()
                            .filter(pt -> !pt.getDeleted())
                            .mapToLong(pt -> pt.getStock() != null ? pt.getStock() : 0)
                            .sum();

                    // Count images
                    imagesCount = productTypes.stream()
                            .filter(pt -> !pt.getDeleted())
                            .mapToInt(pt -> pt.getImages() != null ? pt.getImages().size() : 0)
                            .sum();

                    // Check if product has discount
                    if (product.getDiscount() != null) {
                        hasDiscount = true;
                        discountType = product.getDiscount().getType() != null ?
                                product.getDiscount().getType().name() : "";
                        discountValue = product.getDiscount().getValue() != null ?
                                product.getDiscount().getValue().toString() : "";
                    }
                }

                row.createCell(9).setCellValue(minPrice.doubleValue());
                row.createCell(10).setCellValue(maxPrice.doubleValue());
                row.createCell(11).setCellValue(avgPrice.doubleValue());
                row.createCell(12).setCellValue(totalStock);
                row.createCell(13).setCellValue(productTypes.size());
                row.createCell(14).setCellValue(imagesCount);

                // Likes count
                long likesCount = likeRepository.findAllByProductId(product.getPkey()).size();
                row.createCell(15).setCellValue(likesCount);

                // Comments count
                long commentsCount = commentRepository.findAllByProductId(product.getPkey()).size();
                row.createCell(16).setCellValue(commentsCount);

                // Has Discount
                row.createCell(17).setCellValue(hasDiscount ? "Yes" : "No");
                row.createCell(18).setCellValue(discountType);
                row.createCell(19).setCellValue(discountValue);

                row.createCell(20).setCellValue(product.getCreatedAt() != null ?
                        product.getCreatedAt().format(DATE_FORMATTER) : "");
                row.createCell(21).setCellValue(product.getUpdatedAt() != null ?
                        product.getUpdatedAt().format(DATE_FORMATTER) : "");
                row.createCell(22).setCellValue(product.getDescriptionUz() != null ?
                        product.getDescriptionUz() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Product Types sheet
            Sheet typesSheet = workbook.createSheet("Product Types");

            String[] typeHeaders = {
                    "Product ID", "Product Name", "Type Name (UZ)", "Type Name (RU)",
                    "Type Name (EN)", "Type Name (CYR)", "Stock", "Price",
                    "Has Discount", "SKU", "Images Count",
                    "Created At", "Deleted", "Main Image URL"
            };

            Row typeHeaderRow = typesSheet.createRow(0);
            for (int i = 0; i < typeHeaders.length; i++) {
                Cell cell = typeHeaderRow.createCell(i);
                cell.setCellValue(typeHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int typeRowNum = 1;
            for (uz.zafar.onlineshoptelegrambot.db.entity.category.Product product : products) {
                List<uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType> productTypes =
                        productTypeRepository.findAllByProductId(product.getPkey());

                for (uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType type : productTypes) {
                    Row row = typesSheet.createRow(typeRowNum++);

                    row.createCell(0).setCellValue(product.getPkey() != null ? product.getPkey().toString() : "");
                    row.createCell(1).setCellValue(product.getNameUz() != null ? product.getNameUz() : "");
                    row.createCell(2).setCellValue(type.getNameUz() != null ? type.getNameUz() : "");
                    row.createCell(3).setCellValue(type.getNameRu() != null ? type.getNameRu() : "");
                    row.createCell(4).setCellValue(type.getNameEn() != null ? type.getNameEn() : "");
                    row.createCell(5).setCellValue(type.getNameCyr() != null ? type.getNameCyr() : "");
                    row.createCell(6).setCellValue(type.getStock() != null ? type.getStock() : 0);
                    row.createCell(7).setCellValue(type.getPrice() != null ? type.getPrice().doubleValue() : 0.0);

                    // Check if product has discount (product level discount applies to all types)
                    boolean typeHasDiscount = product.getDiscount() != null;
                    row.createCell(8).setCellValue(typeHasDiscount ? "Yes" : "No");

                    // Product SKU (not type SKU)
                    row.createCell(9).setCellValue(product.getSku() != null ? product.getSku() : "");

                    // Images count
                    int typeImagesCount = type.getImages() != null ? type.getImages().size() : 0;
                    row.createCell(10).setCellValue(typeImagesCount);

                    row.createCell(11).setCellValue(type.getCreatedAt() != null ?
                            type.getCreatedAt().format(DATE_FORMATTER) : "");
                    row.createCell(12).setCellValue(type.getDeleted() ? "Yes" : "No");

                    // Main image URL (if exists)
                    String mainImageUrl = "";
                    if (type.getImages() != null && !type.getImages().isEmpty()) {
                        mainImageUrl = type.getImages().stream()
                                .filter(img -> img.getMain() != null && img.getMain())
                                .findFirst()
                                .map(img -> img.getImageUrl())
                                .orElse(type.getImages().get(0).getImageUrl());
                    }
                    row.createCell(13).setCellValue(mainImageUrl);
                }
            }

            // Auto-size type columns
            for (int i = 0; i < typeHeaders.length; i++) {
                typesSheet.autoSizeColumn(i);
            }

            // Discounts sheet
            Sheet discountsSheet = workbook.createSheet("Product Discounts");

            String[] discountHeaders = {
                    "Product ID", "Product Name", "Discount Type", "Applied To",
                    "Discount Value", "Created At", "Updated At"
            };

            Row discountHeaderRow = discountsSheet.createRow(0);
            for (int i = 0; i < discountHeaders.length; i++) {
                Cell cell = discountHeaderRow.createCell(i);
                cell.setCellValue(discountHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int discountRowNum = 1;
            for (uz.zafar.onlineshoptelegrambot.db.entity.category.Product product : products) {
                if (product.getDiscount() != null) {
                    Row row = discountsSheet.createRow(discountRowNum++);

                    row.createCell(0).setCellValue(product.getPkey() != null ? product.getPkey().toString() : "");
                    row.createCell(1).setCellValue(product.getNameUz() != null ? product.getNameUz() : "");
                    row.createCell(2).setCellValue(product.getDiscount().getType() != null ?
                            product.getDiscount().getType().name() : "");
                    row.createCell(3).setCellValue(product.getDiscount().getAppliedTo() != null ?
                            product.getDiscount().getAppliedTo().name() : "");
                    row.createCell(4).setCellValue(product.getDiscount().getValue() != null ?
                            product.getDiscount().getValue().doubleValue() : 0.0);
                    row.createCell(5).setCellValue(product.getDiscount().getCreatedAt() != null ?
                            product.getDiscount().getCreatedAt().format(DATE_FORMATTER) : "");
                    row.createCell(6).setCellValue(product.getDiscount().getUpdatedAt() != null ?
                            product.getDiscount().getUpdatedAt().format(DATE_FORMATTER) : "");
                }
            }

            for (int i = 0; i < discountHeaders.length; i++) {
                discountsSheet.autoSizeColumn(i);
            }

            // 4. Summary sheet
            Sheet summarySheet = workbook.createSheet("Summary");

            // Calculate summary statistics
            long totalProducts = products.size();
            long draftProducts = products.stream()
                    .filter(p -> p.getStatus() == ProductStatus.DRAFT)
                    .count();
            long openProducts = products.stream()
                    .filter(p -> p.getStatus() == ProductStatus.OPEN)
                    .count();
            long deletedProducts = products.stream()
                    .filter(p -> p.getStatus() == ProductStatus.DELETED)
                    .count();

            long productsWithDiscount = products.stream()
                    .filter(p -> p.getDiscount() != null)
                    .count();

            long totalProductTypes = products.stream()
                    .mapToLong(p -> productTypeRepository.findAllByProductId(p.getPkey()).size())
                    .sum();

            long totalImages = productTypeImageRepository.count();

            Object[][] summaryData = {
                    {"Total Products", totalProducts},
                    {"Draft Products", draftProducts},
                    {"Open Products", openProducts},
                    {"Deleted Products", deletedProducts},
                    {"Products with Discount", productsWithDiscount},
                    {"Total Product Types", totalProductTypes},
                    {"Total Images", totalImages},
                    {"Total Likes", products.stream()
                            .mapToLong(p -> likeRepository.findAllByProductId(p.getPkey()).size())
                            .sum()},
                    {"Total Comments", products.stream()
                            .mapToLong(p -> commentRepository.findAllByProductId(p.getPkey()).size())
                            .sum()},
                    {"Average Product Types per Product",
                            totalProducts > 0 ? (double) totalProductTypes / totalProducts : 0},
                    {"Percentage with Discount",
                            totalProducts > 0 ? (double) productsWithDiscount / totalProducts * 100 : 0},
                    {"Status Filter", status != null ? status : "All statuses"},
                    {"Category Filter", categoryId != null ? categoryId : "All categories"},
                    {"Report Generated", LocalDateTime.now().format(DATE_FORMATTER)}
            };

            for (int i = 0; i < summaryData.length; i++) {
                Row row = summarySheet.createRow(i);
                row.createCell(0).setCellValue(summaryData[i][0].toString());
                if (summaryData[i][1] instanceof Number) {
                    row.createCell(1).setCellValue(((Number) summaryData[i][1]).doubleValue());
                } else {
                    row.createCell(1).setCellValue(summaryData[i][1].toString());
                }
            }

            summarySheet.autoSizeColumn(0);
            summarySheet.autoSizeColumn(1);

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            byte[] bytes = outputStream.toByteArray();
            String fileName = "products_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status parameter. Valid values: " +
                    Arrays.toString(ProductStatus.values()), e);
        } catch (IOException e) {
            throw new RuntimeException("Error exporting products to Excel", e);
        }
    }

    @GetMapping("/excel/shops")
    public ResponseEntity<ByteArrayResource> exportShopsToExcel(
            @RequestParam(required = false) String status) {
        try {
            List<uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop> shops;

            if ("active".equalsIgnoreCase(status)) {
                shops = shopRepository.activeShops();
            } else if ("pending".equalsIgnoreCase(status)) {
                shops = shopRepository.notConfirmedShops();
            } else if ("deleted".equalsIgnoreCase(status)) {
                shops = shopRepository.deletedShops();
            } else {
                shops = shopRepository.findAll();
            }

            // Create Excel workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Shops");

            // Create header
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "ID", "Name (UZ)", "Name (RU)", "Name (EN)", "Name (CYR)",
                    "Seller", "Phone", "Email", "Telegram", "Instagram",
                    "Facebook", "Website", "Address", "Active",
                    "Delivery Price", "Has Delivery", "Work Start", "Work End",
                    "Products Count", "Created At", "Updated At"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data
            int rowNum = 1;
            for (uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop shop : shops) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(shop.getPkey() != null ? shop.getPkey().toString() : "");
                row.createCell(1).setCellValue(shop.getNameUz() != null ? shop.getNameUz() : "");
                row.createCell(2).setCellValue(shop.getNameRu() != null ? shop.getNameRu() : "");
                row.createCell(3).setCellValue(shop.getNameEn() != null ? shop.getNameEn() : "");
                row.createCell(4).setCellValue(shop.getNameCyr() != null ? shop.getNameCyr() : "");

                row.createCell(5).setCellValue(shop.getSeller() != null ?
                        (shop.getSeller().getPhone() != null ? shop.getSeller().getPhone() : "") : "");

                row.createCell(6).setCellValue(shop.getPhone() != null ? shop.getPhone() : "");
                row.createCell(7).setCellValue(shop.getEmail() != null ? shop.getEmail() : "");
                row.createCell(8).setCellValue(shop.getTelegram() != null ? shop.getTelegram() : "");
                row.createCell(9).setCellValue(shop.getInstagram() != null ? shop.getInstagram() : "");
                row.createCell(10).setCellValue(shop.getFacebook() != null ? shop.getFacebook() : "");
                row.createCell(11).setCellValue(shop.getWebsite() != null ? shop.getWebsite() : "");
                row.createCell(12).setCellValue(shop.getAddress() != null ? shop.getAddress() : "");
                row.createCell(13).setCellValue(shop.getActive() != null ? (shop.getActive() ? "Yes" : "No") : "No");
                row.createCell(14).setCellValue(shop.getDeliveryPrice() != null ?
                        shop.getDeliveryPrice().doubleValue() : 0.0);
                row.createCell(15).setCellValue(shop.getHasDelivery() != null ?
                        (shop.getHasDelivery() ? "Yes" : "No") : "No");
                row.createCell(16).setCellValue(shop.getWorkStartTime() != null ?
                        shop.getWorkStartTime().toString() : "");
                row.createCell(17).setCellValue(shop.getWorkEndTime() != null ?
                        shop.getWorkEndTime().toString() : "");

                // Products count
                long productsCount = shop.getProducts() != null ? shop.getProducts().size() : 0;
                row.createCell(18).setCellValue(productsCount);

                row.createCell(19).setCellValue(shop.getCreatedAt() != null ?
                        shop.getCreatedAt().format(DATE_FORMATTER) : "");
                row.createCell(20).setCellValue(shop.getUpdatedAt() != null ?
                        shop.getUpdatedAt().format(DATE_FORMATTER) : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            byte[] bytes = outputStream.toByteArray();
            String fileName = "shops_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Error exporting shops to Excel", e);
        }
    }

    @GetMapping("/excel/sellers")
    public ResponseEntity<ByteArrayResource> exportSellersToExcel() {

        try {
            List<uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller> sellers = sellerRepository.findAll();

            // Create Excel workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sellers");

            // Create header
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "ID", "Phone", "Card Number", "Card Type", "Card Owner",
                    "Balance", "Status", "Gender", "Plan Expires At",
                    "Subscription Plan", "Shops Count", "Created Card Time"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data
            int rowNum = 1;
            for (uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller seller : sellers) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(seller.getPkey() != null ? seller.getPkey().toString() : "");
                row.createCell(1).setCellValue(seller.getPhone() != null ? seller.getPhone() : "");
                row.createCell(2).setCellValue(seller.getCardNumber() != null ? seller.getCardNumber() : "");
                row.createCell(3).setCellValue(seller.getCardType() != null ? seller.getCardType().name() : "");
                row.createCell(4).setCellValue(seller.getCardOwner() != null ? seller.getCardOwner() : "");
                row.createCell(5).setCellValue(seller.getBalance() != null ? seller.getBalance().doubleValue() : 0.0);
                row.createCell(6).setCellValue(seller.getStatus() != null ? seller.getStatus().name() : "");
                row.createCell(7).setCellValue(seller.getGender() != null ? seller.getGender().name() : "");
                row.createCell(8).setCellValue(seller.getPlanExpiresAt() != null ?
                        seller.getPlanExpiresAt().format(DATE_FORMATTER) : "");

                // Subscription plan name
                String planName = "";
                if (seller.getSubscriptionPlanId() != null) {
                    var planOpt = subscriptionPlanRepository.findById(seller.getSubscriptionPlanId());
                    if (planOpt.isPresent()) {
                        planName = planOpt.get().getName() != null ? planOpt.get().getName().name() : "";
                    }
                }
                row.createCell(9).setCellValue(planName);

                // Shops count (would need additional query)
                row.createCell(10).setCellValue(0);

                row.createCell(11).setCellValue(seller.getCardAddedTime() != null ?
                        seller.getCardAddedTime().format(DATE_FORMATTER) : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            byte[] bytes = outputStream.toByteArray();
            String fileName = "sellers_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Error exporting sellers to Excel", e);
        }
    }
    @GetMapping("/pdf/order/{orderId}")
    public ResponseEntity<ByteArrayResource> exportOrderToPdf(@PathVariable("orderId") UUID orderId) {
        try {
            Optional<ShopOrder> orderOpt = shopOrderRepository.findById(orderId);

            if (orderOpt.isEmpty()) {
                throw new RuntimeException("Order not found with ID: " + orderId);
            }
            ShopOrder order = orderOpt.get();
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new PdfPageEvent());
            document.open();
            addHeader(document, order);
            addOrderInfo(document, order);
            addCustomerInfo(document, order);

            addOrderItemsTable(document, order);
            addOrderSummary(document, order);
            addFooterNotes(document);

            document.close();

            byte[] bytes = outputStream.toByteArray();
            String fileName = "order_information-" + order.getPkey().toString() + "_" +
                    System.currentTimeMillis() + ".pdf";

            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF for order: " + orderId, e);
        }
    }
    private void addHeader(Document document, ShopOrder order) throws DocumentException {
        // Company/Shop name
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("SH-Z FRIENDS MARKET", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        // Invoice title
        Font invoiceFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
        Paragraph invoice = new Paragraph("BUYURTMA XATI / INVOICE", invoiceFont);
        invoice.setAlignment(Element.ALIGN_CENTER);
        invoice.setSpacingAfter(20);
        document.add(invoice);

        // Horizontal line
        addHorizontalLine(document);
    }

    private void addOrderInfo(Document document, ShopOrder order) throws DocumentException {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);
        float[] columnWidths = {1f, 2f};
        table.setWidths(columnWidths);
        addTableRow(table, "Buyurtma raqami:", order.getPkey().toString(), headerFont, valueFont);
        String orderDate = order.getCreatedAt() != null ?
                order.getCreatedAt().format(DATE_FORMATTER) : "N/A";
        addTableRow(table, "Buyurtma sanasi:", orderDate, headerFont, valueFont);
        String status = getOrderStatusDescription(order);
        addTableRow(table, "Holati:", status, headerFont, valueFont);
        String deliveryType = order.getDeliveryType() != null ?
                order.getDeliveryType().name() : "N/A";
        addTableRow(table, "Yetkazish turi:", deliveryType, headerFont, valueFont);
        document.add(table);
    }

    private void addCustomerInfo(Document document, ShopOrder order) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("MIJOZ MA'LUMOTLARI", sectionFont);
        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(15);

        float[] columnWidths = {1f, 2f};
        table.setWidths(columnWidths);

        // Customer name
        String customerName = "";
        if (order.getCustomer() != null) {
            customerName = order.getCustomer().getFirstName() + " " +
                    (order.getCustomer().getLastName() != null ? order.getCustomer().getLastName() : "");
        }
        addTableRow(table, "Mijoz ismi:", customerName, headerFont, valueFont);

        // Phone
        addTableRow(table, "Telefon raqami:", order.getPhone() != null ? order.getPhone() : "N/A",
                headerFont, valueFont);

        // Address
        if (order.getAddress() != null && !order.getAddress().isEmpty()) {
            addTableRow(table, "Manzil:", order.getAddress(), headerFont, valueFont);
        }

        // Shop information
        if (order.getShop() != null) {
            addTableRow(table, "Do'kon:", order.getShop().getNameUz(), headerFont, valueFont);
            if (order.getShop().getPhone() != null) {
                addTableRow(table, "Do'kon telefoni:", order.getShop().getPhone(), headerFont, valueFont);
            }
        }

        document.add(table);
    }

    private void addOrderItemsTable(Document document, ShopOrder order) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("MAHSULOTLAR", sectionFont);
        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        List<OrderItem> orderItems = orderItemRepository.findByShopOrder(order);

        if (orderItems.isEmpty()) {
            Paragraph noItems = new Paragraph("Mahsulotlar topilmadi",
                    new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY));
            document.add(noItems);
            return;
        }

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        // Column widths
        float[] columnWidths = {0.5f, 2f, 1f, 0.8f, 1f, 1f};
        table.setWidths(columnWidths);

        // Table header
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        addTableHeader(table, "#", headerFont);
        addTableHeader(table, "Mahsulot", headerFont);
        addTableHeader(table, "Turi", headerFont);
        addTableHeader(table, "Miqdori", headerFont);
        addTableHeader(table, "Narxi", headerFont);
        addTableHeader(table, "Jami", headerFont);

        // Table data
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
        int itemNumber = 1;
        for (OrderItem item : orderItems) {
            table.addCell(createCell(String.valueOf(itemNumber++), dataFont, Element.ALIGN_CENTER));

            // Product name
            String productName = "N/A";
            if (item.getProductType() != null && item.getProductType().getProduct() != null) {
                productName = item.getProductType().getProduct().getNameUz();
            }
            table.addCell(createCell(productName, dataFont, Element.ALIGN_LEFT));

            // Product type
            String productType = item.getProductType() != null ?
                    item.getProductType().getNameUz() : "N/A";
            table.addCell(createCell(productType, dataFont, Element.ALIGN_LEFT));

            // Quantity
            table.addCell(createCell(String.valueOf(item.getQuantity()), dataFont, Element.ALIGN_CENTER));

            // Price
            String price = item.getPrice() != null ? PRICE_FORMAT.format(item.getPrice()) + " so'm" : "0.00 so'm";
            table.addCell(createCell(price, dataFont, Element.ALIGN_RIGHT));

            // Total
            String total = item.getTotalPrice() != null ? PRICE_FORMAT.format(item.getTotalPrice()) + " so'm" : "0.00 so'm";
            table.addCell(createCell(total, dataFont, Element.ALIGN_RIGHT));
        }

        document.add(table);
    }

    private void addOrderSummary(Document document, ShopOrder order) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("BUYURTMA JAMI", sectionFont);
        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        List<OrderItem> orderItems = orderItemRepository.findByShopOrder(order);

        // Calculate totals
        double productsTotal = orderItems.stream()
                .mapToDouble(item -> item.getTotalPrice() != null ?
                        item.getTotalPrice().doubleValue() : 0.0)
                .sum();

        double deliveryPrice = order.getDeliveryPrice() != null ?
                order.getDeliveryPrice().doubleValue() : 0.0;

        double grandTotal = productsTotal + deliveryPrice;

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);

        float[] columnWidths = {1.5f, 1f};
        table.setWidths(columnWidths);

        // Products total
        addSummaryRow(table, "Mahsulotlar jami:", PRICE_FORMAT.format(productsTotal) + " so'm",
                headerFont, valueFont);

        // Delivery price
        addSummaryRow(table, "Yetkazib berish:", PRICE_FORMAT.format(deliveryPrice) + " so'm",
                headerFont, valueFont);

        // Horizontal line
        PdfPCell lineCell = new PdfPCell(new Phrase(""));
        lineCell.setColspan(2);
        lineCell.setBorder(PdfPCell.BOTTOM);
        lineCell.setBorderWidth(1);
        lineCell.setBorderColor(BaseColor.LIGHT_GRAY);
        lineCell.setPadding(5);
        table.addCell(lineCell);

        // Grand total
        addSummaryRow(table, "JAMI TO'LOV:", PRICE_FORMAT.format(grandTotal) + " so'm",
                headerFont, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.RED));

        document.add(table);
    }

    private void addFooterNotes(Document document) throws DocumentException {
        Font notesFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);

        Paragraph notes = new Paragraph();
        notes.setSpacingBefore(20);

        notes.add(new Chunk("Eslatmalar:\n",
                new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.DARK_GRAY)));
        notes.add(new Chunk("• Ushbu hujjat rasmiy buyurtma xati hisoblanadi\n", notesFont));
        notes.add(new Chunk("• Mahsulotlarni qaytarish shartlari do'kon qoidalariga muvofiq\n", notesFont));
        notes.add(new Chunk("• Savollar bo'lsa do'kon administratoriga murojaat qiling\n", notesFont));
        notes.add(new Chunk("• Hujjat raqami: " + System.currentTimeMillis() + "\n", notesFont));
        notes.add(new Chunk("• Generatsiya qilingan sana: " +
                new java.util.Date().toString() + "\n", notesFont));

        document.add(notes);
    }

    // Helper methods
    private void addHorizontalLine(Document document) throws DocumentException {
        Paragraph line = new Paragraph();
        line.add(new Chunk("\n"));
        LineSeparator ls = new LineSeparator();
        ls.setLineWidth(1);
        ls.setLineColor(BaseColor.LIGHT_GRAY);
        line.add(new Chunk(ls));
        line.add(new Chunk("\n"));
        document.add(line);
    }

    private void addTableRow(PdfPTable table, String header, String value,
                             Font headerFont, Font valueFont) {
        table.addCell(createCell(header, headerFont, Element.ALIGN_LEFT));
        table.addCell(createCell(value, valueFont, Element.ALIGN_LEFT));
    }

    private void addSummaryRow(PdfPTable table, String header, String value,
                               Font headerFont, Font valueFont) {
        table.addCell(createCell(header, headerFont, Element.ALIGN_LEFT));
        table.addCell(createCell(value, valueFont, Element.ALIGN_RIGHT));
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        return cell;
    }

    private String getOrderStatusDescription(ShopOrder order) {
        if (order.getStatus() == null || order.getDeliveryType() == null) {
            return order.getStatus() != null ? order.getStatus().name() : "N/A";
        }

        try {
            return order.getStatus().getDescriptionUz(order.getDeliveryType());
        } catch (Exception e) {
            return order.getStatus().name();
        }
    }

    // PDF Page Event for header and footer
    class PdfPageEvent extends PdfPageEventHelper {
        private PdfTemplate total;
        private BaseFont baseFont;

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 16);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

                // Footer with page numbers
                PdfPTable footer = new PdfPTable(3);
                footer.setWidthPercentage(100);
                footer.setTotalWidth(document.right() - document.left());

                // Left cell - company name
                PdfPCell leftCell = new PdfPCell(new Phrase("SH-Z FRIENDS MARKET",
                        new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY)));
                leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                leftCell.setBorder(0);
                footer.addCell(leftCell);

                // Center cell - page number
                PdfPCell centerCell = new PdfPCell(new Phrase(String.format("Sahifa %d", writer.getPageNumber()),
                        new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY)));
                centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                centerCell.setBorder(0);
                footer.addCell(centerCell);

                // Right cell - date
                PdfPCell rightCell = new PdfPCell(new Phrase(
                        new java.util.Date().toString(),
                        new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY)));
                rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                rightCell.setBorder(0);
                footer.addCell(rightCell);

                // Write footer
                footer.writeSelectedRows(0, -1, document.left(), document.bottom() - 20,
                        writer.getDirectContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {}
    }
}