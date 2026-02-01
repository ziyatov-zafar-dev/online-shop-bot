package uz.zafar.onlineshoptelegrambot.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.BotCustomer;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.comment.Comment;
import uz.zafar.onlineshoptelegrambot.db.repositories.CommentRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.bot.BotCustomerRepository;
import uz.zafar.onlineshoptelegrambot.db.repositories.category.ProductRepository;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;
import uz.zafar.onlineshoptelegrambot.dto.enums.ErrorCode;
import uz.zafar.onlineshoptelegrambot.service.CommentService;

import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {
    private final BotCustomerRepository botCustomerRepository;
    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(BotCustomerRepository botCustomerRepository, ProductRepository productRepository, CommentRepository commentRepository) {
        this.botCustomerRepository = botCustomerRepository;
        this.productRepository = productRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ResponseDto<Comment> addComment(String text, Long chatId, UUID productId) {
        BotCustomer botCustomer = botCustomerRepository.checkUser(chatId).orElse(null);
        if (botCustomer == null) return (ResponseDto.error(ErrorCode.NOT_FOUND_CUSTOMER));
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        Comment comment = new Comment();
        comment.setProduct(product);
        comment.setCustomer(botCustomer);
        comment.setText(text);
        return ResponseDto.success(commentRepository.save(comment));
    }

    @Override
    public ResponseDto<List<Comment>> getProductCommentaries(UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseDto.error(ErrorCode.NOT_FOUND_PRODUCT);
        return ResponseDto.success(commentRepository.findAllByProductId(productId));
    }
}
