package uz.zafar.onlineshoptelegrambot.service;

import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.shop.request.EditOrAddShopRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.shop.response.ShopResponse;

import java.util.List;
import java.util.UUID;

public interface ShopService {
    ResponseDto<ShopResponse> addShop(EditOrAddShopRequestDto req, Long chatId);

    ResponseDto<ShopResponse> editShop(EditOrAddShopRequestDto req, UUID shopId,Long chatId);

    ResponseDto<Void> deleteShop(Long chatId, UUID shopId);

    ResponseDto<List<ShopResponse>> myShops(Long chatId,boolean isProduct);

    ResponseDto<List<ShopResponse>> myActiveShops(Long chatId);

    ResponseDto<List<ShopResponse>> myNotActiveShops(Long chatId);
}
