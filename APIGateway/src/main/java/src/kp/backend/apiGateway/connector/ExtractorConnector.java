package src.kp.backend.apiGateway.connector;

import src.kp.backend.apiGateway.dto.FilmDTO;

import java.util.List;

public interface ExtractorConnector {

    List<FilmDTO> getFilmsInfoByName(List<String> filmNames);

}
