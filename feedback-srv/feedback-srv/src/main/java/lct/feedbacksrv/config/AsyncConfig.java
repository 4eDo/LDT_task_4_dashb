package lct.feedbacksrv.config;

import lct.feedbacksrv.resource.AsyncParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

/**
 * Async configuration
 *
 * @author Balashova Maria (balashovamaria00@gmail.com)
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${async.enabled}")
    private boolean asyncEnabled;
    @Value("${async.pool.core.size}")
    private int poolCoreSize;
    @Value("${async.pool.worker.size}")
    private int poolWorkerSize;

    @Bean
    public AsyncParameters asyncParameters() {
        log.info("*** Async parameters: [Enabled: {}, Pool core size: {}, Pool worker size: {}]",
                asyncEnabled, poolCoreSize, poolWorkerSize);
        return AsyncParameters.builder()
                .enabled(asyncEnabled)
                .poolCoreSize(asyncEnabled ? poolCoreSize : 1)
                .poolWorkerSize(asyncEnabled ? poolWorkerSize: 1)
                .build();
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(asyncParameters().getPoolCoreSize(),
                new CustomizableThreadFactory("async-sched-"));
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        AsyncParameters asyncParameters = asyncParameters();
        int maxPoolSize = asyncParameters.getPoolWorkerSize();
        return new ThreadPoolExecutor(maxPoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomizableThreadFactory("async-worker-"));
   }
}
