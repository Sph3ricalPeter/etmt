package cz.cvut.fel.pro.etmt.service.generator;

import cz.cvut.fel.pro.etmt.model.library.TestTemplate;

import java.util.List;

public interface Generator {

    List<List<Object>> generateQuestionCombinations(final TestTemplate testTemplate) throws Exception;

}
