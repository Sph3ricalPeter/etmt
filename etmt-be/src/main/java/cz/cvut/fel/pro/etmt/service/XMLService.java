package cz.cvut.fel.pro.etmt.service;

import cz.cvut.fel.pro.etmt.model.QuestionType;
import cz.cvut.fel.pro.etmt.model.library.Answer;
import cz.cvut.fel.pro.etmt.model.library.Category;
import cz.cvut.fel.pro.etmt.model.library.Question;
import cz.cvut.fel.pro.etmt.model.library.TestVariant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Float.*;

@Service
@AllArgsConstructor
@Slf4j
public class XMLService {

    private ItemService itemService;

    public void importCategories(@NotNull final byte[] data, @NotNull final String parentId) throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();

        var stream = new ByteArrayInputStream(data);

        var doc = builder.parse(stream);
        doc.getDocumentElement().normalize();

        var questionNodes = doc.getElementsByTagName("question");

        var currentCategory = itemService.getItemById(parentId, Category.class);
        for (var i = 0; i < questionNodes.getLength(); i++) {
            var questionEl = (Element) questionNodes.item(i);
            var typeValue = questionEl.getAttribute("type");

            // create a parent category for the questions that follow, until the next category occurs
            if (typeValue.equals("category")) {
                var categoryFullPath = getInnerText(questionEl);
                var pathParts = categoryFullPath.split("/");
                var categoryName = pathParts[pathParts.length - 1]; // category name is last part after last forward slash

                var category = Category.builder()
                        .title(categoryName)
                        .parentId(parentId)
                        .isLeaf(false)
                        .build();

                currentCategory = itemService.addItem(category, Category.class);
                continue;
            }

            QuestionType questionType;
            if (typeValue.equals("essay")) {
                // TODO: add essay type question with no answers (look into the xml)
                questionType = QuestionType.SHORT_ANSWER;
            } else {
                try {
                    questionType = QuestionType.getByName(typeValue);
                } catch (IllegalArgumentException e) {
                    log.error(String.format("unsupported question type %s in import, question skipped", typeValue));
                    continue;
                }
            }

            var title = getInnerText((Element) questionEl.getElementsByTagName("name").item(0));
            var text = getInnerText((Element) questionEl.getElementsByTagName("questiontext").item(0));

            var points = (int) parseFloat(questionEl.getElementsByTagName("defaultgrade").item(0).getTextContent());
            var penalty = parseFloat(questionEl.getElementsByTagName("penalty").item(0).getTextContent());

            var answerNodeList = questionEl.getElementsByTagName("answer");
            var answerList = new ArrayList<Answer>();
            for (var j = 0; j < answerNodeList.getLength(); j++) {
                var answerEl = (Element) answerNodeList.item(j);
                var fraction = parseFloat(answerEl.getAttribute("fraction"));
                var answerText = getInnerText(answerEl);

                var answer = new Answer();
                answer.setText(answerText);
                answer.setCorrect(fraction > 0);

                answerList.add(answer);
            }


            var question = Question.builder()
                    .title(title)
                    .text(text)
                    .questionType(questionType)
                    .parentId(currentCategory.getId())
                    .points(points)
                    .penalty(penalty)
                    .answers(answerList)
                    .build();

            question = itemService.addItem(question, Question.class);
        }
    }

    public String export(@NotNull final TestVariant testVariant, @NotNull final String exportParentCategoryName) throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();

        var doc = builder.newDocument();

        var rootEl = doc.createElement("quiz");
        doc.appendChild(rootEl);

        appendCategory(doc, rootEl, exportParentCategoryName);

        for (var question : testVariant.getQuestions()) {
            appendQuestion(doc, rootEl, question, exportParentCategoryName);
        }

        // write dom document to a file
        try {
            var outStream = new ByteArrayOutputStream();
            writeXml(doc, outStream);
            return outStream.toString();
        } catch (TransformerException e) {
            throw new Exception("failed to create XML output stream");
        }
    }

    private void appendCategory(Document doc, Element parent, String parentCategoryTitle) {
        // question
        var questionEl = doc.createElement("question");
        parent.appendChild(questionEl);
        questionEl.setAttribute("type", "category");

        // category
        var categoryEl = questionEl.appendChild(doc.createElement("category"));
        var textEl = categoryEl.appendChild(doc.createElement("text"));
        var moodleParentCategoryName = parentCategoryTitle.replaceAll("\\s+", "").toLowerCase();
        textEl.setTextContent("$course$/etmt/" + moodleParentCategoryName);
    }

    private void appendQuestion(Document doc, Element parent, Question question, String tag) {
        // question
        var questionEl = doc.createElement("question");
        parent.appendChild(questionEl);
        questionEl.setAttribute("type", question.getQuestionType().getName());

        // name
        var nameEl = questionEl.appendChild(doc.createElement("name"));
        var nameTextEl = nameEl.appendChild(doc.createElement("text"));
        nameTextEl.setTextContent(question.getTitle());

        // question text
        var questionTextEl = doc.createElement("questiontext");
        questionEl.appendChild(questionTextEl);
        questionTextEl.setAttribute("format", "html");

        var questionTextTextEl = questionTextEl.appendChild(doc.createElement("text"));
        questionTextTextEl.appendChild(doc.createCDATASection(question.getText()));

        var defaultGradeEl = doc.createElement("defaultgrade");
        questionEl.appendChild(defaultGradeEl);
        defaultGradeEl.setTextContent(question.getPoints().toString());

        var penaltyEl = doc.createElement("penalty");
        questionEl.appendChild(penaltyEl);
        var penalty = Objects.isNull(question.getPenalty()) ? 0 : question.getPenalty();
        penaltyEl.setTextContent(String.valueOf(penalty));

        var answerCount = question.getAnswers().size();
        var correctCount = question.getAnswers().stream().filter(Answer::isCorrect).count();
        var correctFraction = 100f / correctCount;
        var wrongFraction = -100f / (answerCount - correctCount);
        for (var answer : question.getAnswers()) {
            var answerEl = doc.createElement("answer");
            questionEl.appendChild(answerEl);
            if (question.getQuestionType() != QuestionType.SHORT_ANSWER) {
                answerEl.setAttribute("fraction", String.valueOf(answer.isCorrect() ? correctFraction : wrongFraction));
            }
            answerEl.setAttribute("format", "html"); // todo change to plain_text if issues occur

            var answerTextEl = doc.createElement("text");
            answerEl.appendChild(answerTextEl);
            answerTextEl.appendChild(doc.createCDATASection(answer.getText()));
        }

        var tagsEl = questionEl.appendChild(doc.createElement("tags"));
        var tagEl = tagsEl.appendChild(doc.createElement("tag"));
        var textEl = tagEl.appendChild(doc.createElement("text"));
        textEl.setTextContent(tag);
    }

    // write doc to output stream
    private static void writeXml(Document doc,
                                 OutputStream output)
            throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // pretty print XML
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);
    }

    private static String getInnerText(Element element) {
        return element.getElementsByTagName("text").item(0).getTextContent();
    }

}
