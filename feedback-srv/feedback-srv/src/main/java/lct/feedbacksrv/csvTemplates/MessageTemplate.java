package lct.feedbacksrv.csvTemplates;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Api(value = "Сообщение пользователя")
@ApiModel
@Data
@JsonPropertyOrder({ "comment", "createdate", "stars", "summ" })
public class MessageTemplate implements Serializable {
    private String comment;
    private String createdate;
    private Integer stars;
    private Double price;

}
