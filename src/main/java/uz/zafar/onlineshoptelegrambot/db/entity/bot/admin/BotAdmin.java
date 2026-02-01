//package uz.zafar.onlineshoptelegrambot.db.entity.bot.admin;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//
//import java.util.UUID;
//
//public class BotAdmin {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(columnDefinition = "uuid", name = "id")
//    private UUID pkey;
//    @Column(nullable = false, unique = true)
//    private Long chatId;
//    private String username;
//    private String firstName;
//    private String lastName;
//    private UUID userid;
//
//}
