package src.kp.backend.apiGateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import src.kp.backend.apiGateway.connector.DataServiceConnector;
import src.kp.backend.apiGateway.connector.ExtractorConnector;
import src.kp.backend.apiGateway.connector.ReasonerConnector;
import src.kp.backend.apiGateway.dto.FilmDTO;
import src.kp.backend.apiGateway.dto.GraphDTO;
import src.kp.backend.apiGateway.dto.api.RecommendationDTO;
import src.kp.backend.apiGateway.dto.dataService.DataServiceResponseDTO;
import src.kp.backend.apiGateway.dto.reasoner.ReasonerRequestDTO;

import java.util.List;

@Component
public class RoutingServiceImpl implements RoutingService {

    private DataServiceConnector dataServiceConnector;
    private ReasonerConnector reasonerConnector;

    @Autowired
    public RoutingServiceImpl(DataServiceConnector dataServiceConnector,
                              ReasonerConnector reasonerConnector){
        this.dataServiceConnector = dataServiceConnector;
        this.reasonerConnector = reasonerConnector;
    }

    @Override
    public RecommendationDTO getRecommendation(List<Integer> viewedFilms) {

        //check if all films are present in the db
        List<Integer> missingFilms = dataServiceConnector.checkFilmsExist(viewedFilms);
        if (!missingFilms.isEmpty()) viewedFilms.removeAll(missingFilms);

        //get a recommendation
        List<Integer> recommendedFilmIds = reasonerConnector.getRecommendation(new ReasonerRequestDTO(viewedFilms));

        //get graph
        GraphDTO graphDTO = dataServiceConnector.getGraph();

        //return recommendation with graph to visualize on
        return new RecommendationDTO(recommendedFilmIds, graphDTO.getVertices(), graphDTO.getEdges());
    }

    @Override
    public String getAllFilmInfo() {
        return dataServiceConnector.getAllFilmsAsCSVString();
    }
}
