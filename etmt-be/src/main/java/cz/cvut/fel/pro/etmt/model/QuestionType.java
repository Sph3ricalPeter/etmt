package cz.cvut.fel.pro.etmt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QuestionType {

    @JsonProperty("shortAnswer")
    SHORT_ANSWER("shortanswer"),

    @JsonProperty("singleChoice")
    SINGLE_CHOICE("singlechoice"),

    @JsonProperty("multipleChoice")
    MULTIPLE_CHOICE("multichoice");

    private final String name;

    public static QuestionType getByName(final String name) {
        for (var value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException(name);
    }

}
