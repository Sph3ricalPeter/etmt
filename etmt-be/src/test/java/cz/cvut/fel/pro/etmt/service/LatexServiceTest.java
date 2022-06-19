package cz.cvut.fel.pro.etmt.service;

import static org.junit.jupiter.api.Assertions.*;

import cz.cvut.fel.pro.etmt.TestConstants;
import cz.cvut.fel.pro.etmt.model.QuestionType;
import cz.cvut.fel.pro.etmt.model.library.Answer;
import cz.cvut.fel.pro.etmt.model.library.Question;
import cz.cvut.fel.pro.etmt.model.library.TestVariant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class LatexServiceTest {

    @InjectMocks
    private LatexService testSubject;

    @Test
    public void test() throws IOException, InterruptedException {
        assertEquals(HttpStatus.OK, testSubject.upload(new byte[]{}).getStatusCode());
    }

    @Test
    public void test_generateTex() throws Exception {
        testSubject.generateTexFromVariant(TestConstants.TEST_VARIANT);
    }

}
