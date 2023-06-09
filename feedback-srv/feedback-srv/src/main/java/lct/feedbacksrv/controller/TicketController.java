package lct.feedbacksrv.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lct.feedbacksrv.domain.Ticket;
import lct.feedbacksrv.resource.ErrorsList;
import lct.feedbacksrv.resource.Paginator;
import lct.feedbacksrv.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */

@Slf4j
@RestController
@RequestMapping("/ticket")
@Api(value = "Заявки/Тикеты",
        description = "Методы для управления тикетами")
public class TicketController extends MainController {
    private static final String LAYOUT = "ticket";
    @Autowired
    TicketService ticketService;

    public TicketController() {
        super(LAYOUT);
    }

    @GetMapping
    @ApiIgnore
    public ModelAndView getTicketsListUIWithoutPages() {
        return postPagesAndGetTicketsListUI(0,0);
    }
    @PostMapping
    @ApiIgnore
    public ModelAndView postPagesAndGetTicketsListUI(Integer page, Integer pageSize) {
        log.info("Get tickets  list page {}, pageSize {}", page, pageSize);

        Map<String, Object> data = getHeaderMap();
        data.put("content", "ticketslist");

        try {
            Long ticketsCount = ticketService.getCount();
            if(page > 0) page--;
            pageSize = pageSize==0 ? 30 : pageSize;
            int from = page * pageSize;

            Paginator.PaginatorBuilder paginator = Paginator.builder();
            paginator.pageCount(Paginator.calculatePageCount(ticketsCount.intValue(), pageSize));
            if(ticketsCount.intValue() < from) {
                data.put("tickets", ticketService.getTicketsByLimitAndOffset(pageSize, 0));
                paginator.currentPage(1);
            } else {
                data.put("tickets", ticketService.getTicketsByLimitAndOffset(pageSize, from));
                paginator.currentPage(page+1);
            }
            paginator.pageSize(pageSize);
            data.put("paginator", paginator.build());

        } catch (Exception e) {
            log.info("Exception on getting tickets list method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);
    }

    @GetMapping(path = {"/info/{id}"})
    @ApiIgnore
    public ModelAndView getOneTicketUI(@PathVariable("id") Long id) {
        log.info("Get ticket {}", id);

        Map<String, Object> data = getHeaderMap();
        data.put("content", "ticketslist");

        try {
            Long partnersCount = ticketService.getCount();
            int page = 0;
            int pageSize = 30;
            Optional<Ticket> t = ticketService.findTicket(id);
            List<Ticket> singleTicket = new ArrayList<>();
            if(t.isEmpty()) {
                singleTicket.add(Ticket.builder()
                        .id(id)
                        .comment("Сообщение не найдено")
                        .build());
            } else {
                singleTicket.add(t.get());
            }
            data.put("tickets", singleTicket);

            Paginator.PaginatorBuilder paginator = Paginator.builder();
            paginator.pageCount(Paginator.calculatePageCount(partnersCount.intValue(), pageSize));
            paginator.currentPage(1);

            paginator.pageSize(pageSize);
            data.put("paginator", paginator.build());

        } catch (Exception e) {
            log.info("Exception on getting singleTicket method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);
    }

    @GetMapping(path = {"/close/{id}"})
    @ApiIgnore
    public ModelAndView closeTicketUI(@PathVariable("id") Long id) {
        log.info("Close ticket {}", id);

        try {
            Optional<Ticket> t = ticketService.findTicket(id);
            List<Ticket> singleTicket = new ArrayList<>();
            if(t.isEmpty()) {
                log.info("Ticket {} not found", id);
            } else {
                Boolean isClosed = ticketService.closeTicket(t.get());
                if(!isClosed) log.info("Can't closeticket {}", id);
            }

        } catch (Exception e) {
            log.info("Exception on close ticket method", e);
        }

        return getTicketsListUIWithoutPages();
    }

    @GetMapping(path={"/comment/{id}"})
    @ApiIgnore
    public ModelAndView getEditPageUI(@PathVariable("id") Long id) {
        log.info("Edit ticket page");

        Map<String, Object> data = getHeaderMap();
        data.put("content", "edit");

        try {
            Optional<Ticket> ticket = ticketService.findTicket(id);
            if(ticket.isPresent()) {
                data.put("ticket", ticket.get());
                data.put("content", "editComment");
            } else {
                log.info("Ticket {} not found", id);
                data.put("content", "error");
                data.put("errorType", ErrorsList.TICKET_NOT_FOUND.getDescription());
            }

        } catch (Exception e) {
            log.error("Exception on getting edit ticket page method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);

    }
    @Operation(
            summary = "Изменить комментарий к тикету",
            description = "Позволяет изменить комментарий"
    )
    @PostMapping(path={"/edit/{id}"})
    public ModelAndView editPartner(@PathVariable("id") Long id, String comment) {
        try{
            Optional<Ticket> ticket = ticketService.findTicket(id);
            if(ticket.isPresent()) {
                Ticket t = ticket.get();
                t.setComment(comment);
                ticketService.addTicket(t);
            } else {
                log.info("Ticket {} not found", id);
            }

            return getOneTicketUI(id);
        } catch (Exception e) {
            return getTicketsListUIWithoutPages();
        }
    }

    @Operation(
            summary = "Получить список тикетов",
            description = "Возвращает список тикетов"
    )
    @GetMapping(path={"/all"})
    public ResponseEntity getTicketsList(String login, String password) {
        try{
            return ResponseEntity.ok(ticketService.getAllTickets());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }
}
