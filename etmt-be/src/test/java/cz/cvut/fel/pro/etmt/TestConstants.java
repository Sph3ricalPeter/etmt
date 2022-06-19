package cz.cvut.fel.pro.etmt;

import cz.cvut.fel.pro.etmt.model.QuestionType;
import cz.cvut.fel.pro.etmt.model.Role;
import cz.cvut.fel.pro.etmt.model.User;
import cz.cvut.fel.pro.etmt.model.library.*;
import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.List;
import java.util.Set;

@UtilityClass
public class TestConstants {

    public static final User USER_NOT_REGISTERED = User.builder()
            .id("anonymousUserId")
            .username("anonymousUser")
            .password("testPassword")
            .role(Set.of(Role.ROLE_USER))
            .build();

    public static final User REGISTERED_USER = User.builder()
            .id("registeredUserId")
            .username("registeredUser")
            .password("testPassword")
            .role(Set.of(Role.ROLE_USER))
            .build();

    public static final TestVariant TEST_VARIANT = TestVariant.builder()
            .title("Epic test")
            .date(new Date())
            .questions(List.of(
                    Question.builder()
                            .title("Epic question")
                            .questionType(QuestionType.MULTIPLE_CHOICE)
                            .text("How much shit does this question eat daily?")
                            .points(5)
                            .penalty(1f)
                            .answers(List.of(
                                    new Answer("This is epic", true)
                            ))
                            .build()
            )).build();

    public static final Question THREE_PT_Q = Question.builder()
            .title("Test question")
            .text("Test question text")
            .questionType(QuestionType.SHORT_ANSWER)
            .points(3)
            .penalty(1f)
            .build();

    public static final Question FIVE_PT_Q = Question.builder()
            .title("Test question")
            .text("Test question text")
            .questionType(QuestionType.SHORT_ANSWER)
            .points(5)
            .penalty(1f)
            .build();

    public static final Category TEST_ROOT_CATEGORY = Category.builder()
            .title("Test root category")
            .isLeaf(false)
            .build();

    public static final TestTemplate TEST_TEMPLATE_MINIMAL = TestTemplate.builder()
            .title("Test template")
            .topics(List.of(
                    new Topic(1, 3, null)
            )).build();

    public static final TestTemplate TEST_TEMPLATE_HALF = TestTemplate.builder()
            .title("Test template")
            .topics(List.of(
                    new Topic(1, 3, null),
                    new Topic(1, 5, null)
            )).build();

    public static final TestTemplate TEST_TEMPLATE_FULL = TestTemplate.builder()
            .title("Test template")
            .topics(List.of(
                    new Topic(2, 3, null),
                    new Topic(2, 5, null)
            )).build();

}
