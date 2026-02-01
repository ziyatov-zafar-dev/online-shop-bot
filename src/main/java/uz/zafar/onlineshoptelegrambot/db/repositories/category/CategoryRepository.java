package uz.zafar.onlineshoptelegrambot.db.repositories.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("select c from Category c where c.active=true order by c.orderNumber asc")
    List<Category> getAllCategories();

    @Query("select c from Category c where c.active is true and c.parent is null order by c.orderNumber asc")
    List<Category> getParentCategories();


    Optional<Category> findBySlug(String android);

    @Query("""
                select c from Category c
                where lower(c.nameUz)  = lower(:text)
                   or lower(c.nameCyr) = lower(:text)
                   or lower(c.nameRu)  = lower(:text)
                   or lower(c.nameEn)  = lower(:text)
            """)
    Optional<Category> findByName(@Param("text") String text);

    @Query("""
                select c
                from Category c
                where c.parent.pkey = :parentId
                  and c.active = true
                order by c.orderNumber asc
            """)
    List<Category> findActiveChildrenByParentId(@Param("parentId") UUID parentId);

    @Query("""
                select c
                from Category c
                where c.active = true
                  and c.children is empty
                order by c.orderNumber asc
            """)
    List<Category> findAllLeafCategories();

}
