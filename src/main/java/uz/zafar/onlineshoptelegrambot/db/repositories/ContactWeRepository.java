package uz.zafar.onlineshoptelegrambot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.zafar.onlineshoptelegrambot.db.entity.contact.ContactWe;

import java.util.List;

public interface ContactWeRepository extends JpaRepository<ContactWe,Integer> {
    @Query("select c from ContactWe c where c.deleted=false")
    List<ContactWe>allContacts();
}
