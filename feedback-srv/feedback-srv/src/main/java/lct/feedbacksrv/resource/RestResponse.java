package lct.feedbacksrv.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse {
    private int status;
    private String         description;
    private Object         data;
}
