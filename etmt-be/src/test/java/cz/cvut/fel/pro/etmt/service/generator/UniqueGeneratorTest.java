package cz.cvut.fel.pro.etmt.service.generator;

import cz.cvut.fel.pro.etmt.TestConstants;
import cz.cvut.fel.pro.etmt.service.ItemService;
import cz.cvut.fel.pro.etmt.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UniqueGeneratorTest {

    @InjectMocks
    private UniqueGenerator testSubject;

    @Mock
    private ItemService itemServiceMock;

    @Test
    public void testGenerate_oneTopicWithOneQuestion_shouldThrowException() throws Exception {
        when(itemServiceMock.getItemById(eq(TestConstants.TEST_TEMPLATE_MINIMAL.getParentId()), any())).thenReturn(TestConstants.TEST_ROOT_CATEGORY);
        var ex = assertThrows(Exception.class, () -> testSubject.generateQuestionCombinations(TestConstants.TEST_TEMPLATE_MINIMAL));
        assertEquals(ex.getMessage(), "not enough 3pt questions");
    }

    @Test
    public void testGenerate_oneTopicWithOneQuestion_shouldGenerateOneVariant() throws Exception {
        when(itemServiceMock.getItemById(eq(TestConstants.TEST_TEMPLATE_MINIMAL.getParentId()), any())).thenReturn(TestConstants.TEST_ROOT_CATEGORY);
        when(itemServiceMock.getQuestionsWithGivenPointsFromSubtree(any(), any())).thenReturn(List.of(TestConstants.THREE_PT_Q));
        var expectedCount = 1;

        var variants = testSubject.generateQuestionCombinations(TestConstants.TEST_TEMPLATE_MINIMAL);

        var actualCount = variants.size();
        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void testGenerate_twoTopicsOneAndOneQuestion_oneAndTwoAvailable_shouldGenerateTwoVariants() throws Exception {
        when(itemServiceMock.getItemById(eq(TestConstants.TEST_TEMPLATE_HALF.getParentId()), any())).thenReturn(TestConstants.TEST_ROOT_CATEGORY);
        when(itemServiceMock.getQuestionsWithGivenPointsFromSubtree(any(), eq(3))).thenReturn(List.of(TestConstants.THREE_PT_Q));
        when(itemServiceMock.getQuestionsWithGivenPointsFromSubtree(any(), eq(5))).thenReturn(List.of(TestConstants.FIVE_PT_Q, TestConstants.FIVE_PT_Q));
        var expectedCount = 1;

        var variants = testSubject.generateQuestionCombinations(TestConstants.TEST_TEMPLATE_HALF);

        var actualCount = variants.size();
        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void testGenerate_twoTopicsTwoAndTwoQuestions_notEnoughThreePointQuestions_shouldThrowException() throws Exception {
        when(itemServiceMock.getItemById(eq(TestConstants.TEST_TEMPLATE_FULL.getParentId()), any())).thenReturn(TestConstants.TEST_ROOT_CATEGORY);
        when(itemServiceMock.getQuestionsWithGivenPointsFromSubtree(any(), eq(3))).thenReturn(List.of(TestConstants.THREE_PT_Q));

        var ex = assertThrows(Exception.class, () -> testSubject.generateQuestionCombinations(TestConstants.TEST_TEMPLATE_FULL));
        assertEquals(ex.getMessage(), "not enough 3pt questions");
    }

    @Test
    public void testGenerate_twoTopicsTwoQuestions_allQuestionsAvailable_shouldReturnFourVariants() throws Exception {
        when(itemServiceMock.getItemById(eq(TestConstants.TEST_TEMPLATE_HALF.getParentId()), any())).thenReturn(TestConstants.TEST_ROOT_CATEGORY);
        when(itemServiceMock.getQuestionsWithGivenPointsFromSubtree(any(), eq(3))).thenReturn(List.of(TestConstants.THREE_PT_Q, TestConstants.THREE_PT_Q));
        when(itemServiceMock.getQuestionsWithGivenPointsFromSubtree(any(), eq(5))).thenReturn(List.of(TestConstants.FIVE_PT_Q, TestConstants.FIVE_PT_Q, TestConstants.FIVE_PT_Q));
        var expectedCount = 2;

        var variants = testSubject.generateQuestionCombinations(TestConstants.TEST_TEMPLATE_HALF);
        Utils.log2DList(variants);

        var actualCount = variants.size();
        assertEquals(expectedCount, actualCount);
    }

}
