package lct.feedbacksrv.service;

import lct.feedbacksrv.domain.Partner;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;

/**
 * TODO: Add interface description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface PartnerService {
    Partner addPartner(Partner p);
    Optional<Partner> getPartnerById(Long id);
    Partner editPartner(Partner p);
    Boolean deletePartner(Partner p);
    List<Partner> findByName(String name);
    Optional<Partner> findById(Long id);
    List<Partner> getPartners();
    List<String> getPartnersNames();
    List<Pair<Long, String>> getPartnersIdAndNames();
    List<Partner> getPartnersByLimitAndOffset(int limit, int offset);
    Long getCount();
}
