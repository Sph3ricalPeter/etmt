package cz.cvut.fel.pro.etmt.controller;

import cz.cvut.fel.pro.etmt.model.library.TestTemplate;
import cz.cvut.fel.pro.etmt.payload.ApiResponse;
import cz.cvut.fel.pro.etmt.payload.library.TestTemplatePayload;
import cz.cvut.fel.pro.etmt.service.AuthService;
import cz.cvut.fel.pro.etmt.service.ItemService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RestControllerAdvice
@RequestMapping("/templates")
public class TestTemplateController extends LibraryController<TestTemplate, TestTemplatePayload> {

    public TestTemplateController(ModelMapper modelMapper, AuthService authService, ItemService itemService) {
        super(modelMapper, authService, itemService);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestTemplatePayload> getTemplateById(@PathVariable final String id) throws Exception {
        return getItemById(id, TestTemplate.class, TestTemplatePayload.class);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse> editTemplate(@RequestBody @Validated final TestTemplatePayload templatePayload, @PathVariable @NotNull final String id) throws Exception {
        return editItem(templatePayload, TestTemplate.class, id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addTemplate(@RequestBody @Validated final TestTemplatePayload templatePayload) throws Exception {
        return addItem(templatePayload, TestTemplate.class);
    }

}
