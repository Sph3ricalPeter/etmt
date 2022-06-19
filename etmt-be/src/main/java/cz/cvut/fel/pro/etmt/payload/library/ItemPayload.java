package cz.cvut.fel.pro.etmt.payload.library;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CategoryPayload.class, name = "category"),
        @JsonSubTypes.Type(value = QuestionPayload.class, name = "question"),
        @JsonSubTypes.Type(value = TestTemplatePayload.class, name = "template"),
        @JsonSubTypes.Type(value = TestVariantPayload.class, name = "variant")
})
public abstract class ItemPayload {

    protected String id;

    protected String parentId;

    @NotBlank(message = "Title cannot be empty")
    protected String title;

    protected String key;

    @JsonProperty("isLeaf")
    private boolean isLeaf = false;

}
