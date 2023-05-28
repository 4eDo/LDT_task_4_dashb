package lct.feedbacksrv.service.impl;

import lct.feedbacksrv.domain.Message;
import lct.feedbacksrv.domain.Ticket;
import lct.feedbacksrv.repository.TicketRepository;
import lct.feedbacksrv.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketServiceImpl implements TicketService {
    @Autowired
    TicketRepository ticketRepository;

    @Override
    public Optional<Ticket> findTicket(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Ticket addTicket(Ticket ticket) {
        try{
            return ticketRepository.saveAndFlush(ticket);
        } catch (Exception e) {
            log.error("Error in addTicket method", e);
        }
        return null;
    }

    @Override
    public Ticket editComment(Ticket ticket, String comment) {
        try{
            ticket.setComment(comment);
            return ticketRepository.saveAndFlush(ticket);
        } catch (Exception e) {
            log.error("Error in editComment method", e);
        }
        return null;
    }

    @Override
    public Boolean closeTicket(Ticket ticket) {
        try{
            ticket.setIsClosed(true);
            ticketRepository.saveAndFlush(ticket);
            return true;
        } catch (Exception e) {
            log.error("Error in closeTicket method", e);
        }
        return false;
    }

    @Override
    public List<Ticket> getAllTickets() {
        try{
            return ticketRepository.findAll();
        } catch (Exception e) {
            log.error("Error in getAllTickets method", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Ticket> getTicketsByLimitAndOffset(int limit, int offset) {
        try{
            return ticketRepository.findPage(offset, limit);
        } catch (Exception e) {
            log.error("Error in getTicketsByLimitAndOffset method", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Long getCount() {
        return ticketRepository.count();
    }

    @Override
    public void createTicket(Message m) {
        if(m.getTone() < 2) return;
        Ticket.TicketBuilder ticket = Ticket.builder();
        ArrayList<Long> messages = new ArrayList<>();
        messages.add(m.getId());
        ticket.messages(messages);
        ticket.partner(m.getPartner());
        ticket.createDate(new Date());
        ticket.category(m.getCategory());
        ticketRepository.saveAndFlush(ticket.build());
    }
}
