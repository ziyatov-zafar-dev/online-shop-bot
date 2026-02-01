package uz.zafar.onlineshoptelegrambot.service;

import uz.zafar.onlineshoptelegrambot.db.entity.comment.Comment;
import uz.zafar.onlineshoptelegrambot.dto.ResponseDto;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    ResponseDto<Comment> addComment(String text, Long chatId, UUID productId);
    ResponseDto<List<Comment>>getProductCommentaries(UUID productId);
}
