package lct.feedbacksrv.service.impl;

import lct.feedbacksrv.domain.Partner;
import lct.feedbacksrv.repository.MessageRepository;
import lct.feedbacksrv.repository.PartnerRepository;
import lct.feedbacksrv.service.PartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static lct.feedbacksrv.resource.Utils.roundTail;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PartnerServiceImpl implements PartnerService {
    @Autowired
    PartnerRepository partnerRepository;

    @Value("${feedback.negative.recheck.min}")
    private int recheckMin;

    @Autowired
    MessageRepository messageRepository;

    private final Runtime runtime = Runtime.getRuntime();
    private final ThreadPoolExecutor executorService;
    private final ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    private void postConstruct() {
        checkNegative();
    }


    private void checkNegative() {
        try {
            float free = calculate(runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory()));
            boolean hasFreeMem = free > 60;

            if (hasFreeMem) {
                log.info("*** [Upd partner negative]");

                executorService.submit(this::updNegative);
            }
        } catch (Exception e) {
            log.error("3 Exception on check partner negative", e);
        }
        scheduledExecutorService.schedule(this::checkNegative, recheckMin, TimeUnit.MINUTES);
    }

    private float calculate(long targetMemory) {
        return (float) targetMemory / (float) runtime.maxMemory() * 100;
    }

    @Override
    public Partner addPartner(Partner p) {
        try{
            return partnerRepository.saveAndFlush(p);
        } catch (Exception e) {
            log.error("Error in addPartner method", e);
        }
        return null;
    }

    @Override
    public Optional<Partner> getPartnerById(Long id) {
        try{
            return partnerRepository.findById(id);
        } catch (Exception e) {
            log.error("Error in getPartnerById method", e);
        }
        return Optional.empty();
    }

    @Override
    public Partner editPartner(Partner p) {
        try{
            return partnerRepository.saveAndFlush(p);
        } catch (Exception e) {
            log.error("Error in editPartner method", e);
        }
        return null;
    }

    @Override
    public Boolean deletePartner(Partner p) {
        try{
            p.setIsDeleted(true);
            p.setDescription("Сотрудничество прекращено");
            partnerRepository.saveAndFlush(p);
            return true;
        } catch (Exception e) {
            log.error("Error in deletePartner method", e);
        }
        return false;
    }

    @Override
    public List<Partner> findByName(String name) {
        try{
            return partnerRepository.findAllByName(name);
        } catch (Exception e) {
            log.error("Error in findByName method", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<Partner> findById(Long id) {
        try{
            return partnerRepository.findById(id);
        } catch (Exception e) {
            log.error("Error in findById method", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Partner> getPartners() {
        try{
            return partnerRepository.findExists();
        } catch (Exception e) {
            log.error("Error in getPartners method", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getPartnersNames() {
        try{
            List<String> names = new ArrayList<>();
            partnerRepository.findExists().forEach(partner -> names.add(partner.getName()));
            return names;
        } catch (Exception e) {
            log.error("Error on get partners names list method!", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Pair<Long, String>> getPartnersIdAndNames() {
        List<Pair<Long, String>> result = new ArrayList<>();
        getPartners().forEach(partner -> result.add(new MutablePair(partner.getId(), partner.getName())));
        return result;
    }

    @Override
    public List<Partner> getPartnersByLimitAndOffset(int limit, int offset) {
        try{
            return partnerRepository.findPage(offset,limit);
        } catch (Exception e) {
            log.error("Error in getPartnersByLimitAndOffset method", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Long getCount() {
        return partnerRepository.existsCount();
    }

    private void updNegative() {
        log.info("[ partner ]\u2B50\u2B50\u2B50\u2B50\u2B50 upd negative start");
        partnerRepository.findExists().forEach(partner -> {
            try{
                Long total = messageRepository.findMessagesCountByPartner(partner.getName());
                Long bad = messageRepository.findMessagesCountWithPartnerBug(partner.getName());
                if(total < 1) total = 1L;
                partner.setNegativePercent(roundTail((bad/(double) total)*100, 2));
                partnerRepository.saveAndFlush(partner);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
        log.info("[ partner ]\u2B50\u2B50\u2B50\u2B50\u2B50 upd negative end");
    }
}
