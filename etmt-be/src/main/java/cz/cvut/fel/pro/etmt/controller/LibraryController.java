package cz.cvut.fel.pro.etmt.controller;

import cz.cvut.fel.pro.etmt.model.library.Category;
import cz.cvut.fel.pro.etmt.model.library.Item;
import cz.cvut.fel.pro.etmt.payload.ApiResponse;
import cz.cvut.fel.pro.etmt.payload.library.CategoryPayload;
import cz.cvut.fel.pro.etmt.payload.library.ItemPayload;
import cz.cvut.fel.pro.etmt.service.AuthService;
import cz.cvut.fel.pro.etmt.service.ItemService;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RestControllerAdvice
@AllArgsConstructor
@Slf4j
@RequestMapping("/library")
public class LibraryController<K extends Item, V extends ItemPayload> {

    protected final ModelMapper modelMapper;
    protected final AuthService authService;
    protected final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemPayload>> getLibraryItemsList() {
        var libraryItemsList = itemService.getLibraryItemsListForUserId(authService.getCurrentUser());
        List<ItemPayload> itemPayloadList = modelMapper.map(libraryItemsList, new TypeToken<List<ItemPayload>>() {
        }.getType());
        return ResponseEntity.ok(itemPayloadList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> removeItemById(@PathVariable @NotNull final String id, @RequestParam final boolean deleteChildren) throws NotFoundException {
        itemService.removeItemById(id, deleteChildren);

        return ResponseEntity.ok(new ApiResponse("item deleted"));
    }

    @PostMapping("/move")
    public ResponseEntity<ApiResponse> moveItemInHierarchy(@RequestParam @NotNull final String itemId, @RequestParam @NotNull final String newParentId, @RequestParam @NotNull final Integer position) throws Exception {
        itemService.moveItem(itemId, newParentId, position);

        return ResponseEntity.ok(new ApiResponse("item moved"));
    }

    protected ResponseEntity<V> getItemById(@PathVariable @NotNull final String id, Class<K> itemClass, Class<V> payloadClass) throws Exception {
        var item = itemService.getItemById(id, itemClass);

        var payload = modelMapper.map(item, payloadClass);

        return ResponseEntity.ok(payload);
    }

    protected <T extends Item> ResponseEntity<ApiResponse> addItem(@RequestBody @Validated final ItemPayload itemPayload, Class<T> clazz) throws Exception {
        var item = modelMapper.map(itemPayload, clazz);

        item = itemService.addItem(item, clazz);

        return ResponseEntity.ok(new ApiResponse(item.getId()));
    }

    protected ResponseEntity<ApiResponse> editCategory(@RequestBody @Validated final CategoryPayload itemPayload, @PathVariable @NotNull final String id) throws Exception {
        var category = modelMapper.map(itemPayload, Category.class);

        itemService.updateCategory(id, category);

        return ResponseEntity.ok().body(new ApiResponse("item edited"));
    }

    protected <T extends Item> ResponseEntity<ApiResponse> editItem(@RequestBody @Validated final ItemPayload itemPayload, Class<T> clazz, @PathVariable @NotNull final String id) throws Exception {
        var item = modelMapper.map(itemPayload, clazz);

        itemService.updateItem(id, item, clazz);

        return ResponseEntity.ok().body(new ApiResponse("item edited"));
    }

}
