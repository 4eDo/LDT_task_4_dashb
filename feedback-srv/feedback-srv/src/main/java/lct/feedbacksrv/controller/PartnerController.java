package lct.feedbacksrv.controller;

import io.swagger.v3.oas.annotations.Operation;
import lct.feedbacksrv.domain.Partner;
import lct.feedbacksrv.resource.ErrorsList;
import lct.feedbacksrv.resource.Paginator;
import lct.feedbacksrv.service.PartnerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

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
@RequestMapping("/partner")
public class PartnerController extends MainController {
    private static final String LAYOUT = "partner";
    @Autowired
    PartnerService partnerService;

    public PartnerController() {
        super(LAYOUT);
    }

    @GetMapping
    @ApiIgnore
    public ModelAndView getPartnersListUIWithoutPages() {
        return postPagesAndGetPartnersListUI(0,0);
    }
    @PostMapping
    @ApiIgnore
    public ModelAndView postPagesAndGetPartnersListUI(Integer page, Integer pageSize) {
        log.info("Get partners  list page {}, pageSize {}", page, pageSize);

        Map<String, Object> data = getHeaderMap();
        data.put("content", "partnerslist");

        try {
            Long partnersCount = partnerService.getCount();
            if(page > 0) page--;
            pageSize = pageSize==0 ? 30 : pageSize;
            int from = page * pageSize;

            Paginator.PaginatorBuilder paginator = Paginator.builder();
            paginator.pageCount(Paginator.calculatePageCount(partnersCount.intValue(), pageSize));
            if(partnersCount.intValue() < from) {
                data.put("partners", partnerService.getPartnersByLimitAndOffset(pageSize, 0));
                paginator.currentPage(1);
            } else {
                data.put("partners", partnerService.getPartnersByLimitAndOffset(pageSize, from));
                paginator.currentPage(page+1);
            }
            paginator.pageSize(pageSize);
            data.put("paginator", paginator.build());

        } catch (Exception e) {
            log.info("Exception on getting partners list method", e);
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
    public ResponseEntity getPartnersList(String login, String password) {
        try{
            return ResponseEntity.ok(partnerService.getPartners());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping(path={"/add"})
    @ApiIgnore
    public ModelAndView getAddPageUI() {
        log.info("Get add partner page");

        Map<String, Object> data = getHeaderMap();
        data.put("content", "edit");
        data.put("action", "add");

        try {
            data.put("partner", Partner.builder().build());

        } catch (Exception e) {
            log.error("Exception on getting add partner page method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);

    }
    @Operation(
            summary = "Добавить партнёра",
            description = "Позволяет добавить партнёра в систему"
    )
    @PostMapping(path={"/add"})
    public ResponseEntity addPartner(String login,
                                     String password,
                                     String name,
                                     String contacts,
                                     String delegate,
                                     String description
    ) {
        try{
            Partner.PartnerBuilder partner = Partner.builder();
            if(StringUtils.isNotBlank(name))
                if(!partnerService.findByName(name).isEmpty()) {
                    return ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body(String.format("Партнёр %s уже существует", name));
                }
                partner.name(name);
            if(StringUtils.isNotBlank(contacts))
                partner.contacts(contacts);
            if(StringUtils.isNotBlank(delegate))
                partner.delegate(delegate);
            if(StringUtils.isNotBlank(description))
                partner.description(description);
            return ResponseEntity.ok(partnerService.addPartner(partner.build()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping(path={"/edit/{id}"})
    @ApiIgnore
    public ModelAndView getEditPageUI(@PathVariable("id") Long id) {
        log.info("Edit postamat page");

        Map<String, Object> data = getHeaderMap();
        data.put("content", "edit");
        data.put("action", "edit");

        try {
            Optional<Partner> partner = partnerService.findById(id);
            if(partner.isPresent()) {
                data.put("partner", partner.get());
            } else {
                log.info("Partner {} not found", id);
                data.put("content", "error");
                data.put("errorType", ErrorsList.PARTNER_NOT_FOUND.getDescription());
            }

        } catch (Exception e) {
            log.error("Exception on getting edit partner page method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);

    }
    @Operation(
            summary = "Изменить партнёра",
            description = "Позволяет изменить информацию о партнёре"
    )
    @PostMapping(path={"/edit"})
    public ResponseEntity editPartner(String login,
                                     String password,
                                     Long id,
                                     String name,
                                     String contacts,
                                     String delegate,
                                     String description
    ) {
        try{
            if(partnerService.getPartnerById(id).isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(String.format("Партнёр %d не найден", id));
            }

            Partner.PartnerBuilder partner = Partner.builder();
            if(StringUtils.isNotBlank(name))
                partner.name(name);
            if(StringUtils.isNotBlank(contacts))
                partner.contacts(contacts);
            if(StringUtils.isNotBlank(delegate))
                partner.delegate(delegate);
            if(StringUtils.isNotBlank(description))
                partner.description(description);
            partner.id(id);

            return ResponseEntity.ok(partnerService.addPartner(partner.build()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @Operation(
            summary = "Удалить партнёра",
            description = "Позволяет удалить партнёра из системы"
    )
    @DeleteMapping(path={"/delete"})
    public ResponseEntity deletePartner(String login,
                                     String password,
                                     Long id
    ) {
        try{
            Optional<Partner> partner = partnerService.getPartnerById(id);
            if(partner.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(String.format("Партнёр %d не найден", id));
            }
            if(partnerService.deletePartner(partner.get())) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @Operation(
            summary = "Удалить партнёра (по имени)",
            description = "Позволяет удалить партнёра из системы"
    )
    @DeleteMapping(path={"/delete/byname"})
    public ResponseEntity deletePartnerByName(String login,
                                        String password,
                                        String name
    ) {
        try{
            List<Partner> partner = partnerService.findByName(name);
            if(partner.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(String.format("Партнёр %s не найден", name));
            }
            if(partnerService.deletePartner(partner.get(0))) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

}
