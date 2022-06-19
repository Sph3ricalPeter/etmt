package cz.cvut.fel.pro.etmt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GenerationStrategy {

    @JsonProperty("compromise")
    COMPROMISE,

    @JsonProperty("unique")
    UNIQUE

}
