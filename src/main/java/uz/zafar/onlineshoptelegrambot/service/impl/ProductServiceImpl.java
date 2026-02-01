package uz.zafar.onlineshoptelegrambot.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.zafar.onlineshoptelegrambot.bot.TelegramBot;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductType;
import uz.zafar.onlineshoptelegrambot.db.entity.category.ProductTypeImage;
import uz.zafar.onlineshoptelegrambot.db.entity.common.Discount;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.AppliedTo;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.DiscountType;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SellerStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.repositories.DiscountRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.CategoryRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeImageRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductTypeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.common.request.AddDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.common.request.ChangeDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.product.request.*;
import uz.zafar.onlineshoptelegrambot.dto.product.response.CategoryResponse;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductResponse;
import uz.zafar.onlineshoptelegrambot.mapper.CategoryMapper;
import uz.zafar.onlineshoptelegrambot.mapper.ProductMapper;
import uz.zafar.onlineshoptelegrambot.service.ProductService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final BotSellerRepository botSellerRepository;
    private final SellerRepository sellerRepository;
    private final ShopRepository shopRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeImageRepository productTypeImageRepository;
    private final DiscountRepository discountRepository;


    public ProductServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, ProductMapper productMapper, ProductRepository productRepository, BotSellerRepository botSellerRepository, SellerRepository sellerRepository, ShopRepository shopRepository, ProductTypeRepository productTypeRepository, ProductTypeImageRepository productTypeImageRepository, DiscountRepository discountRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.productRepository = productRepository;
        this.botSellerRepository = botSellerRepository;
        this.sellerRepository = sellerRepository;
        this.shopRepository = shopRepository;
        this.productTypeRepository = productTypeRepository;
        this.productTypeImageRepository = productTypeImageRepository;
        this.discountRepository = discountRepository;
    }


    @Override
    public ResponseDto<List<ProductResponse>> myProducts(Long chatId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }
        List<Product> products = productRepository.myProducts(seller.getPkey(), List.of(ProductStatus.OPEN, ProductStatus.DRAFT));
        return ResponseDto.success(productMapper.toList(products));
    }

    @Override
    public ResponseDto<List<ProductResponse>> myProductsByCategoryId(Long chatId, UUID categoryId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_CATEGORY);
        }
        if (category.getActive() == null || category.getActive() == false) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_CATEGORY);
        }
        List<Product> products = productRepository.myProductsByCategoryId(seller.getPkey(), List.of(ProductStatus.OPEN, ProductStatus.DRAFT), categoryId);
        return ResponseDto.success(productMapper.toList(products));
    }

    @Override
    public ResponseDto<List<CategoryResponse>> allCategories() {
        return ResponseDto.success(categoryMapper.toList(categoryRepository.getAllCategories()));
    }

    @Override
    public ResponseDto<CategoryResponse> findByCategoryId(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) return ResponseDto.error(ErrorCode.NOT_FOUND_CATEGORY);
        return ResponseDto.success(categoryMapper.toResponse(category));
    }

    @Override
    public ResponseDto<ProductResponse> findByProductId(UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);

        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        if (product.getStatus() == ProductStatus.DELETED) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        return ResponseDto.success(productMapper.toResponse(product));
    }

    @Transactional
    @Override
    public ResponseDto<ProductResponse> addProduct(AddProductRequestDto req, Long chatId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }
        Shop shop = shopRepository.findById(req.getShopId()).orElse(null);
        if (shop == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_SHOP);
        }
        if (shop.getActive() == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_SHOP);
        }
        if (shop.getActive() == false) {
            return ResponseDto.error(ErrorCode.SHOP_NOT_CONFIRMED_ADMIN);
        }
        Category category = categoryRepository.findById(req.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_CATEGORY);
        }
        if (category.getActive() == null || category.getActive() == false) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_CATEGORY);
        }
        if (req.getProductTypes() == null || req.getProductTypes().isEmpty()) {
            return ResponseDto.error(ErrorCode.REQUIRED_PRODUCT_TYPES);
        }
        for (AddProductTypeRequestDto productType : req.getProductTypes()) {
            if (productType.getImages().isEmpty()) {
                return ResponseDto.error(ErrorCode.REQUIRED_PRODUCT_TYPE_IMAGES);
            }
        }
        Product product = new Product();
        product.setNameUz(req.getNameUz());
        product.setNameRu(req.getNameRu());
        product.setNameCyr(req.getNameCyr());
        product.setNameEn(req.getNameEn());
        product.setDescriptionUz(req.getDescriptionUz());
        product.setDescriptionCyr(req.getDescriptionCyr());
        product.setDescriptionRu(req.getDescriptionRu());
        product.setDescriptionEn(req.getDescriptionEn());
        product.setCategory(category);
        product.setShop(shop);
        product.setSku(generateSku(category, product));
        product.setStatus(ProductStatus.DRAFT);
        product = productRepository.save(product);
        List<ProductType> types = new ArrayList<>();
        if (!Boolean.TRUE.equals(req.getHasDiscount())) product.setDiscount(null);
        else {
            AddDiscountRequestDto cd = req.getDiscount();
            Discount discount = new Discount();
            if (cd.getType() == null) cd.setType(DiscountType.FIXED);
            discount.setType(cd.getType());
            discount.setAppliedTo(AppliedTo.PRODUCT);
            discount.setValue(cd.getValue());
            discount.setCreatedAt(LocalDateTime.now());
            discount.setUpdatedAt(LocalDateTime.now());
            discount.setProduct(product);
            discount.setSubscriptionPlanId(null);
            discount = discountRepository.save(discount);
            product.setDiscount(discount);
        }
        for (AddProductTypeRequestDto productType : req.getProductTypes()) {
            ProductType type = new ProductType();
            type.setNameUz(productType.getNameUz());
            type.setNameRu(productType.getNameRu());
            type.setNameEn(productType.getNameEn());
            type.setNameCyr(productType.getNameCyr());
            type.setPrice(productType.getPrice());
            type.setStock(productType.getStock());
            type.setDeleted(false);
            type.setProduct(product);
            type = productTypeRepository.save(type);
            List<ProductTypeImage> images = new ArrayList<>();
            int mainCount = 0;
            for (AddProductTypeImageRequestDto img : productType.getImages()) {
                ProductTypeImage image = new ProductTypeImage();
                image.setImageUrl(img.getImageUrl());
                image.setImgName(img.getImgName());
                image.setImgSize(img.getImgSize());
                if (img.getMain()) mainCount++;
                image.setMain(mainCount == 1 && img.getMain());
                image.setProductType(type);
                image.setCreatedAt(LocalDateTime.now());
                image.setUpdatedAt(LocalDateTime.now());
                image.setDeleted(false);
                image = productTypeImageRepository.save(image);
                images.add(image);
            }
            type.setImages(images);
            types.add(type);
        }
        product.setProductTypes(types);
        product = productRepository.save(product);
        return ResponseDto.success(productMapper.toResponse(product));
    }

    public String generateSku(Category category, Product product) {
        String categoryCode = code(category.getSlug(), 4);
        String productCode = code(product.getNameEn(), 4);

        String datePart = LocalDate.now().toString().replace("-", "").substring(0, 6);
        String randomPart = String.valueOf(1000 + new Random().nextInt(9000));

        return String.format("%s-%s-%s-%s", categoryCode, productCode, datePart, randomPart);
    }

    private static String code(String value, int length) {
        if (value == null || value.isBlank()) {
            return "XXXX";
        }
        value = value.replaceAll("[^a-zA-Z]", "").toUpperCase(Locale.ROOT);
        return value.length() >= length ? value.substring(0, length) : String.format("%-" + length + "s", value).replace(' ', 'X');
    }

    @Override
    @Transactional

    public ResponseDto<ProductResponse> editProduct(EditProductRequestDto req, Long chatId, UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }

        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        product.setNameUz(req.getNameUz());
        product.setNameRu(req.getNameRu());
        product.setNameCyr(req.getNameCyr());
        product.setNameEn(req.getNameEn());
        product.setDescriptionUz(req.getDescriptionUz());
        product.setDescriptionCyr(req.getDescriptionCyr());
        product.setDescriptionRu(req.getDescriptionRu());
        product.setDescriptionEn(req.getDescriptionEn());
        product.setUpdatedAt(LocalDateTime.now());
        return ResponseDto.success(productMapper.toResponse(productRepository.save(product)));
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public ResponseDto<ProductResponse> removeDiscount(UUID productId, Long chatId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }

        if (product.getDiscount() == null) return ResponseDto.error(ErrorCode.NOT_FOUND_DISCOUNT);
        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        discountRepository.delete(product.getDiscount());
        product.setDiscount(null);
        product = productRepository.save(product);
        return ResponseDto.success(productMapper.toResponse(product));
    }

    @Override
    @Transactional
    public ResponseDto<ProductResponse> addDiscount(AddDiscountRequestDto req, UUID productId, Long chatId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }

        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        if (product.getDiscount() == null) {
            Discount discount = new Discount();
            discount.setValue(req.getValue());
            discount.setProduct(product);
            discount.setType(req.getType());
            discount.setAppliedTo(AppliedTo.PRODUCT);
            discount.setCreatedAt(LocalDateTime.now());
            discount.setUpdatedAt(LocalDateTime.now());
            discount.setSubscriptionPlanId(null);
            discount = discountRepository.save(discount);
            product.setDiscount(discount);
            product = productRepository.save(product);
        } else {
            return ResponseDto.error(ErrorCode.ALREADY_DISCOUNT_EXISTS, productMapper.toResponse(product));
        }
        return ResponseDto.success(productMapper.toResponse(product));

    }

    @Override
    @Transactional
    public ResponseDto<Void> deleteProduct(UUID productId, Long chatId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }
        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
        return ResponseDto.success();
    }

    @Override
    @Transactional
    public ResponseDto<ProductResponse> changeDiscount(ChangeDiscountRequestDto req, UUID productId, Long chatId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }

        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        if (product.getDiscount() == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_DISCOUNT);
        }
        Discount discount = product.getDiscount();
        discount.setValue(req.getValue());
        discount.setType(req.getType());
        discount.setAppliedTo(AppliedTo.PRODUCT);
        discount.setUpdatedAt(LocalDateTime.now());
        discount = discountRepository.save(discount);
        product.setDiscount(discount);
        product = productRepository.save(product);
        return ResponseDto.success(productMapper.toResponse(product));
    }

    @Override
    @Transactional
    public ResponseDto<ProductResponse> addType(AddProductTypeRequestDto req, UUID productId, Long chatId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }

        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        ProductType type = new ProductType();
        type.setNameUz(req.getNameUz());
        type.setNameRu(req.getNameRu());
        type.setNameEn(req.getNameEn());
        type.setNameCyr(req.getNameCyr());
        type.setPrice(req.getPrice());
        type.setStock(req.getStock());
        type.setDeleted(false);
        type.setProduct(product);
        type = productTypeRepository.save(type);
        List<ProductTypeImage> images = new ArrayList<>();
        int mainCount = 0;
        for (AddProductTypeImageRequestDto img : req.getImages()) {
            ProductTypeImage image = new ProductTypeImage();
            image.setImageUrl(img.getImageUrl());
            image.setImgName(img.getImgName());
            image.setImgSize(img.getImgSize());
            if (img.getMain()) mainCount++;
            image.setMain(mainCount == 1 && img.getMain());
            image.setProductType(type);
            image.setCreatedAt(LocalDateTime.now());
            image.setUpdatedAt(LocalDateTime.now());
            image.setDeleted(false);
            image = productTypeImageRepository.save(image);
            images.add(image);
        }
        type.setImages(images);
        productTypeRepository.save(type);
        return ResponseDto.success(productMapper.toResponse(productRepository.findById(productId).orElse(null)));
    }

    @Transactional
    @Override
    public ResponseDto<ProductResponse> editType(EditProductTypeRequestDto req, UUID typeId, UUID productId, Long chatId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }

        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        ProductType productType = productTypeRepository.findById(typeId).orElse(null);
        if (productType == null) return ResponseDto.error(ErrorCode.TYPE_NOT_FOUND);
        if (productType.getDeleted()) return ResponseDto.error(ErrorCode.TYPE_NOT_FOUND);
        productType.setNameUz(req.getNameUz());
        productType.setNameRu(req.getNameRu());
        productType.setNameEn(req.getNameEn());
        productType.setNameCyr(req.getNameCyr());
        productType.setPrice(req.getPrice());
        productType.setUpdatedAt(LocalDateTime.now());
        productType.setStock(req.getStock());
        productType = productTypeRepository.save(productType);
        return ResponseDto.success(productMapper.toResponse(productRepository.findById(productId).orElse(null)));
    }

    @Transactional
    @Override
    public ResponseDto<Void> deleteType(UUID typeId, UUID productId, Long chatId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        ProductType productType = productTypeRepository.findById(typeId).orElse(null);
        if (productType == null) return ResponseDto.error(ErrorCode.TYPE_NOT_FOUND);
        if (productType.getDeleted()) return ResponseDto.error(ErrorCode.TYPE_NOT_FOUND);
        productType.setDeleted(true);
        productTypeRepository.save(productType);
        return ResponseDto.success();
    }

    @Override
    @Transactional
    public ResponseDto<ProductResponse> addTypeImage(AddProductTypeImageRequestDto req, UUID productId, Long chatId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }

        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        ProductType productType = productTypeRepository.findById(req.getProductTypeId()).orElse(null);
        if (productType == null) return ResponseDto.error(ErrorCode.TYPE_NOT_FOUND);
        if (productType.getDeleted()) return ResponseDto.error(ErrorCode.TYPE_NOT_FOUND);
        ProductTypeImage image = new ProductTypeImage();
        image.setImageUrl(req.getImageUrl());
        image.setImgSize(req.getImgSize());
        image.setImgName(req.getImgName());
        if (req.getMain()) {
            for (ProductTypeImage productTypeImage : productType.getImages()) {
                if (productTypeImage.getMain()) {
                    productTypeImage.setMain(false);
                    productTypeImageRepository.save(productTypeImage);
                }
            }
        }
        image.setMain(req.getMain());
        image.setUpdatedAt(LocalDateTime.now());
        image.setCreatedAt(LocalDateTime.now());
        image.setProductType(productType);
        image.setDeleted(false);
        productTypeImageRepository.save(image);

        productType.setUpdatedAt(LocalDateTime.now());
        productTypeRepository.save(productType);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        return ResponseDto.success(productMapper.toResponse(productRepository.findById(productId).orElse(null)));
    }

    @Transactional
    @Override
    public ResponseDto<ProductResponse> editTypeImage(EditProductTypeImageRequestDto req, UUID productId, Long chatId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }

        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        ProductType productType = productTypeRepository.findById(req.getProductTypeId()).orElse(null);
        if (productType == null) return ResponseDto.error(ErrorCode.TYPE_NOT_FOUND);
        if (productType.getDeleted()) return ResponseDto.error(ErrorCode.TYPE_NOT_FOUND);
        ProductTypeImage image = productTypeImageRepository.findById(req.getImageId()).orElse(null);
        if (image == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_IMAGE);
        }
        if (image.getDeleted()) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_IMAGE);
        }
        try {

            String fileUrl = new File("uploads/" + image.getImageUrl().substring(image.getImageUrl().lastIndexOf('/') + 1)).getAbsolutePath();
            boolean deleted = deleteFile(fileUrl);
        } catch (Exception ignored) {

        }
        image.setImageUrl(req.getImageUrl());
        image.setImgSize(req.getImgSize());
        image.setImgName(req.getImgName());
        if (req.getMain()) {
            for (ProductTypeImage productTypeImage : productType.getImages()) {
                if (productTypeImage.getMain()) {
                    productTypeImage.setMain(false);
                    productTypeImageRepository.save(productTypeImage);
                }
            }
        }
        image.setMain(req.getMain());
        image.setUpdatedAt(LocalDateTime.now());
        productTypeImageRepository.save(image);
        productType.setUpdatedAt(LocalDateTime.now());
        productTypeRepository.save(productType);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        return ResponseDto.success(productMapper.toResponse(productRepository.findById(productId).orElse(null)));

    }

    @Override
    @Transactional
    public ResponseDto<Void> deleteTypeImage(UUID imageId, UUID typeId, UUID productId, Long chatId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        if (seller.getStatus() == SellerStatus.PENDING || seller.getStatus() == SellerStatus.REJECTED) {
            return new ResponseDto<>(false, new ResponseDto.Message(seller.getStatus().getDescriptionUz(), seller.getStatus().getDescriptionUzCyrillic(), seller.getStatus().getDescriptionRu(), seller.getStatus().getDescriptionEn()), ErrorCode.ERROR, null);
        }

        if (product.getStatus() == ProductStatus.DELETED) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        ProductType productType = productTypeRepository.findById(typeId).orElse(null);
        if (productType == null) return ResponseDto.error(ErrorCode.TYPE_NOT_FOUND);
        if (productType.getDeleted()) return ResponseDto.error(ErrorCode.TYPE_NOT_FOUND);
        ProductTypeImage image = productTypeImageRepository.findById(imageId).orElse(null);
        if (image == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_IMAGE);
        }
        if (image.getDeleted()) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_IMAGE);
        }
        try {
            String fileUrl = new File("uploads/" + image.getImageUrl().substring(image.getImageUrl().lastIndexOf('/') + 1)).getAbsolutePath();
            boolean deleted = deleteFile(fileUrl);
        } catch (Exception ignored) {

        }
        image.setDeleted(true);
        productTypeImageRepository.save(image);
        productType.setUpdatedAt(LocalDateTime.now());
        productTypeRepository.save(productType);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        return ResponseDto.success();
    }

    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);

            if (Files.exists(path)) {
                Files.delete(path);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResponseDto<Page<ProductResponse>> list(int page, int size) {
        Page<Product> products = productRepository.forAdminProducts(PageRequest.of(page, size));
        Page<ProductResponse> productResponses = productMapper.toPage(products);
        return ResponseDto.success(productResponses);
    }

    @Override
    public ResponseDto<Page<ProductResponse>> list(int page, int size, ProductStatus status) {
        Page<Product> products = productRepository.forAdminProducts(
                PageRequest.of(page, size), status
        );
        Page<ProductResponse> productResponses = productMapper.toPage(products);
        return ResponseDto.success(productResponses);
    }

    @Override
    public ResponseDto<ProductResponse> changeStatus(UUID productId, ProductStatus status) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        product.setStatus(status);
        return ResponseDto.success(productMapper.toResponse(productRepository.save(product)));
    }

    @Override
    public ResponseDto<Page<ProductResponse>> search(String query, ProductStatus status, int page, int size) {
        Page<Product> products = productRepository.search(
                PageRequest.of(page, size), query, status
        );
        Page<ProductResponse> productResponses = productMapper.toPage(products);
        return ResponseDto.success(productResponses);
    }

    @Override
    public ResponseDto<Page<ProductResponse>> search(String query, int page, int size) {
        Page<Product> products = productRepository.search(
                PageRequest.of(page, size), query
        );
        Page<ProductResponse> productResponses = productMapper.toPage(products);
        return ResponseDto.success(productResponses);
    }

    @Override
    public ResponseDto<List<ProductResponse>> search(String query) {
        return ResponseDto.success(
                productMapper.toList(
                        productRepository.search(query, ProductStatus.OPEN)
                )
        );
    }

    @Override
    public ResponseDto<List<ProductResponse>> findAllByCategoryId(UUID categoryId) {
        if (categoryRepository.findById(categoryId).isEmpty()) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_CATEGORY);
        }
        return ResponseDto.success(
                productMapper.toList(
                        productRepository.findAllByCategoryId(categoryId)
                )
        );
    }
}
