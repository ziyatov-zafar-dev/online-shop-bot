package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.payment.Payment;
import uz.zafar.onlineshoptelegrambot.dto.payment.PaymentResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    @Query("select p from Payment p order by p.createdAt desc ")
    List<Payment> findAll();

    @Query("select p from Payment p where p.transactionId=:id")
    Optional<Payment> findByTransactionId(@Param("id") String transactionID);

    @Query("select p from Payment p where p.seller.pkey=:pkey order by p.createdAt desc")
    List<Payment> myPayments(@Param("pkey") Long pkey);
}
