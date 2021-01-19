package src.kp.backend.apiGateway.connector.implementation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import src.kp.backend.apiGateway.connector.ReasonerConnector;
import src.kp.backend.apiGateway.dto.reasoner.ReasonerRequestDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ReasonerConnectorImpl implements ReasonerConnector {


    @Override
    public List<Integer> getRecommendation(ReasonerRequestDTO requestDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Integer>> request = new HttpEntity<>(requestDTO.getViewedFilmsIds(), headers);

        ResponseEntity<List> response = reasonerServiceRestTemplate
                .exchange(API_SUFFIX + "/recommend", HttpMethod.POST, request, List.class);

        if (response.getStatusCodeValue() == 200 && Objects.nonNull(response.getBody())) {
            List<Integer> result = new ArrayList<>();
            response.getBody().forEach(o -> result.add((Integer) o));
            return result;
        }
        else return new ArrayList<>();
    }

    private RestTemplate reasonerServiceRestTemplate;

    public ReasonerConnectorImpl(@Qualifier("reasonerServiceRestTemplate") RestTemplate restTemplate){
        reasonerServiceRestTemplate = restTemplate;
    }

    private static final String API_SUFFIX = "/api/v1";

}
