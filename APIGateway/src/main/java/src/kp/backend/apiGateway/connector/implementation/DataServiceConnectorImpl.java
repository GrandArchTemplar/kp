package src.kp.backend.apiGateway.connector.implementation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import src.kp.backend.apiGateway.connector.DataServiceConnector;
import src.kp.backend.apiGateway.dto.FilmDTO;
import src.kp.backend.apiGateway.dto.GraphDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class DataServiceConnectorImpl implements DataServiceConnector {

    @Override
    public List<Integer> checkFilmsExist(List<Integer> viewedFilms) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Integer>> request = new HttpEntity<>(viewedFilms, headers);

        ResponseEntity<List> response = dataServiceRestTemplate
                .exchange(API_SUFFIX + "/films/present", HttpMethod.POST, request, List.class);

        if (response.getStatusCodeValue() == 200 && Objects.nonNull(response.getBody())) {
            List<Integer> result = new ArrayList<>();
            response.getBody().forEach(o -> result.add((Integer) o));
            return result;
        }
        else return new ArrayList<>();
    }

    @Override
    public List<Integer> persistFilms(List<FilmDTO> films) {
        return null;
    }

    @Override
    public GraphDTO getGraph() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<GraphDTO> response = dataServiceRestTemplate
                .exchange(API_SUFFIX + "/graph", HttpMethod.GET, request, GraphDTO.class);

        if (response.getStatusCodeValue() == 200)
            return response.getBody();
        else return new GraphDTO();
    }

    @Override
    public GraphDTO getSubGraph(FilmDTO recommendation, List<String> viewedFilms) {
        return null;
    }

    @Override
    public FilmDTO getFilmInfo(Integer filmId) {
        return null;
    }

    @Override
    public String getAllFilmsAsCSVString() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = dataServiceRestTemplate
                .exchange(API_SUFFIX + "/films", HttpMethod.GET, request, String.class);

        if (response.getStatusCodeValue() == 200)
            return response.getBody();
        else return null;
    }

    private RestTemplate dataServiceRestTemplate;

    public DataServiceConnectorImpl(@Qualifier("dataServiceRestTemplate") RestTemplate restTemplate){
        dataServiceRestTemplate = restTemplate;
    }

    private static final String API_SUFFIX = "/api/v1";

}
