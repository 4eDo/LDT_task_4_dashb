package lct.feedbacksrv.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PyRequestList implements Serializable {
    private List<PyRequestObject> list;
}
