package lct.feedbacksrv.service.impl;

import lct.feedbacksrv.domain.Message;
import lct.feedbacksrv.domain.Ticket;
import lct.feedbacksrv.repository.CategoryRepository;
import lct.feedbacksrv.repository.MessageRepository;
import lct.feedbacksrv.repository.TicketRepository;
import lct.feedbacksrv.resource.AsyncParameters;
import lct.feedbacksrv.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketServiceImpl implements TicketService {
    @Value("${feedback.statuses.recheck.sec}")
    private int recheckSec;

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    CategoryRepository categoryRepository;

    private final Runtime runtime = Runtime.getRuntime();
    private final AsyncParameters asyncParameters;
    private final ThreadPoolExecutor executorService;
    private final ScheduledExecutorService scheduledExecutorService;


    @PostConstruct
    private void postConstruct() {
        checkStatuses();
        checkNeeded();
    }


    private void checkStatuses() {
        try {
            int activeTasks = executorService.getActiveCount();
            int maxTasks = asyncParameters.getPoolWorkerSize();
            int reserved = 1; // Зарезервировано для потока, сохраняющего файл в бд!
            int needed = maxTasks - activeTasks - reserved;
            int chunkSize = 1000;
            float free = calculate(runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory()));
            boolean hasFreeMem = free > 60;

            if (needed > 0 && hasFreeMem) {
                List<Message> toCreateTickets = messageRepository.findMessagesByStatusWithLimit("TONE_CHECKED", chunkSize);
                log.info("*** [To create tickets] Got messages count: {}, chunkSize: {}", toCreateTickets.size(), chunkSize);
                toCreateTickets.forEach(message -> message.setStatus("CREATE_TICKET"));
                messageRepository.saveAllAndFlush(toCreateTickets);
                executorService.submit(() -> createTickets(toCreateTickets));
            }
        } catch (Exception e) {
            log.error("3 Exception on check messages status", e);
        }
        scheduledExecutorService.schedule(this::checkStatuses, recheckSec, TimeUnit.SECONDS);
    }
    private void checkNeeded() {
        try {
            int activeTasks = executorService.getActiveCount();
            int maxTasks = asyncParameters.getPoolWorkerSize();
            int reserved = 1; // Зарезервировано для потока, сохраняющего файл в бд!
            int needed = maxTasks - activeTasks - reserved;
            int chunkSize = 1000;
            float free = calculate(runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory()));
            boolean hasFreeMem = free > 60;

            if (needed > 0 && hasFreeMem) {
                List<Message> toCreateTickets = messageRepository.findMessagesByStatusWithLimit("NEED_TICKET", chunkSize);
                log.info("*** [Need create tickets] Got messages count: {}, chunkSize: {}", toCreateTickets.size(), chunkSize);
                toCreateTickets.forEach(message -> message.setStatus("CREATE_NEED_TICKET"));
                messageRepository.saveAllAndFlush(toCreateTickets);
                executorService.submit(() -> createNeedTickets(toCreateTickets));
            }
        } catch (Exception e) {
            log.error("3 Exception on check messages status", e);
        }
        scheduledExecutorService.schedule(this::checkNeeded, recheckSec, TimeUnit.SECONDS);
    }

    private float calculate(long targetMemory) {
        return (float) targetMemory / (float) runtime.maxMemory() * 100;
    }


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
        try {
            m.setStatus("WITHOUT_TICKET");
            if(m.hasTone()){
                if(m.getTone() < 2) {
                    messageRepository.saveAndFlush(m);
                    return;
                }
                Ticket.TicketBuilder ticket = Ticket.builder();
                ticket.messages(m.getId().toString());
                ticket.partner(m.getPartner());
                ticket.createDate(new Date());
                ticket.category(m.getCategory());
                ticket.subcat(m.getSubcat());
                Ticket t = ticketRepository.saveAndFlush(ticket.build());
                log.info("Отзыв не положительный; Тикет №{} создан", t.getId());
                m.addTicket(t.getId().toString());
                m.setStatus("TICKET_CREATED");
            } else {
                if(m.getStars() > 3) {
                    m.setTone(1f);
                } else if(m.getStars() <3) {
                    m.setTone(3f);
                } else {
                    m.setTone(2f);
                }

                if(m.getCategory() == null) {
                    m.setCategory(categoryRepository.findByCode("NOT_BUG").get(0));
                }
            }
            messageRepository.saveAndFlush(m);
        } catch (Exception e) {
            log.error("cant create ticket for message {}", m, e);
        }

    }

    private void createNeedTickets(List<Message> toCreateTickets) {
        toCreateTickets.forEach(m -> {
            try {
                    Ticket.TicketBuilder ticket = Ticket.builder();
                    ticket.messages(m.getId().toString());
                    ticket.partner(m.getPartner());
                    ticket.createDate(new Date());
                    ticket.category(m.getCategory());
                    ticket.subcat(m.getSubcat());
                    Ticket t = ticketRepository.saveAndFlush(ticket.build());
                    log.info("ПРИНУДИТЕЛЬНО Тикет №{} создан", t.getId());
                    m.addTicket(t.getId().toString());
                    m.setStatus("TICKET_CREATED");
                messageRepository.saveAndFlush(m);
            } catch (Exception e) {
                log.error("cant create ticket for message {}", m, e);
            }
        });
    }

    public void createTickets(List<Message> messages){
        messages.forEach(this::createTicket);
    }
}
