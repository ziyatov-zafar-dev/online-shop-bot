package uz.zafar.onlineshoptelegrambot.db.repositories.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Category;
import uz.zafar.onlineshoptelegrambot.db.entity.category.Product;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("select p from Product  p order by p.createdAt")
    List<Product> getAllProducts();

    @Query("""
                select p
                from Product p
                    join p.shop sh
                    join sh.seller s
                where s.pkey = :sellerId
                  and p.status in (:statuses)
                order by p.createdAt desc
            """)
    List<Product> myProducts(
            @Param("sellerId") Long sellerId,
            @Param("statuses") List<ProductStatus> statuses
    );

    @Query("""
                select p
                from Product p
                    join p.shop sh
                    join sh.seller s
                where s.pkey = :sellerId
                  and p.status in (:statuses)
                  and p.category.pkey=:categoryId
            """)
    List<Product> myProductsByCategoryId(
            @Param("sellerId") Long sellerId,
            @Param("statuses") List<ProductStatus> statuses,
            @Param("categoryId") UUID categoryId
    );

    @Query("""
            select p from Product p
            inner join Shop sh on sh.pkey=p.shop.pkey
            inner join Seller s on s.pkey=sh.seller.pkey
            where s.pkey=:sellerId and
            p.status=uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus.OPEN
            and sh.active=true order by p.createdAt asc
            """)
    List<Product> findAllProductsBySeller(@Param("sellerId") Long sellerId);

    //    @Query("select p from Product p where p.category.pkey=:cid and p.status=uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus.OPEN order by p.createdAt desc")
    @Query("""
                select p
                from Product p
                where p.category.pkey = :cid
                  and p.status = uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus.OPEN
                  and p.shop.active is true
                  and p.shop.seller.planExpiresAt >= CURRENT_TIMESTAMP
                  and p.shop.seller.status = 'APPROVED'
                order by p.createdAt desc
            """)
    List<Product> findAllByCategoryId(@Param("cid") UUID categoryId);

    @Query("""
            select p from Product p where p.category.pkey=:cid and
            p.status=uz.zafar.onlineshoptelegrambot.db.entity.enums.ProductStatus.OPEN
            order by p.createdAt asc """)
    List<Product> findAllSellerProductByCategoryId(@Param("cid") UUID pkey);

    /// ///////////////admins

    @Query("""
            select p from Product p order by p.createdAt asc""")
    Page<Product> forAdminProducts(Pageable pageable);

    @Query("""
            select p from Product p where p.status=:status order by p.createdAt asc""")
    Page<Product> forAdminProducts(Pageable pageable, @Param("status") ProductStatus status);


    @Query("""
                SELECT DISTINCT p
                FROM Product p
                    JOIN p.category c
                    JOIN p.shop s
                    JOIN s.seller sel
                WHERE s.active = true
                  AND (
                        /* ================= PRODUCT ================= */
                        LOWER(p.nameUz)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.nameRu)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.nameEn)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.nameCyr)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(p.descriptionUz) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.descriptionRu) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.descriptionEn) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.descriptionCyr)LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(p.sku)           LIKE LOWER(CONCAT('%', :q, '%'))
            
                        /* ================= CATEGORY ================= */
                     OR LOWER(c.nameUz)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(c.nameRu)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(c.nameEn)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(c.nameCyr)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                        /* ================= SHOP ================= */
                     OR LOWER(s.nameUz)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.nameRu)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.nameEn)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.nameCyr)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(s.phone)         LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.email)         LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.telegram)      LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.instagram)     LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.facebook)      LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.website)       LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.address)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                        /* ================= SELLER ================= */
                     OR LOWER(sel.phone)       LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(sel.cardOwner)   LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(sel.cardNumber)  LIKE LOWER(CONCAT('%', :q, '%'))
                  )
            """)
    Page<Product> search(Pageable pageable, @Param("q") String q);

    @Query("""
                SELECT DISTINCT p
                FROM Product p
                    JOIN p.category c
                    JOIN p.shop s
                    JOIN s.seller sel
                WHERE s.active = true and p.status=:status and p.shop.active=true and p.category.active=true
                  AND (
                        LOWER(p.nameUz)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.nameRu)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.nameEn)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.nameCyr)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(p.descriptionUz) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.descriptionRu) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.descriptionEn) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.descriptionCyr)LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(p.sku)           LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(c.nameUz)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(c.nameRu)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(c.nameEn)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(c.nameCyr)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(s.nameUz)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.nameRu)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.nameEn)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.nameCyr)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(s.phone)         LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.email)         LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.telegram)      LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.instagram)     LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.facebook)      LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.website)       LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.address)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(sel.phone)       LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(sel.cardOwner)   LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(sel.cardNumber)  LIKE LOWER(CONCAT('%', :q, '%'))
                  )
            """)
    List<Product> search(@Param("q") String q, @Param("status") ProductStatus status);

    @Query("""
                SELECT DISTINCT p
                FROM Product p
                    JOIN p.category c
                    JOIN p.shop s
                    JOIN s.seller sel
                WHERE p.status = :status
                  AND s.active = true
                  AND (
                        LOWER(p.nameUz)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.nameRu)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.nameEn)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.nameCyr)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(p.descriptionUz) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.descriptionRu) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.descriptionEn) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(p.descriptionCyr)LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(p.sku)           LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(c.nameUz)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(c.nameRu)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(c.nameEn)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(c.nameCyr)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(s.nameUz)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.nameRu)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.nameEn)        LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.nameCyr)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(s.phone)         LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.email)         LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.telegram)      LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.instagram)     LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.facebook)      LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.website)       LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(s.address)       LIKE LOWER(CONCAT('%', :q, '%'))
            
                     OR LOWER(sel.phone)       LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(sel.cardOwner)   LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(sel.cardNumber)  LIKE LOWER(CONCAT('%', :q, '%'))
                  )
""")
    Page<Product> search(Pageable pageable, @Param("q") String q, @Param("status") ProductStatus status);
}
