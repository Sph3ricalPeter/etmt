package cz.cvut.fel.pro.etmt.controller;

import cz.cvut.fel.pro.etmt.model.GenerationStrategy;
import cz.cvut.fel.pro.etmt.model.library.Category;
import cz.cvut.fel.pro.etmt.model.library.TestTemplate;
import cz.cvut.fel.pro.etmt.model.library.TestVariant;
import cz.cvut.fel.pro.etmt.payload.ApiResponse;
import cz.cvut.fel.pro.etmt.payload.util.QuestionsSufficientPayload;
import cz.cvut.fel.pro.etmt.payload.library.TestVariantPayload;
import cz.cvut.fel.pro.etmt.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RestControllerAdvice
@RequestMapping("/variants")
public class TestVariantController extends LibraryController<TestVariant, TestVariantPayload> {

    private final TestVariantService testVariantService;
    private final XMLService xmlService;
    private final LatexService latexService;

    public TestVariantController(ModelMapper modelMapper, AuthService authService, ItemService itemService, TestVariantService testVariantService, XMLService xmlService, LatexService latexService) {
        super(modelMapper, authService, itemService);
        this.testVariantService = testVariantService;
        this.xmlService = xmlService;
        this.latexService = latexService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestVariantPayload> getTestVariantById(@PathVariable final String id) throws Exception {
        return getItemById(id, TestVariant.class, TestVariantPayload.class);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse> editTestVariant(@RequestBody @Validated final TestVariantPayload variantPayload, @PathVariable @NotNull final String id) throws Exception {
        return editItem(variantPayload, TestVariant.class, id);
    }

    @GetMapping("/check")
    public ResponseEntity<QuestionsSufficientPayload> checkSufficientQuestionsUnderCategory(@RequestParam @NotNull final String categoryId, @RequestParam final Integer points, @RequestParam final Integer count) throws Exception {
        var root = itemService.getItemById(categoryId, Category.class);
        return ResponseEntity.ok(new QuestionsSufficientPayload(root.getTitle(), testVariantService.checkSufficientQuestionCountInSubtree(root, points, count)));
    }

    @GetMapping("/generate")
    public ResponseEntity<List<TestVariant>> generate(@RequestParam final String testTemplateId, @RequestParam final GenerationStrategy strategy) throws Exception {
        var template = itemService.getItemById(testTemplateId, TestTemplate.class);

        List<TestVariant> variants = testVariantService.generateTestVariantsForTemplate(template, strategy);

        return ResponseEntity.ok().body(variants);
    }

    @GetMapping(value = "/xml")
    public ResponseEntity<byte[]> exportXML(@RequestParam @NotNull final String testVariantId, @RequestParam @NotNull final String parentCategoryName) throws Exception {
        var variant = itemService.getItemById(testVariantId, TestVariant.class);

        var xmlBytes = xmlService.export(variant, parentCategoryName).getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok().body(xmlBytes);
    }

    @GetMapping(value = "/pdf")
    public ResponseEntity<byte[]> exportPDF(@RequestParam @NotNull final String testVariantId) throws Exception {
        var variant = itemService.getItemById(testVariantId, TestVariant.class);

        var texBytes = latexService.generateTexFromVariant(variant);

        return latexService.upload(texBytes);
    }


}
