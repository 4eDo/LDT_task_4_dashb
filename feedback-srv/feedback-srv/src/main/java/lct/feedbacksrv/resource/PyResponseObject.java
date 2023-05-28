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
public class PyResponseObject implements Serializable {
    private Long id;
    private Float tone_predicted;
    private Float problem_predicted;
    private Float category_predicted;
}
