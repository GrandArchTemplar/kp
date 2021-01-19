package src.kp.backend.apiGateway.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {

    @Getter
    @Setter
    private List<Integer> recommended = new ArrayList<>();

    @Getter
    @Setter
    private String vertices;

    @Getter
    @Setter
    private String edges;

}
