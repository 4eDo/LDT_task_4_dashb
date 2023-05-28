package lct.feedbacksrv.repository;

import lct.feedbacksrv.domain.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * TODO: Add interface description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    List<Partner> findAllByName(String name);

    @Query(value="SELECT * FROM partners WHERE is_deleted=FALSE ORDER BY name offset ?1 limit ?2", nativeQuery = true)
    List<Partner> findPage(int offset, int limit);

    @Query(value="SELECT * FROM partners WHERE is_deleted=FALSE", nativeQuery = true)
    List<Partner> findExists();

    @Query(value="SELECT count(*) FROM partners WHERE is_deleted=FALSE", nativeQuery = true)
    Long existsCount();
}
