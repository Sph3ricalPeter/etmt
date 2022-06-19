package cz.cvut.fel.pro.etmt.model.library;

import cz.cvut.fel.pro.etmt.model.QuestionType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Document("item")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Question extends Item {

    private QuestionType questionType;

    private String text;

    @Min(value = 1, message = "Points reward needs to be at least 1")
    private Integer points;

    @Min(value = 0, message = "Points penalty needs to be at least 0")
    private Float penalty;

    private List<Answer> answers = new ArrayList<>();

    @Builder
    public Question(String id, String parentId, String title, String key, QuestionType questionType, String text, Integer points, Float penalty, List<Answer> answers) {
        super(id, parentId, title, key, true);
        this.questionType = questionType;
        this.text = text;
        this.points = points;
        this.penalty = penalty;
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "Question(type: '" + questionType + "', title: '" + title + "', points: " + points + ", penalty: " + penalty + ", text: '" + text + "')";
    }

}
