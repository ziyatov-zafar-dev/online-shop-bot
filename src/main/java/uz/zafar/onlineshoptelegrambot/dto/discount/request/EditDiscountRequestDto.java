package uz.zafar.onlineshoptelegrambot.dto.discount.request;

import uz.zafar.onlineshoptelegrambot.db.entity.enums.DiscountType;

import java.math.BigDecimal;

public class EditDiscountRequestDto {
    private DiscountType type;
    private BigDecimal value;

    public DiscountType getType() {
        return type;
    }

    public void setType(DiscountType type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
