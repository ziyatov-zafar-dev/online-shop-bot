package uz.zafar.onlineshoptelegrambot.rest.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.comment.Comment;
import uz.zafar.onlineshoptelegrambot.db.repositories.CommentRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductResponse;
import uz.zafar.onlineshoptelegrambot.rest.seller.SellerCommentaryRestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/customer/comment")
public class CustomerCommentRestController {
    private final CommentRepository commentRepository;
    private final BotCustomerRepository botCustomerRepository;
    private final ProductRepository productRepository;

    public CustomerCommentRestController(CommentRepository commentRepository, BotCustomerRepository botCustomerRepository, ProductRepository productRepository) {
        this.commentRepository = commentRepository;
        this.botCustomerRepository = botCustomerRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("list/{productId}")
    public ResponseDto<?> comments(@PathVariable("productId") UUID pid,
                                   @RequestParam("chat_id") Long chatId) {
        List<CommentResponse> list = new ArrayList<>();
        for (Comment c : commentRepository.findAllByProductId(pid)) {
            list.add(new CommentResponse(
                    c.getPkey(),
                    c.getText(),
                    c.getCustomer(),
                    null,
                    c.getCreatedAt(),
                    c.getCustomer().getChatId().equals(chatId)
            ));
        }
        return ResponseDto.success(list);
    }


    @PostMapping("add-comment/{productId}")
    public ResponseDto<?> comments(@PathVariable("productId") UUID pid,
                                   @RequestParam("chat_id") Long chatId,
                                   @RequestParam("text") String text
    ) {
        Comment comment = new Comment();
        comment.setText(text);
        BotCustomer botCustomer = botCustomerRepository.checkUser(chatId).orElse(null);
        if (botCustomer == null) return ResponseDto.error(ErrorCode.ERROR);
        comment.setCustomer(botCustomer);
        Product product = productRepository.findById(pid).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        comment.setProduct(product);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setDeleted(false);
        commentRepository.save(comment);
        return ResponseDto.success();
    }

    public static class CommentResponse {
        private UUID id;
        private String text;
        private BotCustomer customer;
        @JsonIgnore
        private ProductResponse product;
        private LocalDateTime createdAt;
        private boolean me;

        public CommentResponse(UUID id, String text, BotCustomer customer, ProductResponse product, LocalDateTime createdAt, boolean me) {
            this.id = id;
            this.text = text;
            this.customer = customer;
            this.product = product;
            this.createdAt = createdAt;
            this.me = me;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public BotCustomer getCustomer() {
            return customer;
        }

        public void setCustomer(BotCustomer customer) {
            this.customer = customer;
        }

        public ProductResponse getProduct() {
            return product;
        }

        public void setProduct(ProductResponse product) {
            this.product = product;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public boolean isMe() {
            return me;
        }

        public void setMe(boolean me) {
            this.me = me;
        }
    }
}
