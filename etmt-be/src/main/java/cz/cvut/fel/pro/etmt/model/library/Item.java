package cz.cvut.fel.pro.etmt.model.library;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Item {

    @Id
    protected String id;

    @NotNull
    protected String parentId;

    @NotBlank(message = "Title cannot be empty")
    protected String title;

    @NotNull
    protected String key;

    protected boolean isLeaf = false;

}
