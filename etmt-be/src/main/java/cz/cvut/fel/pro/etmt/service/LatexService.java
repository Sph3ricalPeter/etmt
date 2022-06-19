package cz.cvut.fel.pro.etmt.service;

import cz.cvut.fel.pro.etmt.model.library.TestVariant;
import cz.cvut.fel.pro.etmt.util.LatexStringBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class LatexService {

    public byte[] generateTexFromVariant(@NotNull final TestVariant testVariant) throws Exception {
        var builder = new LatexStringBuilder();
        builder.create(testVariant.getTitle(), testVariant.getDate());
        if (Objects.nonNull(testVariant.getInstructions()) && !testVariant.getInstructions().isEmpty()) {
            builder.instructions(testVariant.getInstructions());
        }
        for (var question : testVariant.getQuestions()) {
            builder.question(question);
        }
        var string = builder.build();
        log.info("\n" + string);
        return string.getBytes();
    }

    public ResponseEntity<byte[]> upload(@NotNull final byte[] latexBytes) {
        var url = System.getenv("PDFLATEX_HOST") + "/upload";

        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        var entity = new HttpEntity<byte[]>(latexBytes, headers);

        return restTemplate.<byte[]>postForEntity(url, entity, byte[].class);
    }
}
