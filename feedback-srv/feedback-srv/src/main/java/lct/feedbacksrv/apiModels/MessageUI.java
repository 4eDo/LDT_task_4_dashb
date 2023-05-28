package lct.feedbacksrv.apiModels;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

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
public class MessageUI implements Serializable {

    @ApiModelProperty(
            name = "Имя пользователя",
            value = "Имя пользователя, оставившего отзыв",
            example = "Иванов Иван Иванович",
            required = true
    )
    private String username;

    @ApiModelProperty(
            name = "Дата и время создания отзыва",
            value = "Дата и время создания отзыва. Если оставить поле пустым, временем создания отзыва будет считаться время получения отзыва сервисом обработки обратной связи. гггг-ММ-дд чч:мм:сс",
            dataType = "yyyy-MM-dd HH:mm:ss",
            example = "2023-04-20 12:34:56",
            required = false
    )
    private String createDate;

    @ApiModelProperty(
            name = "Постамат",
            value = "Идентификатор постамата, через который производилась выдача",
            required = true
    )
    private String postamat;

    @ApiModelProperty(
            name = "Заказ",
            value = "Идентификатор заказа",
            required = true
    )
    private String orderId;

    @ApiModelProperty(
            name = "Сообщение",
            value = "Содержание отзыва",
            required = false
    )
    private String message;

    @ApiModelProperty(
            name = "Оценка",
            value = "Оценка качества обслуживания",
            allowableValues = "1, 2, 3, 4, 5",
            required = true
    )
    @Builder.Default
    private Integer stars = 5;

    @ApiModelProperty(
            name = "Партнёр",
            value = "Идентификатор организации, продавшей пользователю товар",
            example = "123",
            required = true
    )
    @Builder.Default
    private Long partner = 0L;


    public Boolean hasUsername(){return StringUtils.isNotEmpty(username);}
    public Boolean hasCreateDate(){return StringUtils.isNotEmpty(createDate);}
    public Boolean hasPostamat(){return StringUtils.isNotEmpty(postamat);}
    public Boolean hasOrderId(){return StringUtils.isNotEmpty(orderId);}
    public Boolean hasMessage(){return StringUtils.isNotEmpty(message);}
    public Boolean hasStars(){return stars != null;}
    public Boolean hasPartner(){return partner != null;}
}
