package lct.feedbacksrv.service;

import lct.feedbacksrv.domain.Postamat;
import lct.feedbacksrv.resource.mapTemplates.YaObject;

import java.util.List;
import java.util.Optional;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface PostamatService {
    Optional<Postamat> findPostamat(String id);
    Postamat addPostamat(Postamat postamat);
    Postamat editPostamat(Postamat postamat);
    Boolean deletePostamat(Postamat postamat);
    List<Postamat> getAllPostamats();
    List<String> getAllPostamatIds();
    List<Postamat> getPostamatsByLimitAndOffset(int limit, int offset);
    Long getCount();

    YaObject getMapData();
}
