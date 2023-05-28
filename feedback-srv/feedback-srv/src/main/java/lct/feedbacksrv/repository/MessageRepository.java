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
}
