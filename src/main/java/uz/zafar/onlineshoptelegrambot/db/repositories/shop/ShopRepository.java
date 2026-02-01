package uz.zafar.onlineshoptelegrambot.db.repositories.shop;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.seller.Seller;
import uz.zafar.onlineshoptelegrambot.db.entity.shop.Shop;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, UUID> {
    @Query("SELECT s FROM Shop s WHERE s.seller = :seller AND s.active IS NOT NULL ORDER BY s.createdAt")
    List<Shop> findAllBySeller(@Param("seller") Seller seller, Sort sort);
    @Query("Select sh from Shop sh where sh.active=true and sh.seller.pkey=:sellerId order by sh.createdAt asc")
    List<Shop> findAllBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT s FROM Shop s WHERE s.seller = :seller AND s.active = :active ORDER BY s.createdAt")
    List<Shop> findAllBySellerAndActive(@Param("seller") Seller seller, @Param("active") Boolean active, Sort sort);

    @Query("""
            select sh from Shop sh
            inner join Seller s 
            on sh.seller.pkey=s.pkey
            where sh.active=true and sh.seller.pkey=:sellerId  and sh.active=true
            """)
    List<Shop> getShopByChatId(@Param("sellerId") Long sellerId);
    @Query("select sh from Shop sh where sh.active=true order by sh.createdAt asc")
    List<Shop>activeShops() ;

    @Query("select sh from Shop sh where sh.active=false order by sh.createdAt asc")
    List<Shop>notConfirmedShops() ;

    @Query("select sh from Shop sh where sh.active is null order by sh.createdAt asc")
    List<Shop>deletedShops() ;
}
