package lct.feedbacksrv.repository;

import lct.feedbacksrv.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * TODO: Add interface description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value="SELECT * FROM messages ORDER BY id offset ?1 limit ?2", nativeQuery = true)
    List<Message> findPage(int offset, int limit);

    @Query(value="SELECT * FROM messages WHERE status = ?1 ORDER BY id limit ?2", nativeQuery = true)
    List<Message> findMessagesByStatusWithLimit(String status, int limit);

    @Query(value="SELECT * FROM messages WHERE status = ?1 ORDER BY id", nativeQuery = true)
    List<Message> findMessagesByStatus(String status);

    @Query(value="SELECT * FROM messages WHERE NOT (status IS NOT NULL);", nativeQuery = true)
    List<Message> findMessagesWithoutStatus();

    @Query(value="SELECT count(*) FROM messages WHERE postamat = ?1", nativeQuery = true)
    Long findMessagesCountByPostamat(String postamatId);

    @Query(value="SELECT count(*) FROM messages WHERE postamat = ?1 AND category IN (SELECT id FROM categories WHERE archetype = 'PM_BUG')", nativeQuery = true)
    Long findMessagesCountWithPostamatBug(String postamatId);

    @Query(value="SELECT count(*) FROM messages WHERE partner = ?1", nativeQuery = true)
    Long findMessagesCountByPartner(String partnerName);

    @Query(value="SELECT count(*) FROM messages WHERE partner = ?1 AND category IN (SELECT id FROM categories WHERE archetype = 'PARTNER_BUG')", nativeQuery = true)
    Long findMessagesCountWithPartnerBug(String partnerName);
}
