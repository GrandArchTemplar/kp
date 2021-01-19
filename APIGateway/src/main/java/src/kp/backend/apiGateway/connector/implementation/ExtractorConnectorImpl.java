package src.kp.backend.apiGateway.connector.implementation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import src.kp.backend.apiGateway.connector.ExtractorConnector;
import src.kp.backend.apiGateway.dto.FilmDTO;

import java.util.List;

@Component
public class ExtractorConnectorImpl implements ExtractorConnector {

    private RestTemplate extractorServiceRestTemplate;

    public ExtractorConnectorImpl(@Qualifier("extractorServiceRestTemplate") RestTemplate restTemplate){
        extractorServiceRestTemplate = restTemplate;
    }

    @Override
    public List<FilmDTO> getFilmsInfoByName(List<String> filmNames) {
        return null;
    }
}
