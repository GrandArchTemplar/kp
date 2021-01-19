package src.kp.backend.apiGateway.dto.dataService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class DataServiceResponseDTO {

    @Getter
    @Setter
    private List<Integer> viewedFilmsIds = new ArrayList<>();

    @Getter
    @Setter
    private List<String> missingFilms = new ArrayList<>();

}
