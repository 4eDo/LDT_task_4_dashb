package lct.feedbacksrv.service.impl;

import lct.feedbacksrv.domain.Postamat;
import lct.feedbacksrv.repository.PostamatRepository;
import lct.feedbacksrv.resource.mapTemplates.YaFeature;
import lct.feedbacksrv.resource.mapTemplates.YaGeometryPoint;
import lct.feedbacksrv.resource.mapTemplates.YaObject;
import lct.feedbacksrv.resource.mapTemplates.YaProperties;
import lct.feedbacksrv.service.PostamatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PostamatServiceImpl implements PostamatService {
    @Autowired
    PostamatRepository postamatRepository;

    @Override
    public Optional<Postamat> findPostamat(String id) {
        try{
            return postamatRepository.findById(id);
        } catch (Exception e) {
            log.error("Error in findPostamat method", e);
        }
        return Optional.empty();
    }

    @Override
    public Postamat addPostamat(Postamat postamat) {
        try{
            String uuid = UUID.randomUUID().toString();
            postamat.setId(uuid);
            return postamatRepository.saveAndFlush(postamat);
        } catch (Exception e) {
            log.error("Error in addPostamat method", e);
        }
        return null;
    }

    @Override
    public Postamat editPostamat(Postamat postamat) {
        try{
            return postamatRepository.saveAndFlush(postamat);
        } catch (Exception e) {
            log.error("Error in editPostamat method", e);
        }
        return null;
    }

    @Override
    public Boolean deletePostamat(Postamat postamat) {
        try{
            postamat.setIsDeleted(true);
            postamat.setComment("Работа постамата преостановлена");
            postamatRepository.saveAndFlush(postamat);
            return true;
        } catch (Exception e) {
            log.error("Error in deletePostamat method", e);
        }
        return false;
    }

    @Override
    public List<Postamat> getAllPostamats() {
        try{
            return postamatRepository.findExists();
        } catch (Exception e) {
            log.error("Error in getAllPostamats method", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getAllPostamatIds() {
        List<String> result = new ArrayList<>();
        getAllPostamats().forEach(postamat -> result.add(postamat.getId()));
        return result;
    }

    @Override
    public List<Postamat> getPostamatsByLimitAndOffset(int limit, int offset) {
        try{
            return postamatRepository.findPage(offset,limit);
        } catch (Exception e) {
            log.error("Error in getPostamatsByLimitAndOffset method", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Long getCount() {
        return postamatRepository.existsCount();
    }

    @Override
    public YaObject getMapData() {
        YaObject map = new YaObject();
        List<YaFeature> features = new ArrayList<>();

        AtomicLong id = new AtomicLong(0);
        postamatRepository.findExists().forEach(postamat -> {
            YaProperties properties = new YaProperties();
            properties.setBalloonContentHeader("Информация о постамате");
            properties.setBalloonContentFooter("Демонстрационная версия сервиса обработки обратной связи. Команда 'Дашбордизация'");
            properties.setHintContent("Информация о постамате");
            properties.setBalloonContentBody(getHtmlInfo(postamat));
            properties.setClusterCaption(String.format("Рядом: [%s]", postamat.getAddress()));
            YaFeature.YaFeatureBuilder temp = YaFeature.builder();
            temp.id(id.getAndIncrement());
            temp.properties(properties);

            YaGeometryPoint point = new YaGeometryPoint();
            point.setCoordinates(new Double[]{postamat.getLatitude(), postamat.getLongitude()});
            temp.geometry(point);
            features.add(temp.build());
        });
        map.setFeatures(features);
        return map;
    }

    private String getHtmlInfo(Postamat p) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<p>Адрес установки: %s</p>", p.getAddress()));
        sb.append(String.format("<p>Почтовый индекс: %s</p>", p.getPostIndex()));
        sb.append(String.format("<p>%s, %s</p>", p.getLatitude(), p.getLongitude()));
        sb.append(String.format("<p>Локация: %s</p>", p.getLocation()));
        sb.append(String.format("<p>Тип постамата: %s</p>", p.getType()));
        return sb.toString();
    }
}
