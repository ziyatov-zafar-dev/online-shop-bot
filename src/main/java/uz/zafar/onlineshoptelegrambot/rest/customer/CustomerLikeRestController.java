package uz.zafar.onlineshoptelegrambot.rest.customer;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.like.Like;
import uz.zafar.onlineshoptelegrambot.db.repositories.LikeRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.rest.admin.AdminProductLikeRestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer/like")
public class CustomerLikeRestController {
    private final LikeRepository likeRepository;
    private final BotCustomerRepository botCustomerRepository;
    private final ProductRepository productRepository;

    public CustomerLikeRestController(LikeRepository likeRepository, BotCustomerRepository botCustomerRepository, ProductRepository productRepository) {
        this.likeRepository = likeRepository;
        this.botCustomerRepository = botCustomerRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("is-like/{productId}")
    public ResponseDto<Boolean> isLike(@PathVariable("productId") UUID pid,
                                       @RequestParam("chat_id") Long chatId) {
        BotCustomer customer = botCustomerRepository.checkUser(chatId).orElse(null);
        if (customer == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER);
        }

        Product product = productRepository.findById(pid).orElse(null);
        if (product == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }
        Like like = likeRepository.isLike(product.getPkey(), customer.getPkey()).orElse(null);
        return like == null ? ResponseDto.error(ErrorCode.ERROR) : ResponseDto.success();
    }



    @GetMapping("count/{productId}")
    public ResponseDto<LikeCount> getLikeCount(@PathVariable("productId") UUID pid) {
        List<Like> list = likeRepository.findAllByProductId(pid);
        LikeCount likeCount = new LikeCount();
//        likeCount.setLikeCount(100021);
        likeCount.setLikeCount(list.size());
        likeCount.setLikes(list);
        return ResponseDto.success(likeCount);
    }
    @PutMapping("/handle-like/{productId}")
    @Transactional
    public ResponseDto<?> handleLike(
            @PathVariable("productId") UUID pid,
            @RequestParam("chat_id") Long chatId
    ) {
        BotCustomer customer = botCustomerRepository.checkUser(chatId).orElse(null);
        if (customer == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER);
        }

        Product product = productRepository.findById(pid).orElse(null);
        if (product == null) {
            return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        }

        Like like = likeRepository.isLike(product.getPkey(), customer.getPkey()).orElse(null);
        HandleLikeResponse res = new HandleLikeResponse();
        if (like == null) {
            like = new Like();
            like.setProduct(product);
            like.setCustomer(customer);
            likeRepository.save(like);
            res.setHandle(LikeHandle.LIKED);
        } else {
            likeRepository.delete(like);
            res.setHandle(LikeHandle.REMOVED);
        }
        return ResponseDto.success(res);
    }

    public enum LikeHandle {
        REMOVED, LIKED
    }

    public static class HandleLikeResponse {
        private LikeHandle handle;

        public LikeHandle getHandle() {
            return handle;
        }

        public void setHandle(LikeHandle handle) {
            this.handle = handle;
        }
    }

    public static class LikeCount {
        private long likeCount;
        private List<Like> likes;

        public long getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(long likeCount) {
            this.likeCount = likeCount;
        }

        public List<Like> getLikes() {
            return likes;
        }

        public void setLikes(List<Like> likes) {
            this.likes = likes;
        }
    }
}
