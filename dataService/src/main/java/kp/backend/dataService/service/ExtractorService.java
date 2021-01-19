package kp.backend.dataService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExtractorService {

    private RestTemplate restTemplate;

    @Autowired
    public ExtractorService(@Qualifier("extractorServiceRestTemplate") RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public String getSnapshotAsCSVString(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate
                .exchange(API_SUFFIX + "/imdb", HttpMethod.GET, request, String.class);

        if (response.getStatusCodeValue() == 200)
            return response.getBody();
        else return null;
    }

    private static final String API_SUFFIX = "/api/v1";

}
