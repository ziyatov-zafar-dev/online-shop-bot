package uz.zafar.onlineshoptelegrambot.service.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.shop.request.EditOrAddShopRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.shop.response.ShopResponse;
import uz.zafar.onlineshoptelegrambot.mapper.ShopMapper;
import uz.zafar.onlineshoptelegrambot.service.ShopService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ShopServiceImpl implements ShopService {
    private final BotSellerRepository botSellerRepository;
    private final SellerRepository sellerRepository;
    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;

    public ShopServiceImpl(BotSellerRepository botSellerRepository, SellerRepository sellerRepository, ShopRepository shopRepository, ShopMapper shopMapper) {
        this.botSellerRepository = botSellerRepository;
        this.sellerRepository = sellerRepository;
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
    }

    @Override
    public ResponseDto<ShopResponse> addShop(EditOrAddShopRequestDto req, Long chatId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        }
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);

        Shop shop = setShop(req, seller, new Shop());
        ShopResponse response = shopMapper.toResponse(shop);
        return ResponseDto.success(response);
    }


    @Override
    public ResponseDto<ShopResponse> editShop(EditOrAddShopRequestDto req, UUID shopId, Long chatId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        }
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        Shop shop = shopRepository.findById(shopId).orElse(null);
        if (shop == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SHOP);
        shop = setShop(req, seller, shop);
        ShopResponse response = shopMapper.toResponse(shop);
        return ResponseDto.success(response);
    }

    @NonNull
    private Shop setShop(EditOrAddShopRequestDto req, Seller seller, Shop shop) {
        shop.setNameUz(req.getNameUz().trim());
        shop.setNameCyr(req.getNameCyr().trim());
        shop.setNameEn(req.getNameEn().trim());
        shop.setNameRu(req.getNameRu().trim());
        shop.setDescriptionUz(req.getDescriptionUz().trim());
        shop.setDescriptionCyr(req.getDescriptionCyr().trim());
        shop.setDescriptionEn(req.getDescriptionEn().trim());
        shop.setDescriptionRu(req.getDescriptionRu().trim());
        shop.setLogoUrl(req.getLogoUrl());
        shop.setWorkStartTime(req.getWorkStartTime());
        shop.setWorkEndTime(req.getWorkEndTime());
        shop.setWorkDays(req.getWorkDays() != null ? req.getWorkDays() : List.of());
        shop.setPhone(req.getPhone().trim());
        shop.setEmail(req.getEmail().trim());
        shop.setTelegram(req.getTelegram().trim());
        shop.setInstagram(req.getInstagram().trim());
        shop.setFacebook(req.getFacebook().trim());
        shop.setWebsite(req.getWebsite().trim());
        shop.setHasPhone(req.getHasPhone() != null ? req.getHasPhone() : false);
        shop.setHasEmail(req.getHasEmail() != null ? req.getHasEmail() : false);
        shop.setHasTelegram(req.getHasTelegram() != null ? req.getHasTelegram() : false);
        shop.setHasInstagram(req.getHasInstagram() != null ? req.getHasInstagram() : false);
        shop.setHasFacebook(req.getHasFacebook() != null ? req.getHasFacebook() : false);
        shop.setHasWebsite(req.getHasWebsite() != null ? req.getHasWebsite() : false);
        shop.setHasLocation(req.getHasLocation() != null ? req.getHasLocation() : false);
        shop.setLatitude(req.getLatitude());
        shop.setLongitude(req.getLongitude());
        shop.setAddress(req.getAddress().trim());
        shop.setBrandUrl(req.getBrandUrl());
        shop.setDeliveryInfoUz(req.getDeliveryInfoUz());
        shop.setDeliveryInfoRu(req.getDeliveryInfoRu());
        shop.setDeliveryInfoEn(req.getDeliveryInfoEn());
        shop.setDeliveryInfoCyr(req.getDeliveryInfoCyr());
        shop.setDeliveryPrice(req.getDeliveryPrice() != null ? req.getDeliveryPrice() : BigDecimal.ZERO);
        shop.setHasDelivery(req.getHasDelivery() != null ? req.getHasDelivery() : false);
        shop.setDeliveryOutsidePrice(req.getDeliveryOutsidePrice() != null ? req.getDeliveryOutsidePrice() : BigDecimal.ZERO);
        shop.setHasOutsideDelivery(req.getHasOutsideDelivery() != null ? req.getHasOutsideDelivery() : false);
        shop.setSeller(seller);
        shop.setCreatedAt(LocalDateTime.now());
        shop.setUpdatedAt(LocalDateTime.now());
        return shopRepository.save(shop);

    }

    @Override
    public ResponseDto<Void> deleteShop(Long chatId, UUID shopId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        }
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        Shop shop = shopRepository.findById(shopId).orElse(null);
        if (shop == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SHOP);
        shop.setActive(null);
        shop = shopRepository.save(shop);
        return ResponseDto.success();
    }

    @Override
    public ResponseDto<List<ShopResponse>> myShops(Long chatId, boolean isProducts) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        }
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        }
        List<Shop> shops;
        if (isProducts)
            shops = shopRepository.findAllBySeller(seller, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                    .filter(Shop::getActive)
                    .toList();
        else shops = shopRepository.findAllBySeller(seller, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<ShopResponse> responses = shops.stream()
                .map(shopMapper::toResponse)
                .toList();
        return ResponseDto.success(responses);
    }

    @Override
    public ResponseDto<List<ShopResponse>> myActiveShops(Long chatId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        }
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        }
        List<Shop> shops = shopRepository.findAllBySellerAndActive(seller, true, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<ShopResponse> responseList = shops.stream()
                .map(shopMapper::toResponse)
                .toList();

        return ResponseDto.success(responseList);
    }

    @Override
    public ResponseDto<List<ShopResponse>> myNotActiveShops(Long chatId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER);
        }
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_SELLER);
        }
        List<Shop> shops = shopRepository.findAllBySellerAndActive(seller, false, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<ShopResponse> responseList = shops.stream()
                .map(shopMapper::toResponse)
                .toList();
        return ResponseDto.success(responseList);
    }

}
