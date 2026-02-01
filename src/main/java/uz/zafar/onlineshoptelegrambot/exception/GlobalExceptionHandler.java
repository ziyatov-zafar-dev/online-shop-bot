package uz.zafar.onlineshoptelegrambot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleValidation(Exception ex) {

//        telegramBot.sendMessage(7882316826L, ex.getMessage(), "admin");
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "success", false,
                        "code", "VALIDATION_ERROR",
                        "message", "Yuborilgan maʼlumotlar noto‘g‘ri",
                        "error", ex.getMessage()
                )
        );
    }


}
