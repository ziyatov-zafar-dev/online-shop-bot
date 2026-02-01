package uz.zafar.onlineshoptelegrambot.rest.comment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.service.CommentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/customer/product/comment")
public class CommentRestController {
    private final CommentService commentService;
    private final BotCustomerRepository botCustomerRepository;

    public CommentRestController(CommentService commentService, BotCustomerRepository botCustomerRepository) {
        this.commentService = commentService;
        this.botCustomerRepository = botCustomerRepository;
    }

    @GetMapping("product-commentaries")
    public ResponseEntity<?> all(@RequestParam("productId") UUID pid) {
        return ResponseEntity.ok(commentService.getProductCommentaries(pid));
    }

    @PostMapping("add-comment")
    public ResponseEntity<?> addComment(@RequestBody AddComment comment) {

        return ResponseEntity.ok(commentService.addComment(
                comment.getText(), comment.getChatId(), comment.getProductId()
        ));
    }

    public static class AddComment {
        private String text;
        private Long chatId;
        private UUID productId;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Long getChatId() {
            return chatId;
        }

        public void setChatId(Long chatId) {
            this.chatId = chatId;
        }

        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
        }
    }
}
