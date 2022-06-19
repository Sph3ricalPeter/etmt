package cz.cvut.fel.pro.etmt.util;

import cz.cvut.fel.pro.etmt.model.library.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.aggregation.DateOperators;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LatexStringBuilder {

    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    private StringBuilder sb;

    public LatexStringBuilder() {
        sb = new StringBuilder();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
    }

    public LatexStringBuilder create(@NotNull final String title, final Date date) {
        sb.append(String.format("\\documentclass[a4paper]{article}\n" +
                "\n" +
                "\\usepackage[english]{babel}\n" +
                "\\usepackage[utf8]{inputenc}\n" +
                "\\usepackage{mathtools}\n" +
                "\\usepackage{graphicx}\n" +
                "\\usepackage{parskip}\n" +
                "\n" +
                "\\graphicspath{ {/mnt/tex/images/} }\n" +
                "\n" +
                "\\everymath{\\displaystyle}\n" +
                "\n" +
                "\\title{%s}\n" +
                "\n" +
                "\\author{name: \\texttt{\\detokenize{______________}}}\n" +
                "\n", title));

        if (Objects.nonNull(date)) {
            var formattedDate = sdf.format(date);
            sb.append(String.format("\\date{%s}\n\n", sdf.format(date)));
        }

        sb.append("\\begin{document}\n" +
                "\\maketitle" +
                "\n");

        return this;
    }

    public LatexStringBuilder instructions(@NotNull final String instructions) {
        sb.append(String.format("\\renewcommand{\\abstractname}{Instructions}\n" +
                "\\begin{abstract}\n" +
                "%s\n" +
                "\\end{abstract}\n\\bigskip", instructions));
        return this;
    }

    public LatexStringBuilder question(@NotNull final Question question) throws Exception {
        switch (question.getQuestionType()) {
            case SINGLE_CHOICE:
            case MULTIPLE_CHOICE:
                appendChoiceQuestion(question);
                break;
            case SHORT_ANSWER:
                appendShortAnswerQuestion(question);
                break;
            default:
                throw new Exception("question type " + question.getQuestionType() + " is not supported");
        }
        return this;
    }

    public String build() {
        sb.append("\\end{document}");
        return sb.toString();
    }

    private void appendChoiceQuestion(@NotNull final Question question) {
        sb.append(String.format("\\begin{enumerate}\n" +
                "  \\item %s (%s) \\hfill (%d b)\\\\\n" +
                "    \\begin{enumerate}", Objects.isNull(question.getText()) ? question.getTitle() : question.getText(), question.getQuestionType().getName(), question.getPoints()));

        for (var answer : question.getAnswers()) {
            sb.append(String.format("\\item \\texttt{[ ]} %s\\\\", answer.getText()));
        }

        sb.append("\\end{enumerate}\n" +
                "\\end{enumerate}\n");
    }

    private void appendShortAnswerQuestion(@NotNull final Question question) {
        sb.append(String.format("\\begin{enumerate}\n" +
                        "  \\item %s (%s) \\hfill (%d b)\\\\\n" +
                        "\n" +
                        "\n" +
                        "  \n" +
                        "\\end{enumerate}\n\\bigskip",
                Objects.isNull(question.getText()) ? question.getTitle() : question.getText(), question.getQuestionType().getName(), question.getPoints()));
    }
}
