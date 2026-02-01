package uz.zafar.onlineshoptelegrambot.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.common.request.AddDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.common.request.ChangeDiscountRequestDto;
import uz.zafar.onlineshoptelegrambot.dto.product.request.*;
import uz.zafar.onlineshoptelegrambot.dto.product.response.CategoryResponse;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ResponseDto<List<CategoryResponse>> allCategories();

    ResponseDto<CategoryResponse> findByCategoryId(UUID categoryId);

    ResponseDto<List<ProductResponse>> myProducts(Long chatId);

    ResponseDto<List<ProductResponse>> myProductsByCategoryId(Long chatId, UUID categoryId);

    ResponseDto<ProductResponse> findByProductId(UUID productId);

    ResponseDto<ProductResponse> addProduct(AddProductRequestDto req, Long chatId);

    ResponseDto<ProductResponse> editProduct(EditProductRequestDto req, Long chatId, UUID productId);

    ResponseDto<ProductResponse> removeDiscount(UUID productId, Long chatId);

    ResponseDto<ProductResponse> addDiscount(AddDiscountRequestDto req, UUID productId, Long chatId);

    ResponseDto<ProductResponse> changeDiscount(ChangeDiscountRequestDto req, UUID productId, Long chatId);

    ResponseDto<Void> deleteProduct(UUID productId, Long chatId);

    ResponseDto<ProductResponse> addType(AddProductTypeRequestDto req, UUID productId, Long chatId);

    ResponseDto<ProductResponse> editType(EditProductTypeRequestDto req, UUID typeId, UUID productId, Long chatId);

    ResponseDto<Void> deleteType(UUID typeId, UUID productId, Long chatId);

    ResponseDto<ProductResponse> addTypeImage(AddProductTypeImageRequestDto req, UUID productId, Long chatId);

    ResponseDto<ProductResponse> editTypeImage(EditProductTypeImageRequestDto req, UUID productId, Long chatId);

    ResponseDto<Void> deleteTypeImage(UUID imageId, UUID typeId, UUID productId, Long chatId);

    /// ////////////////////////admin role uchun
    ResponseDto<Page<ProductResponse>> list(int page, int size);

    ResponseDto<Page<ProductResponse>> list(int page, int size, ProductStatus status);

    ResponseDto<ProductResponse> changeStatus(UUID productId, ProductStatus status);

    ResponseDto<Page<ProductResponse>> search(String query, ProductStatus status, int page, int size);

    ResponseDto<Page<ProductResponse>> search(String query, int page, int size);
    ResponseDto<List<ProductResponse>> search(String query);
    /// ///////////////////customer
    ResponseDto<List<ProductResponse>>findAllByCategoryId(UUID c) ;
}
