package lct.feedbacksrv.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Paginator implements Serializable {
    private int pageCount;
    private int currentPage;
    private int pageSize;

    public static int calculatePageCount(int totalCount, int pageSize) {
        int fullPages = totalCount / pageSize;
        int tailPage = totalCount % pageSize == 0 ? 0 : 1;
        return fullPages + tailPage;
    }
}
