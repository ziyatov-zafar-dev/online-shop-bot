package uz.zafar.onlineshoptelegrambot.rest.shop;

import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.shop.request.EditOrAddShopRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.shop.response.ShopResponse;
import uz.zafar.onlineshoptelegrambot.service.ShopService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/seller/shop")
public class ShopRestController {
    private final ShopService shopService;

    public ShopRestController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("my-shops/{chat_id}")
    public ResponseDto<List<ShopResponse>> myShops(@PathVariable("chat_id") Long chatId) {
        return shopService.myShops(chatId,false);
    }

    @GetMapping("my-shops-1/{chat_id}")
    public ResponseDto<List<ShopResponse>> myShops1(@PathVariable("chat_id") Long chatId) {
        return shopService.myActiveShops(chatId);
    }

    @GetMapping("my-active-shops/{chat_id}")
    public ResponseDto<List<ShopResponse>> myActiveShops(@PathVariable("chat_id") Long chatId) {
        return shopService.myActiveShops(chatId);
    }

    @GetMapping("my-approval-shops/{chat_id}")
    public ResponseDto<List<ShopResponse>> approvalShops(@PathVariable("chat_id") Long chatId) {
        return shopService.myNotActiveShops(chatId);
    }

    @DeleteMapping("/delete-shop/{shop_id}/{chat_id}")
    public ResponseDto<Void> deleteShop(@PathVariable("shop_id") UUID shopId, @PathVariable("chat_id") Long chatId) {
        return shopService.deleteShop(chatId, shopId);
    }

    @PutMapping("/edit-shop/{shop_id}/{chat_id}")
    public ResponseDto<ShopResponse> editShop(@RequestBody EditOrAddShopRequestDto req, @PathVariable("shop_id") UUID shopId, @PathVariable("chat_id") Long chatId) {
        return shopService.editShop(req, shopId, chatId);
    }

    @PostMapping("/add-shop/{chat_id}")
    public ResponseDto<ShopResponse> addShop(@RequestBody EditOrAddShopRequestDto req, @PathVariable("chat_id") Long chatId) {
        return shopService.addShop(req, chatId);
    }
}
