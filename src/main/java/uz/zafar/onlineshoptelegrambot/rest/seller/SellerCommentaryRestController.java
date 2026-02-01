package uz.zafar.onlineshoptelegrambot.rest.seller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.seller.BotSeller;
import uz.zafar.onlineshoptelegrambot.db.entity.comment.Comment;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.repositories.CommentRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.SellerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotSellerRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.dto.product.response.ProductResponse;
import uz.zafar.onlineshoptelegrambot.mapper.ProductMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/seller/commentary")
public class SellerCommentaryRestController {
    private final BotSellerRepository botSellerRepository;
    private final SellerRepository sellerRepository;
    private final CommentRepository commentRepository;
    private final ProductMapper productMapper;

    public SellerCommentaryRestController(BotSellerRepository botSellerRepository, SellerRepository sellerRepository, CommentRepository commentRepository, ProductMapper productMapper) {
        this.botSellerRepository = botSellerRepository;
        this.sellerRepository = sellerRepository;
        this.commentRepository = commentRepository;
        this.productMapper = productMapper;
    }

    @GetMapping("list/{chatId}")
    public ResponseEntity<?> allComments(@PathVariable("chatId") Long chatId) {
        BotSeller botSeller = botSellerRepository.checkUser(chatId).orElse(null);
        if (botSeller == null) return ResponseEntity.ok(
                ResponseDto.error(ErrorCode.NOT_FOUND_BOT_SELLER)
        );
        Seller seller = sellerRepository.findByUserid(botSeller.getUserid()).orElse(null);
        if (seller == null) return ResponseEntity.ok(
                ResponseDto.error(ErrorCode.NOT_FOUND_SELLER)
        );
        List<Comment> comments = commentRepository.sellerCommentaries(seller.getPkey());
        List<CommentResponse> response = new ArrayList<>();
        for (Comment comment : comments) {
            BotCustomer customer = comment.getCustomer();
            customer.setTelegramPhone("");
            customer.setHelperPhone("");
            response.add(new CommentResponse(
                    comment.getPkey(),
                    comment.getText(),
                    customer,
                    productMapper.toResponse(comment.getProduct()),
                    comment.getCreatedAt()
            ));
        }
        return ResponseEntity.ok(response);
    }

    public static class CommentResponse {
        private UUID id;
        private String text;
        private BotCustomer customer;
        private ProductResponse product;
        private LocalDateTime createdAt;

        public CommentResponse(UUID id, String text, BotCustomer customer, ProductResponse product, LocalDateTime createdAt) {
            this.id = id;
            this.text = text;
            this.customer = customer;
            this.product = product;
            this.createdAt = createdAt;
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
    }
}
