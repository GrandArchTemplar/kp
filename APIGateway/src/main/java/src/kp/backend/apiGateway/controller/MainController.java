package src.kp.backend.apiGateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import src.kp.backend.apiGateway.dto.api.RecommendationDTO;
import src.kp.backend.apiGateway.service.RoutingService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class MainController {

    private RoutingService routingService;

    @Autowired
    public MainController(RoutingService routingService){
        this.routingService = routingService;
    }

    @PostMapping("/recommendation")
    public RecommendationDTO getRecommendation(@RequestBody List<Integer> viewedFilms) {
        return routingService.getRecommendation(viewedFilms);
    }

    @GetMapping("/film")
    public String getAllFilmsAsCSVString(){
        return routingService.getAllFilmInfo();
    }

}
