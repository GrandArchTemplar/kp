package src.kp.backend.apiGateway.dto.reasoner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class ReasonerRequestDTO {

    @Getter
    @Setter
    private List<Integer> viewedFilmsIds = new ArrayList<>();

}
