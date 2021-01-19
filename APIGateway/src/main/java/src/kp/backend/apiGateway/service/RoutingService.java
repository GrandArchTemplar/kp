package src.kp.backend.apiGateway.service;

import src.kp.backend.apiGateway.dto.api.RecommendationDTO;

import java.util.List;

public interface RoutingService {

    RecommendationDTO getRecommendation(List<Integer> viewedFilms);

    String getAllFilmInfo();

}
