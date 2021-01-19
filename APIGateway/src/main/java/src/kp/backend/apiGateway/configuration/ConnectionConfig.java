package src.kp.backend.apiGateway.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import src.kp.backend.apiGateway.util.handler.RestTemplateErrorHandler;

@Configuration
@ConfigurationProperties(prefix = "kp.services")
public class ConnectionConfig {

    @Getter
    @Setter
    private String dataServiceUrl;

    @Getter
    @Setter
    private String reasonerServiceUrl;

    @Getter
    @Setter
    private String extractorServiceUrl;

    private RestTemplateErrorHandler restTemplateErrorHandler;

    public ConnectionConfig(){
        this.restTemplateErrorHandler = new RestTemplateErrorHandler();
    }

    @Bean(name = "dataServiceRestTemplate")
    public RestTemplate dataServiceRestTemplate(RestTemplateBuilder builder){
        return builder.uriTemplateHandler(new RootUriTemplateHandler(getDataServiceUrl() + "/dataservice"))
                .errorHandler(restTemplateErrorHandler)
                .build();
    }

    @Bean(name = "extractorServiceRestTemplate")
    public RestTemplate extractorServiceRestTemplate(RestTemplateBuilder builder){
        return builder.uriTemplateHandler(new RootUriTemplateHandler(getExtractorServiceUrl() + "/extractor"))
                .errorHandler(restTemplateErrorHandler)
                .build();
    }

    @Bean(name = "reasonerServiceRestTemplate")
    public RestTemplate reasonerServiceRestTemplate(RestTemplateBuilder builder){
        return builder.uriTemplateHandler(new RootUriTemplateHandler(getReasonerServiceUrl() + "/reasoner"))
                .errorHandler(restTemplateErrorHandler)
                .build();
    }

}
