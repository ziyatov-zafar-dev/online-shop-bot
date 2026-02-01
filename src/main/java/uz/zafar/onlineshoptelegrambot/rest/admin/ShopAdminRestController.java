package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;
import uz.zafar.onlineshoptelegrambot.db.repositories.shop.ShopRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.mapper.ShopMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/admin/shop")
public class ShopAdminRestController {
    private final ShopRepository shopRepository;

    private final ShopMapper shopMapper;

    public ShopAdminRestController(ShopRepository shopRepository, ShopMapper shopMapper) {
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
    }

    @GetMapping("active-shops")
    public ResponseDto<?> activeShops() {
        return ResponseDto.success(shopMapper.toList(shopRepository.activeShops()));
    }


    @GetMapping("not-confirmed-shops")
    public ResponseDto<?> notConfirmedShops() {
        return ResponseDto.success(shopMapper.toList(shopRepository.notConfirmedShops()));
    }


    @GetMapping("deleted-shops")
    public ResponseDto<?> deletedShops() {
        return ResponseDto.success(shopMapper.toList(shopRepository.deletedShops()));
    }

    @GetMapping("list")
    public ResponseDto<?> list() {
        return ResponseDto.success(shopMapper.toList(shopRepository.findAll()));
    }

    @PostMapping("confirm-shop/{shopId}")
    @Transactional
    public ResponseDto<?> confirmShop(@PathVariable("shopId") UUID shopId) {
        Shop shop = shopRepository.findById(shopId).orElse(null);
        if (shop == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SHOP);
        shop.setActive(true);
        return ResponseDto.success(shopRepository.save(shop));
    }

    @PostMapping("delete-shop/{shopId}")
    @Transactional
    public ResponseDto<?> deletedShop(@PathVariable("shopId") UUID shopId) {
        Shop shop = shopRepository.findById(shopId).orElse(null);
        if (shop == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SHOP);
        shop.setActive(null);
        return ResponseDto.success(shopRepository.save(shop));
    }

    @PostMapping("restore-shops/{shopId}")
    @Transactional
    public ResponseDto<?> restore(@PathVariable("shopId") UUID shopId) {
        Shop shop = shopRepository.findById(shopId).orElse(null);
        if (shop == null) return ResponseDto.error(ErrorCode.NOT_FOUND_SHOP);
        shop.setActive(false);
        return ResponseDto.success(shopRepository.save(shop));
    }
}
