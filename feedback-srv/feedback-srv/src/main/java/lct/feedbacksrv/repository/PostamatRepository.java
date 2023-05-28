package lct.feedbacksrv.repository;

import lct.feedbacksrv.domain.Postamat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * TODO: Add interface description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface PostamatRepository extends JpaRepository<Postamat, String> {
    @Query(value="SELECT * FROM postamats WHERE is_deleted=FALSE ORDER BY id offset ?1 limit ?2", nativeQuery = true)
    List<Postamat> findPage(int offset, int limit);
    @Query(value="SELECT * FROM postamats WHERE is_deleted=FALSE", nativeQuery = true)
    List<Postamat> findExists();
    @Query(value="SELECT count(*) FROM postamats WHERE is_deleted=FALSE", nativeQuery = true)
    Long existsCount();
}
