package cz.cvut.fel.pro.etmt.controller;

import cz.cvut.fel.pro.etmt.model.library.Category;
import cz.cvut.fel.pro.etmt.payload.ApiResponse;
import cz.cvut.fel.pro.etmt.payload.library.CategoryPayload;
import cz.cvut.fel.pro.etmt.service.AuthService;
import cz.cvut.fel.pro.etmt.service.ItemService;
import cz.cvut.fel.pro.etmt.service.XMLService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@RestController
@RestControllerAdvice
@RequestMapping("/categories")
public class CategoryController extends LibraryController<Category, CategoryPayload> {

    private final XMLService xmlService;

    public CategoryController(ModelMapper modelMapper, AuthService authService, ItemService itemService, XMLService xmlService) {
        super(modelMapper, authService, itemService);
        this.xmlService = xmlService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryPayload> getCategoryById(@PathVariable final String id) throws Exception {
        return getItemById(id, Category.class, CategoryPayload.class);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addCategory(@RequestBody @Validated final CategoryPayload categoryPayload) throws Exception {
        return addItem(categoryPayload, Category.class);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse> editCategory(@RequestBody @Validated final CategoryPayload categoryPayload, @PathVariable @NotNull final String id) throws Exception {
        return super.editCategory(categoryPayload, id);
    }

    @PostMapping("/import/{parentId}")
    public ResponseEntity<ApiResponse> importCategory(@RequestBody @NotNull final MultipartFile file, @PathVariable @NotNull final String parentId) throws Exception {
        var data = file.getBytes();

        xmlService.importCategories(data, parentId);

        return ResponseEntity.ok(new ApiResponse("import ok"));
    }

}
