package lct.feedbacksrv.resource.mapTemplates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * Template for yandex map api
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YaProperties implements Serializable {
    private String balloonContentHeader;
    private String balloonContentBody;
    private String balloonContentFooter;
    private String clusterCaption;
    private String hintContent;
}
