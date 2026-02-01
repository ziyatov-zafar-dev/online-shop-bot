package uz.zafar.onlineshoptelegrambot.rest.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.zafar.onlineshoptelegrambot.service.OrderService;

@RestController
@RequestMapping("api/admin/order")
public class AdminOrderRestController {
    private final OrderService orderService;

    public AdminOrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping("list")
    public ResponseEntity<?>orders(){
        return ResponseEntity.ok(orderService.allOrders());
    }
}
