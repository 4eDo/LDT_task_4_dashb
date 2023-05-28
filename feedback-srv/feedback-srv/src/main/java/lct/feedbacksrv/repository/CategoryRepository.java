package lct.feedbacksrv.repository;


import lct.feedbacksrv.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * TODO: Add interface description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCode(String code);
}
