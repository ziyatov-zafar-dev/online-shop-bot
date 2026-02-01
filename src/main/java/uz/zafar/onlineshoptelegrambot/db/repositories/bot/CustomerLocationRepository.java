package uz.zafar.onlineshoptelegrambot.db.repositories.bot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.bot.customer.CustomerLocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerLocationRepository extends JpaRepository<CustomerLocation, UUID> {
    @Query(value = """
            select l from CustomerLocation l 
                        where l.customerId=:customer_id and l.deleted=false
                                    order by l.created desc
            """)
    List<CustomerLocation> myLocation(@Param("customer_id") UUID pkey);
    @Query("select l from CustomerLocation l where l.address=:address and l.customerId=:customer_id and l.deleted=false")
    Optional<CustomerLocation>locationByAddress(@Param("address") String address,@Param("customer_id") UUID customerid);
}
