package uz.zafar.onlineshoptelegrambot.rest.category;

import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.common.request.AddDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.common.request.ChangeDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.product.request.*;
import uz.zafar.onlineshoptelegrambot.service.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("api/seller/product")
public class CategoryRestController {
    private final ProductService productService;

    public CategoryRestController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("category/list")
    public ResponseDto<?> list() {
        return productService.allCategories();
    }

    @GetMapping("category/by-id/{category_id}")
    public ResponseDto<?> categoryById(@PathVariable("category_id") UUID categoryId) {
        return productService.findByCategoryId(categoryId);
    }

    @GetMapping("my-products/{chat_id}")
    public ResponseDto<?> myProducts(@PathVariable("chat_id") Long chatId) {
        return productService.myProducts(chatId);
    }

    @GetMapping("my-products-by-category-id/{category_id}/{chat_id}")
    public ResponseDto<?> myProductsByCategoryId(@PathVariable("category_id") UUID categoryId, @PathVariable("chat_id") Long chatId) {
        return productService.myProductsByCategoryId(chatId, categoryId);
    }

    @GetMapping("find-by-product-id/{product_id}")
    public ResponseDto<?> findByProductId(@PathVariable("product_id") UUID productId) {
        return productService.findByProductId(productId);
    }

    @PostMapping("add-product/{chat_id}")
    public ResponseDto<?> addProduct(@RequestBody AddProductRequestDto req, @PathVariable("chat_id") Long chatId) {
        return productService.addProduct(req, chatId);
    }

    @PutMapping("edit-product/{product_id}/{chat_id}")
    public ResponseDto<?> editProduct(@RequestBody EditProductRequestDto req, @PathVariable("chat_id") Long chatId, @PathVariable("product_id") UUID productId) {
        return productService.editProduct(req, chatId, productId);
    }

    @DeleteMapping("remove-discount-from-product/{product_id}/{chat_id}")
    public ResponseDto<?> removeDiscount(@PathVariable("chat_id") Long chatId, @PathVariable("product_id") UUID productId) {
        return productService.removeDiscount(productId, chatId);
    }

    @PostMapping("add-discount-to-product/{product_id}/{chat_id}")
    public ResponseDto<?> addDiscountToProduct(@PathVariable("product_id") UUID productId, @PathVariable("chat_id") Long chatId, @RequestBody AddDiscountRequestDto req) {
        return productService.addDiscount(req, productId, chatId);
    }

    @PutMapping("change-discount-product/{product_id}/{chat_id}")
    public ResponseDto<?> changeDiscountToProduct(@PathVariable("product_id") UUID productId, @PathVariable("chat_id") Long chatId, @RequestBody ChangeDiscountRequestDto req) {
        return productService.changeDiscount(req, productId, chatId);
    }

    @DeleteMapping("delete-product/{product_id}/{chat_id}")
    public ResponseDto<Void> deleteProduct(@PathVariable("product_id") UUID productId, @PathVariable("chat_id") Long chatId) {
        return productService.deleteProduct(productId, chatId);
    }
///  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    @PostMapping("add-type-to-product/{product_id}/{chat_id}")
    public ResponseDto<?> addTypeToProduct(@PathVariable("product_id") UUID productId,
                                           @RequestBody AddProductTypeRequestDto req,
                                           @PathVariable("chat_id") Long chatId) {
        return productService.addType(req, productId, chatId);
    }

    @PutMapping("edit-type/{product_id}/{type_id}/{chat_id}")
    public ResponseDto<?> editTypeToProduct(@PathVariable("product_id") UUID productId,
                                            @RequestBody EditProductTypeRequestDto req,
                                            @PathVariable("chat_id") Long chatId,
                                            @PathVariable("type_id") UUID typeId) {
        return productService.editType(req, typeId, productId, chatId);
    }

    @DeleteMapping("delete-type/{product_id}/{type_id}/{chat_id}")
    public ResponseDto<?> deleteTypeToProduct(@PathVariable("product_id") UUID productId,
                                              @PathVariable("chat_id") Long chatId,
                                              @PathVariable("type_id") UUID typeId) {
        return productService.deleteType(typeId, productId, chatId);
    }

    @PostMapping("add-image-to-product-type/{product_id}/{chat_id}")
    public ResponseDto<?> addImageToProductType(@PathVariable("product_id") UUID productId,
                                                @RequestBody AddProductTypeImageRequestDto req,
                                                @PathVariable("chat_id") Long chatId) {
        return productService.addTypeImage(req, productId, chatId);
    }

    @PutMapping("edit-type-image/{product_id}/{chat_id}")
    public ResponseDto<?> editImageToProductType(@PathVariable("product_id") UUID productId,
                                                 @RequestBody EditProductTypeImageRequestDto req,
                                                 @PathVariable("chat_id") Long chatId) {
        return productService.editTypeImage(req, productId, chatId);
    }

    @DeleteMapping("delete-type/{product_id}/{type_id}/{image_id}/{chat_id}")
    public ResponseDto<?> deleteImageToProductImage(@PathVariable("product_id") UUID productId,
                                                    @PathVariable("chat_id") Long chatId,
                                                    @PathVariable("type_id") UUID typeId,
                                                    @PathVariable("image_id") UUID imageId) {
        return productService.deleteTypeImage(imageId,typeId, productId, chatId);
    }

}
