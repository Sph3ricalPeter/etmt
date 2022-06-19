package cz.cvut.fel.pro.etmt.service;

import com.github.jesg.dither.Dither;
import cz.cvut.fel.pro.etmt.model.GenerationStrategy;
import cz.cvut.fel.pro.etmt.model.library.Category;
import cz.cvut.fel.pro.etmt.model.library.Question;
import cz.cvut.fel.pro.etmt.model.library.TestTemplate;
import cz.cvut.fel.pro.etmt.model.library.TestVariant;
import cz.cvut.fel.pro.etmt.service.generator.CompromiseGenerator;
import cz.cvut.fel.pro.etmt.service.generator.Generator;
import cz.cvut.fel.pro.etmt.service.generator.UniqueGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TestVariantService {

    private ItemService itemService;

    private UniqueGenerator uniqueGenerator;
    private CompromiseGenerator compromiseGenerator;

    public boolean checkSufficientQuestionCountInSubtree(@NotNull final Category root, final Integer points, final Integer count) throws Exception {
        var questions = itemService.getQuestionsWithGivenPointsFromSubtree(root, Optional.ofNullable(points));
        return questions.size() >= count;
    }

    public List<TestVariant> generateTestVariantsForTemplate(@NotNull final TestTemplate template, final GenerationStrategy strategy) throws Exception {
        List<List<Object>> generatedQuestions;

        if (strategy == GenerationStrategy.COMPROMISE) {
            generatedQuestions = compromiseGenerator.generateQuestionCombinations(template);
        } else {
            generatedQuestions = uniqueGenerator.generateQuestionCombinations(template);
        }

        var category = itemService.addItem(Category.builder()
                .title(String.format("Variants - %s (%s)", template.getTitle(), strategy))
                .parentId(template.getParentId())
                .build(), Category.class);

        var variants = new ArrayList<TestVariant>();
        for (var i = 0; i < generatedQuestions.size(); i++) {
            var variantData = generatedQuestions.get(i);

            var testVariant = new TestVariant();
            testVariant.setQuestions(variantData.stream()
                    .filter(o -> o instanceof Question)
                    .map(o -> (Question) o)
                    .collect(Collectors.toList())
            );
            testVariant.setParentId(category.getId());
            testVariant.setTitle(String.format("Variant %d", i + 1));
            testVariant.setLeaf(true);
            testVariant = itemService.addItem(testVariant, TestVariant.class);

            variants.add(testVariant);
        }

        return variants;
    }

}
