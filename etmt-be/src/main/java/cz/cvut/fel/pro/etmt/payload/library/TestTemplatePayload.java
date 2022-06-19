package cz.cvut.fel.pro.etmt.payload.library;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TestTemplatePayload extends ItemPayload {

    @JsonProperty("isLeaf")
    private boolean isLeaf = true;

    private List<TopicPayload> topics;

}
