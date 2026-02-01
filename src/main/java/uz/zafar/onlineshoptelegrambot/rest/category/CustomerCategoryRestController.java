package uz.zafar.onlineshoptelegrambot.rest.category;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.common.request.AddDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.common.request.ChangeDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.product.request.*;
import uz.zafar.onlineshoptelegrambot.service.CustomerService;
import uz.zafar.onlineshoptelegrambot.service.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("api/customer")
public class CustomerCategoryRestController {
    private final ProductService productService;
    private final CustomerService customerService;

    public CustomerCategoryRestController(ProductService productService, CustomerService customerService) {
        this.productService = productService;
        this.customerService = customerService;
    }

    @GetMapping("product/category/list")
    public ResponseEntity<?> getAllCategoriesWithProductsTree() {
        return ResponseEntity.ok(productService.allCategories());
    }


    @GetMapping("check/user/{chat_id}")
    public ResponseEntity<?> checkUser(@PathVariable("chat_id") Long chatId) {
        return ResponseEntity.ok(customerService.checkUser(chatId));
    }
}
