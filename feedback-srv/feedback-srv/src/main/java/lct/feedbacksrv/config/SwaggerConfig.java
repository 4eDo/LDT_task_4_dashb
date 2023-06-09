package lct.feedbacksrv.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

;

/**
 * Swagger configuration
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Configuration
@EnableSwagger2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SwaggerConfig {

    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES =
            new HashSet<String>(Arrays.asList("application/json", "multipart/mixed", "multipart/form-data", "text/plain",
                    "application/xml", "application/vnd.ms-excel", "application/octet-stream", "text/csv"));

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .produces(DEFAULT_PRODUCES_AND_CONSUMES)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMES)
                .apiInfo(metaInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .paths(Predicate.not(PathSelectors.regex("/error.*"))).build();

    }

    private ApiInfo metaInfo() {

        ApiInfo apiInfo = new ApiInfo(
                "API сервиса \"Московский постамат\"",
                "Разработано командой \"Дашбордизация\" в рамках хакатона \"Лидеры цифровой трансформации 2023\"",
                "1.0",
                "Условия обслуживания",
                "Дашбордизация",
                "Apache License Version 2.0",
                "https://www.apache.org/licenses/"
        );

        return apiInfo;
    }
}
