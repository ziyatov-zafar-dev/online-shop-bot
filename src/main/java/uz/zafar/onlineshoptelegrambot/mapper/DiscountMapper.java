package uz.zafar.onlineshoptelegrambot.mapper;

import org.springframework.stereotype.Component;
import uz.zafar.onlineshoptelegrambot.db.entity.common.Discount;
import uz.zafar.onlineshoptelegrambot.dto.common.DiscountResponse;

@Component
public class DiscountMapper {
    public DiscountResponse toResponse(Discount discount) {
        DiscountResponse response = new DiscountResponse();
        response.setId(discount.getPkey());
        response.setType(discount.getType());
        response.setAppliedTo(discount.getAppliedTo());
        response.setValue(discount.getValue());
        response.setCreatedAt(discount.getCreatedAt());
        response.setUpdatedAt(discount.getUpdatedAt());
        response.setProductId(discount.getProduct() == null ? null : discount.getProduct().getPkey());
        response.setSubscriptionPlanId(discount.getSubscriptionPlanId());
        return response;
    }
}
