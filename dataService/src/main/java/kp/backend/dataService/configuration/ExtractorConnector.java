package kp.backend.dataService.configuration;

import kp.backend.dataService.util.handler.RestTemplateErrorHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "kp.extractor")
public class ExtractorConnector {

    @Getter
    @Setter
    private String url;

    @Bean(name = "extractorServiceRestTemplate")
    public RestTemplate extractorServiceRestTemplate(RestTemplateBuilder builder){
        return builder.uriTemplateHandler(new RootUriTemplateHandler(getUrl() + "/extractor"))
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }

}
