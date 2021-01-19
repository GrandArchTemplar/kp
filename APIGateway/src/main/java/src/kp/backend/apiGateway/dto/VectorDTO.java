package src.kp.backend.apiGateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class VectorDTO {

    @Getter
    @Setter
    private List<Float> vector;

}
