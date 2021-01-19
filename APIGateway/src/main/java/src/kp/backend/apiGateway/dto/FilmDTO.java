package src.kp.backend.apiGateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class FilmDTO {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String name;

}
