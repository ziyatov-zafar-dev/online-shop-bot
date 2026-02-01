package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.VerificationType;
import uz.zafar.onlineshoptelegrambot.db.entity.user.User;
import uz.zafar.onlineshoptelegrambot.db.entity.user.VerificationCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {
    Optional<VerificationCode> findTopByUserAndTypeAndUsedIsFalseOrderByCreatedAtDesc(User user, VerificationType type);

    Optional<VerificationCode> findByUserAndTypeAndCodeAndUsedIsFalse(User user, VerificationType type, String code);

    @Query("select v from VerificationCode v order by v.createdAt desc")
    Page<VerificationCode> findAllVerificationCodes(Pageable pageable);

    @Query("select v from VerificationCode v where v.user.pkey=:id")
    List<VerificationCode> findAllByUserId(@Param("id") UUID pkey);
}
