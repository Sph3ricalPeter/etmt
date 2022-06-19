package cz.cvut.fel.pro.etmt.controller;

import cz.cvut.fel.pro.etmt.model.library.Question;
import cz.cvut.fel.pro.etmt.payload.ApiResponse;
import cz.cvut.fel.pro.etmt.payload.library.QuestionPayload;
import cz.cvut.fel.pro.etmt.service.AuthService;
import cz.cvut.fel.pro.etmt.service.ItemService;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RestControllerAdvice
@RequestMapping("/questions")
public class QuestionController extends LibraryController<Question, QuestionPayload> {

    public QuestionController(ModelMapper modelMapper, AuthService authService, ItemService itemService) {
        super(modelMapper, authService, itemService);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionPayload> getQuestionById(@PathVariable final String id) throws Exception {
        return getItemById(id, Question.class, QuestionPayload.class);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addQuestion(@RequestBody @Validated final QuestionPayload questionPayload) throws Exception {
        return addItem(questionPayload, Question.class);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse> editQuestion(@RequestBody @Validated final QuestionPayload questionPayload, @PathVariable @NotNull final String id) throws Exception {
        return editItem(questionPayload, Question.class, id);
    }

}
