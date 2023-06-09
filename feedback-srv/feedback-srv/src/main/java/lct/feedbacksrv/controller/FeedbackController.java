package lct.feedbacksrv.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lct.feedbacksrv.apiModels.MessageUI;
import lct.feedbacksrv.domain.Message;
import lct.feedbacksrv.resource.ErrorsList;
import lct.feedbacksrv.resource.Paginator;
import lct.feedbacksrv.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.*;

/**
 * Feedback controller
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Slf4j
@RestController
@RequestMapping("/feedback")
@Api(value = "Сообщения",
description = "Методы для работы с отзывами")
public class FeedbackController extends MainController{
    private static final String LAYOUT = "feedback";

    public FeedbackController() {
        super(LAYOUT);
    }

    @Autowired
    FeedbackService feedbackService;
    @Autowired
    PostamatService postamatService;
    @Autowired
    PartnerService partnerService;
    @Autowired
    CsvReaderService csvReaderService;

    @GetMapping
    @ApiIgnore
    public ModelAndView getMessagesListUIWithoutPages() {
        return postPagesAndGetMessagesListUI(0,0);
    }
    @PostMapping
    @ApiIgnore
    public ModelAndView postPagesAndGetMessagesListUI(Integer page, Integer pageSize) {
        log.info("Get messages  list page {}, pageSize {}", page, pageSize);

        Map<String, Object> data = getHeaderMap();
        data.put("content", "messageslist");
        data.put("subcats", feedbackService.getCategoriesForUi());

        try {
            Long partnersCount = feedbackService.getCount();
            if(page > 0) page--;
            pageSize = pageSize==0 ? 30 : pageSize;
            int from = page * pageSize;

            Paginator.PaginatorBuilder paginator = Paginator.builder();
            paginator.pageCount(Paginator.calculatePageCount(partnersCount.intValue(), pageSize));
            if(partnersCount.intValue() < from) {
                data.put("messages", feedbackService.getMessagesByLimitAndOffset(pageSize, 0));
                paginator.currentPage(1);
            } else {
                data.put("messages", feedbackService.getMessagesByLimitAndOffset(pageSize, from));
                paginator.currentPage(page+1);
            }
            paginator.pageSize(pageSize);
            data.put("paginator", paginator.build());

        } catch (Exception e) {
            log.info("Exception on getting messages list method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);
    }

    @GetMapping(path = "/api/page")
    @Operation(
            summary = "Получить список сообщений постранично",
            description = "Возвращает страницу с сообщениями"
    )
    public ResponseEntity getMessagesPage(Integer page, Integer pageSize) {
        log.info("Get messages  list page {}, pageSize {}", page, pageSize);

        Map<String, Object> data = new HashMap<>();

        try {
            Long partnersCount = feedbackService.getCount();
            if(page > 0) page--;
            pageSize = pageSize==0 ? 30 : pageSize;
            int from = page * pageSize;

            Paginator.PaginatorBuilder paginator = Paginator.builder();
            paginator.pageCount(Paginator.calculatePageCount(partnersCount.intValue(), pageSize));
            if(partnersCount.intValue() < from) {
                data.put("messages", feedbackService.getMessagesByLimitAndOffset(pageSize, 0));
                paginator.currentPage(1);
            } else {
                data.put("messages", feedbackService.getMessagesByLimitAndOffset(pageSize, from));
                paginator.currentPage(page+1);
            }
            paginator.pageSize(pageSize);
            data.put("paginator", paginator.build());

        } catch (Exception e) {
            log.info("Exception on getting messages list method", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }

        return ResponseEntity.ok(data);
    }

    @GetMapping(path = {"/info/{id}"})
    @ApiIgnore
    public ModelAndView getOneMessageUI(@PathVariable("id") Long id) {
        log.info("Get message {}", id);

        Map<String, Object> data = getHeaderMap();
        data.put("content", "messageslist");
        data.put("subcats", feedbackService.getCategoriesForUi());

        try {
            Long partnersCount = feedbackService.getCount();
            int page = 0;
            int pageSize = 30;
            Optional<Message> m = feedbackService.getMessage(id);
            List<Message> singleMessage = new ArrayList<>();
            if(m.isEmpty()) {
                singleMessage.add(Message.builder()
                                .id(id)
                                .message("Сообщение не найдено")
                        .build());
            } else {
                singleMessage.add(m.get());
            }
            data.put("messages", singleMessage);

            Paginator.PaginatorBuilder paginator = Paginator.builder();
            paginator.pageCount(Paginator.calculatePageCount(partnersCount.intValue(), pageSize));
            paginator.currentPage(1);

            paginator.pageSize(pageSize);
            data.put("paginator", paginator.build());

        } catch (Exception e) {
            log.info("Exception on getting message method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);
    }

    @Operation(
            summary = "Получить список сообщений",
            description = "Возвращает все имеющиеся в системе сообщения"
    )
    @GetMapping(path={"/all"})
    public ResponseEntity getMessagesList(String login, String password) {
        try{
            return ResponseEntity.ok(feedbackService.getAllMessages());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @Operation(
            summary = "Получить сообщение по идентификатору",
            description = "Возвращает сообщение"
    )
    @GetMapping(path={"/message"})
    public ResponseEntity getMessage(String login, String password, Long messageId) {
        Optional<Message> message = feedbackService.getMessage(messageId);
        if(message.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(path={"/add"})
    @ApiIgnore
    public ModelAndView getAddPageUI() {
        log.info("Get add message page");

        Map<String, Object> data = getHeaderMap();
        data.put("content", "edit");

        try {
            data.put("message", Message.builder().build());
            data.put("postamats", postamatService.getAllPostamatIds());
            data.put("partners", partnerService.getPartnersIdAndNames());

        } catch (Exception e) {
            log.error("Exception on getting add message page method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);

    }

    @GetMapping(path={"/{id}/category"})
    @ApiIgnore
    public ModelAndView getEditCategoryPageUI(@PathVariable("id") Long id) {
        log.info("Get edit category page");

        Map<String, Object> data = getHeaderMap();
        data.put("content", "editcat");
        data.put("subcats", feedbackService.getCategoriesForUi());

        try {
            Optional<Message> m = feedbackService.getMessage(id);
            if(m.isEmpty()) return getOneMessageUI(id);

            data.put("message", m.get());

        } catch (Exception e) {
            log.error("Exception on getting add message page method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);

    }
    @Operation(
            summary = "Добавить сообщение",
            description = "Добавляет сообщение"
    )
    @PostMapping(path={"/message"})
    public ResponseEntity postMessage(String login,
                                      String password,
                                      Optional<String> username,
                                      Optional<String> createDate,
                                      Optional<String> postamat,
                                      Optional<String> orderId,
                                      Optional<String> message,
                                      Optional<Integer> stars,
                                      Optional<Long> partner
                                      ) {
        MessageUI messageUI = new MessageUI();
        username.ifPresent(messageUI::setUsername);
        createDate.ifPresent(messageUI::setCreateDate);
        postamat.ifPresent(messageUI::setPostamat);
        orderId.ifPresent(messageUI::setOrderId);
        message.ifPresent(messageUI::setMessage);
        stars.ifPresent(messageUI::setStars);
        partner.ifPresent(messageUI::setPartner);

        Message m = feedbackService.messageUItoMessage(messageUI);
        if(m != null) {
            return ResponseEntity.status(HttpStatus.OK).body(m);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path={"/import"})
    @ApiIgnore
    public ModelAndView getImportPageUI() {
        log.info("Get import messages page");

        Map<String, Object> data = getHeaderMap();
        data.put("content", "import");

        return render(data);

    }

    @PostMapping(path = {"/file"}, produces = "text/csv")
    @ResponseBody
    @Operation(
            summary = "Импортировать сообщения",
            description = "Добавляет множество сообщений в систему. Принимает CSV-файл с заголовком 'comment;createdate;stars;summ'\n" +
                    "где comment - текст отзыва, createdate - дата создания, stars - оценка от 1 до 5, summ - сумма заказа."
    )
    public ModelAndView importMessages(@RequestParam(name = "file") MultipartFile file) throws IOException {
        log.info("Create from file");

        if (file.isEmpty()) {
            String msg = "Messages import CSV file is empty";
            log.error(msg);
            Map<String, Object> data = getHeaderMap();
            data.put("content", "error");
            data.put("errorType", ErrorsList.EMPTY_FILE.getDescription());
            return render(data);
        }

        boolean isOk = csvReaderService.decodeMessages(file);
        if(isOk) {
            return getMessagesListUIWithoutPages();
        } else {
            Map<String, Object> data = getHeaderMap();
            data.put("content", "error");
            data.put("errorType", ErrorsList.ERROR_ON_PARSE_FILE.getDescription());
            return render(data);
        }

    }
    @ApiIgnore
    @PostMapping(path={"/{id}/editCategory"})
    public ModelAndView updCat(@PathVariable("id") Long id, Long newCategory, Boolean needTicket) {
        feedbackService.updCategory(id, newCategory, needTicket);
        return getOneMessageUI(id);
    }

    @GetMapping
    @ApiIgnore
    @RequestMapping( {"/gm/{key}"})
    public ModelAndView godModeResetStatuses(@PathVariable("key") String key) {
        if(key.equals("GODMODE")){
            log.info("GOD MODE: RESET WIP STATUS");
            feedbackService.initOnStart();
            log.info("END GOD MODE: RESET WIP STATUS");
        }
        return getMessagesListUIWithoutPages();
    }
}