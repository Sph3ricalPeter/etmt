package cz.cvut.fel.pro.etmt.service.generator;

import com.github.jesg.dither.Dither;
import cz.cvut.fel.pro.etmt.model.library.Category;
import cz.cvut.fel.pro.etmt.model.library.Question;
import cz.cvut.fel.pro.etmt.model.library.TestTemplate;
import cz.cvut.fel.pro.etmt.service.ItemService;
import cz.cvut.fel.pro.etmt.util.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CompromiseGenerator implements Generator {

    private ItemService itemService;

    @Override
    public List<List<Object>> generateQuestionCombinations(@NotNull final TestTemplate testTemplate) throws Exception {
        var root = itemService.getItemById(testTemplate.getParentId(), Category.class);
        var topics = testTemplate.getTopics();

        var sharedLoneQuestions = new ArrayList<>();
        var equivalenceClasses = new ArrayList<List<Question>>();

        // go through topics
        for (var topic : topics) {
            var searchedCategories = new ArrayList<Category>();

            if (Objects.nonNull(topic.getCategories())) {
                for (var ids : topic.getCategories()) {
                    try {
                        // in each selected row we're only looking for the last item as our parent for search
                        searchedCategories.add(itemService.getItemById(ids.get(ids.size() - 1).getId(), Category.class));
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }

            // no subroots specified for this topic, choose the parent category as root instead
            if (searchedCategories.isEmpty()) {
                searchedCategories.add(root);
            }

            var availableQuestions = new ArrayList<Question>();
            for (var category : searchedCategories) {
                // TODO: get questions from all subtrees simultaneously
                // having 3 categories specified for a topic means, that all of these categories COMBINED should
                // have enough questions, NOT each of them
                availableQuestions.addAll(itemService.getQuestionsWithGivenPointsFromSubtree(category, Optional.ofNullable(topic.getPoints())));
            }

            if (availableQuestions.size() < topic.getQuestionCount()) {
                throw new Exception(String.format("not enough %dpt questions", topic.getPoints()));
            }

            // create even sized partitions based on total available questions for given amount of points and question count
            var partitions = Utils.partition(availableQuestions, topic.getQuestionCount());

            // if partition size is < 2, bypass AETG (add-in later to the final result)
            partitions.forEach(partition -> {
                if (partition.size() < 2) {
                    sharedLoneQuestions.add(partition.get(0));
                } else {
                    equivalenceClasses.add(partition);
                }
            });
        }

        // if there's at least 2 equiv. classes, AETG can be used
        if (equivalenceClasses.size() > 1) {
            var aetgInput = equivalenceClasses.stream()
                    .map(Collection::toArray)
                    .toArray(Object[][]::new);

            var outputList = Arrays.stream(Dither.aetg(2, aetgInput)).toList();

            // if output is too long, pick randomly
            // TODO: move this limit to test template
            var limit = 20;
            if (outputList.size() > limit) {
                outputList = Utils.pickRandom(outputList, limit);
            }

            // add lone questions to the AETG output and return the result
            return outputList.stream()
                    .map(array -> {
                        var variant = Arrays.asList(array);
                        variant.addAll(sharedLoneQuestions);
                        return variant;
                    })
                    .collect(Collectors.toList());
        }

        // there's only 1 possible test to be created
        if (equivalenceClasses.size() < 1) {
            return List.of(sharedLoneQuestions);
        }

        // there's only a single equiv. class - we have to create all combinations manually
        return equivalenceClasses.get(0).stream()
                .map(question -> {
                    var variant = new ArrayList<Object>(sharedLoneQuestions);
                    variant.add(question);
                    return variant;
                })
                .collect(Collectors.toList());
    }



}
