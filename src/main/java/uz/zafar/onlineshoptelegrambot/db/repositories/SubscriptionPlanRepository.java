package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.zafar.onlineshoptelegrambot.db.entity.common.SubscriptionPlan;
import uz.zafar.onlineshoptelegrambot.db.entity.enums.SubscriptionPlanType;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    @Query("select s from SubscriptionPlan s where s.name=:name")
    Optional<SubscriptionPlan> findByName(@Param("name") SubscriptionPlanType name);

    @Query("select s from SubscriptionPlan s where s.name in :planNames")
    List<SubscriptionPlan> getViewPlans(List<SubscriptionPlanType> planNames);
}
