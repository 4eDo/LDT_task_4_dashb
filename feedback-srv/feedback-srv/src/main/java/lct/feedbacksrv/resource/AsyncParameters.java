package lct.feedbacksrv.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncParameters {
    private boolean enabled;
    private int poolCoreSize;
    private int poolWorkerSize;
}
