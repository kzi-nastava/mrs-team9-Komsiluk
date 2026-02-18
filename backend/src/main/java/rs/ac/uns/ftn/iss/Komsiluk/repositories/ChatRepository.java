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

    List<Chat> findByConversationIdOrderBySentAtAsc(Long conversationId);
    @Query("SELECT cm FROM Chat cm WHERE cm.id IN " +
            "(SELECT MAX(c.id) FROM Chat c GROUP BY c.conversationId)")
    List<Chat> findLatestMessagesPerConversation();

    @Query("SELECT COUNT(c) FROM Chat c WHERE c.conversationId = :userId " +
            "AND c.receiver.role = 'ADMIN' AND c.isRead = false")
    long countUnreadMessages(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Chat c SET c.isRead = true WHERE c.conversationId = :userId ")
    void markAllAsRead(@Param("userId") Long userId);

}