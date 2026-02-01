package uz.zafar.onlineshoptelegrambot.rest.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.zafar.onlineshoptelegrambot.dto.order.request.CreateShopOrderRequestDto;
import uz.zafar.onlineshoptelegrambot.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("api/customer/order")
public class OrderRestController {
    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("crate-order")
    public ResponseEntity<?> createOrder(@RequestBody List<CreateShopOrderRequestDto> orders) {
        return ResponseEntity.ok(orderService.createOrder(orders));
    }

    @GetMapping("/my-orders/{chatId}")
    public ResponseEntity<?> myOrders(@PathVariable("chatId") Long chatId) {
        return ResponseEntity.ok(orderService.myClientOrders(chatId));
    }
}
