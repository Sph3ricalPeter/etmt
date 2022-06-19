package cz.cvut.fel.pro.etmt.model.library;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document("item")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TestVariant extends Item {

    private Date date;

    private String instructions;

    @DBRef
    private List<Question> questions = new ArrayList<>();

    private List<String> solution = new ArrayList<>();

    @Builder
    public TestVariant(String id, String parentId, String title, String key, Date date, String instructions, List<Question> questions) {
        super(id, parentId, title, key, true);
        this.date = date;
        this.instructions = instructions;
        this.questions = questions;
    }

}
