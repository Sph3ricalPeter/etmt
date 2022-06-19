package cz.cvut.fel.pro.etmt.payload.library;

import javax.validation.constraints.Min;

import cz.cvut.fel.pro.etmt.model.library.Category;
import cz.cvut.fel.pro.etmt.model.library.CategoryInfo;
import cz.cvut.fel.pro.etmt.payload.util.CategoryInfoPayload;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
@NoArgsConstructor
public class TopicPayload {

    @Min(value = 1, message = "Topic has to have at least 1 question")
    private Integer questionCount;

    @Min(value = 1, message = "Topic has to reward at least 1 point")
    private Integer points;

    private List<List<CategoryInfoPayload>> categories;

}
