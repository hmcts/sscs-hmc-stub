package uk.gov.hmcts.reform.sscs.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info().title(applicationName)
                      .description("HMC Stub is a spring boot based application to mock HMC requests")
                      .version("v0.0.1")
                      .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
            .externalDocs(new ExternalDocumentation()
                              .description("README")
                              .url("https://github.com/hmcts/spring-boot-template"));
    }

}
