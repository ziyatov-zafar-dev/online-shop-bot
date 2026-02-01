package uz.zafar.onlineshoptelegrambot.service;

import uz.zafar.onlineshoptelegrambot.db.entity.order.ShopOrder;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateShopOrderRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.order.response.ShopOrderResponse;

import java.util.List;

public interface OrderService {
    ResponseDto<List<ShopOrderResponse>>createOrder(List<CreateShopOrderRequestDto> request);
    ResponseDto<List<ShopOrderResponse>>myClientOrders(Long chatId);
    ResponseDto<List<ShopOrderResponse>>allOrders();
}
