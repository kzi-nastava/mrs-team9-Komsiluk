package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Chat;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    // Pronalazi sve poruke koje pripadaju konverzaciji određenog korisnika
    // Sortirano hronološki (najstarije prve, da se lepo ispišu u chatu)
    List<Chat> findByConversationIdOrderBySentAtAsc(Long conversationId);
    // 1. POPRAVLJEN UPIT ZA INBOX (Nalazi najnoviju poruku po konverzaciji)
    @Query("SELECT cm FROM Chat cm WHERE cm.id IN " +
            "(SELECT MAX(c.id) FROM Chat c GROUP BY c.conversationId)")
    List<Chat> findLatestMessagesPerConversation();

    // 2. NOVI UPIT: Broji nepročitane poruke od konkretnog korisnika ka Adminu
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.conversationId = :userId " +
            "AND c.receiver.role = 'ADMIN' AND c.isRead = false")
    long countUnreadMessages(@Param("userId") Long userId);

    // 3. NOVI UPIT: Markiraj sve poruke kao pročitane kad admin otvori chat
    @Modifying
    @Transactional
    @Query("UPDATE Chat c SET c.isRead = true WHERE c.conversationId = :userId ")
    void markAllAsRead(@Param("userId") Long userId);

}