package lct.feedbacksrv.controller;

import io.swagger.v3.oas.annotations.Operation;
import lct.feedbacksrv.resource.ErrorsList;
import lct.feedbacksrv.resource.Paginator;
import lct.feedbacksrv.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */

@Slf4j
@RestController
@RequestMapping("/ticket")
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

    @Operation(
            summary = "Получить список партнёров",
            description = "Возвращает список партнёров"
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
