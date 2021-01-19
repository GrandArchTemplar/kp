package src.kp.backend.apiGateway.connector;

import src.kp.backend.apiGateway.dto.reasoner.ReasonerRequestDTO;

import java.util.List;

public interface ReasonerConnector {

    List<Integer> getRecommendation(ReasonerRequestDTO requestDTO);

}
