package cz.cvut.fel.pro.etmt.model.library;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    @Min(value = 1, message = "Topic has to have at least 1 question")
    private Integer questionCount;

    @Min(value = 1, message = "Topic has to reward at least 1 point")
    private Integer points;

    private List<List<CategoryInfo>> categories = new ArrayList<>();

}
