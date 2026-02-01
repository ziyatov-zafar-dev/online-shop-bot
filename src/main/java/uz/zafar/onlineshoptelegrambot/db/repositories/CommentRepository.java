package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.comment.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query("select c from Comment c where c.product.pkey=:pid and c.deleted=false order by c.createdAt desc")
    List<Comment> findAllByProductId(@Param("pid") UUID productId);

    @Query("""
                select c
                from Comment c
                join c.product p
                join p.shop s
                where s.seller.pkey = :sellerId
                  and c.deleted = false
                order by c.createdAt desc
            """)
    List<Comment> sellerCommentaries(@Param("sellerId") Long sellerId);

}
