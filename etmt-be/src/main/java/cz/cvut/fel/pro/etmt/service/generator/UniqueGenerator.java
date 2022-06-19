package cz.cvut.fel.pro.etmt.service.generator;

import cz.cvut.fel.pro.etmt.model.library.Category;
import cz.cvut.fel.pro.etmt.model.library.Question;
import cz.cvut.fel.pro.etmt.model.library.TestTemplate;
import cz.cvut.fel.pro.etmt.service.ItemService;
import cz.cvut.fel.pro.etmt.util.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UniqueGenerator implements Generator {

    private ItemService itemService;

    @Override
    public List<List<Object>> generateQuestionCombinations(TestTemplate testTemplate) throws Exception {
        var root = itemService.getItemById(testTemplate.getParentId(), Category.class);
        var topics = testTemplate.getTopics();

        // for each topic, get the available questions
        // if there's < 1, throw exception - not enough questions
        // if there's at least 1, track max number of questions out of all topics
        // the final amount of variants be determined by that

        // variants data should be
        /*
            maxVariants = 1
            t1: part1: q1, q2
                part2: q3
            t2: part1: q5, q6
            t3: part1, q7, q8, q9
         */

        List<List<Object>> topicQuestionsList = new ArrayList<>();
        var maxVariants = Integer.MAX_VALUE;
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
                availableQuestions.addAll(itemService.getQuestionsWithGivenPointsFromSubtree(category, Optional.ofNullable(topic.getPoints())));
            }

            if (availableQuestions.size() < topic.getQuestionCount()) {
                throw new Exception(String.format("not enough %dpt questions", topic.getPoints()));
            }

            // create even sized partitions based on total available questions for given amount of points and question count
            var partitions = Utils.partition(availableQuestions, topic.getQuestionCount());

            for (var partition : partitions) {
                maxVariants = Math.min(maxVariants, partition.size());
                if (maxVariants < 1) {
                    throw new Exception(String.format("not enough %dpt questions", topic.getPoints()));
                }
            }

            // add all partitions to the final variants
            for (var partition : partitions) {
                topicQuestionsList.add(new ArrayList<>(partition));
            }
        }

        // TODO: test template should have the generation strategy in it maybe?
        /* TODO:
            - test template should have the generation strategy in it maybe?
            - whether it can be generated or not is tied to the selected strategy
            - it should also show that tests can't be generated in the template detail
         */

        var variants = new ArrayList<List<Object>>();
        for (var i = 0; i < maxVariants; i++) {
            variants.add(new ArrayList<>());
        }

        for (var topicQuestions : topicQuestionsList) {
            for (var i = 0; i < maxVariants; i++) {
                variants.get(i).add(topicQuestions.get(i));
            }
        }

        // strip the result variants of the incomplete ones
        return variants;
    }
}
