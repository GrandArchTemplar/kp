package src.kp.backend.apiGateway.dto.extractor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class ExtractorRequestDTO {

    @Getter
    @Setter
    private List<String> filmsToAdd = new ArrayList<>();

}
