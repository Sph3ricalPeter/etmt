package cz.cvut.fel.pro.etmt.payload.library;

import java.util.Set;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

import cz.cvut.fel.pro.etmt.model.QuestionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QuestionPayload extends ItemPayload {

    private QuestionType questionType;

    private String text;

    @Min(value = 1, message = "Points reward needs to be at least 1")
    private Integer points;

    @Min(value = 0, message = "Points penalty needs to be at least 0")
    private Float penalty;

    private Set<AnswerPayload> answers;

    @JsonProperty("isLeaf")
    private boolean isLeaf = true;

}
