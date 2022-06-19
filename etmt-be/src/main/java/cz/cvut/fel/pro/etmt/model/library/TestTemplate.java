package cz.cvut.fel.pro.etmt.model.library;

import cz.cvut.fel.pro.etmt.model.GenerationStrategy;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("item")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TestTemplate extends Item {

    private List<Topic> topics = new ArrayList<>();

    @Builder
    public TestTemplate(String id, String parentId, String title, String key, List<Topic> topics) {
        super(id, parentId, title, key, true);
        this.topics = topics;
    }
}
