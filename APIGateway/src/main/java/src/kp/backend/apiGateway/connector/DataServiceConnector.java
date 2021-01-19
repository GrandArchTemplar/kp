package src.kp.backend.apiGateway.connector;

import src.kp.backend.apiGateway.dto.FilmDTO;
import src.kp.backend.apiGateway.dto.GraphDTO;
import src.kp.backend.apiGateway.dto.dataService.DataServiceResponseDTO;

import java.util.List;

public interface DataServiceConnector {

    List<Integer> checkFilmsExist(List<Integer> viewedFilms);

    List<Integer> persistFilms(List<FilmDTO> films);

    GraphDTO getGraph();

    GraphDTO getSubGraph(FilmDTO recommendation, List<String> viewedFilms);

    FilmDTO getFilmInfo(Integer filmId);

    String getAllFilmsAsCSVString();

}
