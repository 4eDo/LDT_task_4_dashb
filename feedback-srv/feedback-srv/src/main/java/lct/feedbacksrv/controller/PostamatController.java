package lct.feedbacksrv.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lct.feedbacksrv.domain.Postamat;
import lct.feedbacksrv.resource.ErrorsList;
import lct.feedbacksrv.resource.Paginator;
import lct.feedbacksrv.service.PostamatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

import static lct.feedbacksrv.resource.RandomGenerators.*;
import static lct.feedbacksrv.resource.Utils.objectMapper;
import static lct.feedbacksrv.resource.Utils.stringToDate;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */

@Slf4j
@RestController
@RequestMapping("/postamat")
@Api(value = "Постаматы",
        description = "Методы для работы с постаматами")
public class PostamatController extends MainController {
    private static final String LAYOUT = "postamat";
    @Autowired
    PostamatService postamatService;

    public PostamatController() {
        super(LAYOUT);
    }

    @GetMapping
    @ApiIgnore
    public ModelAndView getPostamatsListUIWithoutPages() {
        return postPagesAndGetPostamatsListUI(0,0);
    }
    @PostMapping
    @ApiIgnore
    public ModelAndView postPagesAndGetPostamatsListUI(Integer page, Integer pageSize) {

            log.info("Get postamats list page {}, pageSize {}", page, pageSize);

            Map<String, Object> data = getHeaderMap();
            data.put("content", "postamatslist");

            try {
                Long postamatsCount = postamatService.getCount();
                if(page > 0) page--;
                pageSize = pageSize==0 ? 30 : pageSize;
                int from = page * pageSize;

                Paginator.PaginatorBuilder paginator = Paginator.builder();
                paginator.pageCount(Paginator.calculatePageCount(postamatsCount.intValue(), pageSize));
                if(postamatsCount.intValue() < from) {
                    data.put("postamats", postamatService.getPostamatsByLimitAndOffset(pageSize, 0));
                    paginator.currentPage(1);
                } else {
                    data.put("postamats", postamatService.getPostamatsByLimitAndOffset(pageSize, from));
                    paginator.currentPage(page+1);
                }
                paginator.pageSize(pageSize);
                data.put("paginator", paginator.build());
            } catch (Exception e) {
                log.info("Exception on getting postamat list method", e);
                data.put("content", "error");
                data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
            }

            return render(data);

    }
    @GetMapping(path = {"/info/{id}"})
    @ApiIgnore
    public ModelAndView getOneMessageUI(@PathVariable("id") String id) {
        log.info("Get postamat {}", id);

        Map<String, Object> data = getHeaderMap();
        data.put("content", "postamatslist");

        try {
            Long partnersCount = postamatService.getCount();
            int page = 0;
            int pageSize = 30;
            Optional<Postamat> p = postamatService.getPostamat(id);
            List<Postamat> singlePostamat = new ArrayList<>();
            if(p.isEmpty()) {
                singlePostamat.add(Postamat.builder()
                        .id(id)
                        .comment("Сообщение не найдено")
                        .build());
            } else {
                singlePostamat.add(p.get());
            }
            data.put("postamats", singlePostamat);

            Paginator.PaginatorBuilder paginator = Paginator.builder();
            paginator.pageCount(Paginator.calculatePageCount(partnersCount.intValue(), pageSize));
            paginator.currentPage(1);

            paginator.pageSize(pageSize);
            data.put("paginator", paginator.build());

        } catch (Exception e) {
            log.info("Exception on getting postamats method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);
    }
    @Operation(
            summary = "Получить список постаматов",
            description = "Возвращает список постаматов"
    )
    @GetMapping(path={"/all"})
    public ResponseEntity getPostamatsList(String login, String password) {
        try{
            return ResponseEntity.ok(postamatService.getAllPostamats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping(path={"/add"})
    @ApiIgnore
    public ModelAndView getAddPageUI() {
        log.info("Get add postamat page");

        Map<String, Object> data = getHeaderMap();
        data.put("content", "edit");
        data.put("action", "add");

        try {
                data.put("postamat", Postamat.builder().build());

        } catch (Exception e) {
            log.error("Exception on getting add postamat page method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);

    }

    @GetMapping(path={"/map"})
    @ApiIgnore
    public ModelAndView getMapPageUI() {
        log.info("Get postamats on map");

        Map<String, Object> data = getHeaderMap();
        data.put("content", "map");

        return render(data);

    }

    @GetMapping(path={"/getjsonmap"})
    @Operation(
            summary = "Получить модель со списком постаматов для отображения на карте",
            description = "Возвращает json-объект, благодаря которому через API Яндекс.Карт на карте Москвы расставляются метки постаматов и выводится игнформация о них"
    )
    public String getMapJson() {
        log.info("Get json");

        try {
            return objectMapper.writeValueAsString(postamatService.getMapData());

        } catch (Exception e) {
            log.error("Exception on getting map method", e);
            return null;
        }

    }

    @Operation(
            summary = "Добавить постамат",
            description = "Позволяет добавить постамат в систему"
    )
    @PostMapping(path={"/add"})
    public ResponseEntity addPostamat(String login,
                                     String password,
                                     String address,
                                     String postIndex,
                                     String ipAddress,
                                     Double latitude,
                                     Double longitude,
                                     String setupDate,
                                     String softwareVersion,
                                     String comment,
                                      String type,
                                      String location
    ) {
        try{
            Postamat.PostamatBuilder postamat = Postamat.builder();

            if(StringUtils.isNotBlank(address))
                postamat.address(address);
            if(StringUtils.isNotBlank(postIndex))
                postamat.postIndex(postIndex);
            if(StringUtils.isNotBlank(ipAddress))
                postamat.ipAddress(ipAddress);
            if(latitude != null)
                postamat.latitude(latitude);
            if(longitude != null)
                postamat.longitude(longitude);
            if(setupDate != null) {
                Date d = stringToDate(setupDate);
                if(d != null) postamat.setupDate(d);
            }
            if(StringUtils.isNotBlank(softwareVersion))
                postamat.softwareVersion(softwareVersion);
            if(StringUtils.isNotBlank(comment))
                postamat.comment(comment);
            if(StringUtils.isNotBlank(type))
                postamat.type(type);
            if(StringUtils.isNotBlank(location))
                postamat.location(location);
            return ResponseEntity.ok(postamatService.addPostamat(postamat.build()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping(path={"/edit/{id}"})
    @ApiIgnore
    public ModelAndView getEditPageUI(@PathVariable("id") String id) {
        log.info("Edit postamat page");

        Map<String, Object> data = getHeaderMap();
        data.put("content", "edit");
        data.put("action", "edit");

        try {
            Optional<Postamat> postamat = postamatService.findPostamat(id);
            if(postamat.isPresent()) {
                data.put("postamat", postamat.get());
            } else {
                log.info("Postamat {} not found", id);
                data.put("content", "error");
                data.put("errorType", ErrorsList.POSTAMAT_NOT_FOUND.getDescription());
            }

        } catch (Exception e) {
            log.error("Exception on getting edit postamat page method", e);
            data.put("content", "error");
            data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        }

        return render(data);

    }
    @Operation(
            summary = "Изменить постамат",
            description = "Позволяет изменить информацию о постамате"
    )
    @PostMapping(path = {"/edit"})
    public ResponseEntity editPostamat(String login,
                                       String password,
                                       String id,
                                       String address,
                                       String postIndex,
                                       String ipAddress,
                                       Double latitude,
                                       Double longitude,
                                       String setupDate,
                                       String softwareVersion,
                                       String comment,
                                       String type,
                                       String location
    ) {
        try{
            if(postamatService.findPostamat(id).isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(String.format("Постамат %s не найден", id));
            }
            Postamat.PostamatBuilder postamat = Postamat.builder();

            postamat.id(id);
            if(StringUtils.isNotBlank(address))
                postamat.address(address);
            if(StringUtils.isNotBlank(postIndex))
                postamat.postIndex(postIndex);
            if(StringUtils.isNotBlank(ipAddress))
                postamat.ipAddress(ipAddress);
            if(latitude != null)
                postamat.latitude(latitude);
            if(longitude != null)
                postamat.longitude(longitude);
            if(setupDate != null) {
                Date d = stringToDate(setupDate);
                if(d != null) postamat.setupDate(d);
            }
            if(StringUtils.isNotBlank(softwareVersion))
                postamat.softwareVersion(softwareVersion);
            if(StringUtils.isNotBlank(comment))
                postamat.comment(comment);
            if(StringUtils.isNotBlank(type))
                postamat.type(type);
            if(StringUtils.isNotBlank(location))
                postamat.location(location);
            return ResponseEntity.ok(postamatService.editPostamat(postamat.build()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @DeleteMapping(path={"/delete/{id}"})
    @ApiIgnore
    public ModelAndView deletePostamatUI(@PathVariable("id") String id) {
        log.info("Get postamats list");

        Map<String, Object> data = getHeaderMap();
        data.put("content", "postamatslist");

        try {
            Optional<Postamat> postamat = postamatService.findPostamat(id);
            if(postamat.isEmpty()) {
                data.put("content", "error");
                data.put("errorType", ErrorsList.POSTAMAT_NOT_FOUND.getDescription());
                return render(data);
            }
            if(postamatService.deletePostamat(postamat.get())) {
                return getPostamatsListUIWithoutPages();
            }

        } catch (Exception e) {
            log.info("Exception on getting postamat list method", e);
        }
        data.put("content", "error");
        data.put("errorType", ErrorsList.SERVICE_NOT_AVAILABLE.getDescription());
        return render(data);
    }

    @Operation(
            summary = "Удалить постамат",
            description = "Позволяет удалить постамат из системы"
    )
    @DeleteMapping(path={"/delete"})
    public ResponseEntity deletePostamat(String login,
                                        String password,
                                        String id
    ) {
        try{
            Optional<Postamat> postamat = postamatService.findPostamat(id);
            if(postamat.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(String.format("Постамат %s не найден", id));
            }
            if(postamatService.deletePostamat(postamat.get())) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @Operation(
        summary = "ЗАПОЛНЕНИЕ СЛУЧАЙНЫМИ ДАННЫМИ",
        description = "Заполняет списка постоматов случайными данными"
    )
    @PostMapping(path={"/rand"})
    public ResponseEntity getPostamatsList(int count) {
        try{
            log.info("Generate {} rand postamats", count);
            if(count != 0) {
                Postamat.PostamatBuilder p = Postamat.builder();
                for(int i = 0; i < count; i++) {
                    p.address(String.format("Адрес %d, дом %d",
                            i + 1, getRandIntInRange(1, 100)));
                    p.ipAddress(getRandomIp());
                    p.postIndex(String.format("%d",
                            getRandIntInRange(101000, 155599)));
                    p.longitude(getRandMoscowLongitude());
                    p.latitude(getRandMoscowLatitude());
                    p.setupDate(getRandDate());
                    p.softwareVersion(String.format("%d.%d.%d",
                            getRandIntInRange(0, 25),
                            getRandIntInRange(0, 55),
                            getRandIntInRange(0, 99)));
                    p.comment("Случайно сгенерированные данные");
                    p.type(String.format("Тип №%d", getRandIntInRange(1, 5)));
                    p.location(String.format("Локация №%d", getRandIntInRange(1, 10)));
                    postamatService.addPostamat(p.build());
                }
            }
            return ResponseEntity.ok(postamatService.getAllPostamats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }
}
