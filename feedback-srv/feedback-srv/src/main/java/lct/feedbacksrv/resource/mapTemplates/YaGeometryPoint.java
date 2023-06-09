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
public class YaGeometryPoint implements Serializable {
    @Builder.Default
    private String type = "Point";
    @Builder.Default
    private Double[] coordinates = new Double[2];
}
