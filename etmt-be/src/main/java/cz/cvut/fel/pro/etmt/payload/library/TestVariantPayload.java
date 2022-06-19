package cz.cvut.fel.pro.etmt.payload.library;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.pro.etmt.model.library.Question;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TestVariantPayload extends ItemPayload {

    @JsonProperty("isLeaf")
    private boolean isLeaf = true;

    private Date date;

    private String instructions;

    private List<Question> questions;

    private List<String> solution;

}
