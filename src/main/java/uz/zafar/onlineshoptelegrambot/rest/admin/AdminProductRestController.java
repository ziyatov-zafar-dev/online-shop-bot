package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductResponse;
import uz.zafar.onlineshoptelegrambot.service.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("api/admin/product")
public class AdminProductRestController {
    private final ProductService productService;

    public AdminProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("list")
    public ResponseDto<Page<ProductResponse>> list(@RequestParam("page") int page, @RequestParam("size") int size) {
        return productService.list(page, size);
    }


    @GetMapping("list/{status}")
    public ResponseDto<Page<ProductResponse>> list(
            @RequestParam("page") int page, @RequestParam("size") int size, @PathVariable("status") ProductStatus status
    ) {
        return productService.list(page, size, status);
    }

    @PutMapping("change-status/{productId}")
    public ResponseDto<ProductResponse> changeStatus(
            @PathVariable("productId") UUID productId, @RequestParam("status") ProductStatus status
    ) {
        return productService.changeStatus(productId, status);
    }

    @GetMapping("search/{status}")
    public ResponseDto<Page<ProductResponse>> search(
            @RequestParam("q") String query, @PathVariable("status") ProductStatus status, @RequestParam("page") int page, @RequestParam("size") int size
    ) {
        return productService.search(query, status, page, size);
    }

    @GetMapping("search")
    public ResponseDto<Page<ProductResponse>> search(
            @RequestParam("q") String query, @RequestParam("page") int page, @RequestParam("size") int size
    ) {
        return productService.search(query, page, size);
    }
}
