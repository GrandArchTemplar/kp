package kp.backend.dataService.controller;

import kp.backend.dataService.dto.GraphDTO;
import kp.backend.dataService.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MainController {

    @GetMapping("/graph")
    public GraphDTO getGraph(){
        return neo4jService.getGraph();
    }

    @GetMapping("/films")
    public String getFilmsAsCSVString(){
        return neo4jService.getFilms();
    }

    @PostMapping("/films/present")
    public List<Integer> checkPresent(@RequestBody List<Integer> films){
        return neo4jService.checkFilmsExist(films);
    }

    @Autowired
    public MainController(Neo4jService neo4jService){
        this.neo4jService = neo4jService;
    }

    private Neo4jService neo4jService;




}
