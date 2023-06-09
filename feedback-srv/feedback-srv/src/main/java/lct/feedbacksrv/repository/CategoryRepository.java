package lct.feedbacksrv.repository;


import lct.feedbacksrv.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * TODO: Add interface description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCode(String code);
    @Query(value="SELECT * FROM categories WHERE parent = id AND code = ?1", nativeQuery = true)
    List<Category> findParentCat(String code);
    @Query(value="SELECT * FROM categories WHERE parent = ?1 AND code = ?2", nativeQuery = true)
    List<Category> findSubCat(Long parent, String code);
}
