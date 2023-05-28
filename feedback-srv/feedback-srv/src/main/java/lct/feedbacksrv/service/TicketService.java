package lct.feedbacksrv.service;

import lct.feedbacksrv.domain.Message;
import lct.feedbacksrv.domain.Ticket;

import java.util.List;
import java.util.Optional;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface TicketService {
    Optional<Ticket> findTicket(Long id);
    Ticket addTicket(Ticket ticket);
    Ticket editComment(Ticket ticket, String comment);
    Boolean closeTicket(Ticket ticket);
    List<Ticket> getAllTickets();
    List<Ticket> getTicketsByLimitAndOffset(int limit, int offset);
    Long getCount();

    void createTicket(Message m);
}
