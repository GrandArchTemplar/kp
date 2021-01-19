package kp.backend.dataService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class GraphDTO {

    @Getter
    @Setter
    private String vertices;

    @Getter
    @Setter
    private String edges;

}
