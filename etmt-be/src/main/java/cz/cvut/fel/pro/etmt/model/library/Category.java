package cz.cvut.fel.pro.etmt.model.library;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("item")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Category extends Item {

    @DBRef
    private List<Item> children = new ArrayList<>();

    @Builder
    public Category(String id, String parentId, String title, String key, boolean isLeaf) {
        super(id, parentId, title, key, isLeaf);
    }
}
