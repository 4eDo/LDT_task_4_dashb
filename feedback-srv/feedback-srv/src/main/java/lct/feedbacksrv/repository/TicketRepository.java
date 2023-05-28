package lct.feedbacksrv.repository;

import lct.feedbacksrv.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * TODO: Add interface description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query(value="SELECT * FROM tickets ORDER BY id offset ?1 limit ?2", nativeQuery = true)
    List<Ticket> findPage(int offset, int limit);
}
