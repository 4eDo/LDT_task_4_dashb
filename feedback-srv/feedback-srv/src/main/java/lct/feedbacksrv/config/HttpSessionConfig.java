package lct.feedbacksrv.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Http session configuration
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Configuration
public class HttpSessionConfig {

    @Bean
    @Qualifier("httpSession")
    public Map<String, String> getHttpSession() {
        return new HashMap<>();
    }
}
