package cz.cvut.fel.pro.etmt.service;

import cz.cvut.fel.pro.etmt.TestConstants;
import cz.cvut.fel.pro.etmt.model.QuestionType;
import cz.cvut.fel.pro.etmt.model.library.Answer;
import cz.cvut.fel.pro.etmt.model.library.Question;
import cz.cvut.fel.pro.etmt.model.library.TestVariant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class XMLServiceTest {

    @InjectMocks
    private XMLService testSubject;

    @Test
    public void testExport() throws Exception {
        String output = testSubject.export(TestConstants.TEST_VARIANT, "test");
    }

}
