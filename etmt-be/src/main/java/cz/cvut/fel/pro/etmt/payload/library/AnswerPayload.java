package cz.cvut.fel.pro.etmt.payload.library;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnswerPayload {

    private String text;

    @JsonProperty("isCorrect")
    private boolean isCorrect;

}
