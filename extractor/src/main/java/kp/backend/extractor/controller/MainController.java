package kp.backend.extractor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class MainController {

    @GetMapping("/imdb")
    public String getImdbSnapshot(){
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/IMDb movies.csv")))
                .lines().collect(Collectors.joining());
    }

}
