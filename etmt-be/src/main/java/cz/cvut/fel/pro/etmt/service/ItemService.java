package cz.cvut.fel.pro.etmt.service;

import cz.cvut.fel.pro.etmt.model.User;
import cz.cvut.fel.pro.etmt.model.library.Category;
import cz.cvut.fel.pro.etmt.model.library.Item;
import cz.cvut.fel.pro.etmt.model.library.Question;
import cz.cvut.fel.pro.etmt.repository.ItemRepository;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {

    private ItemRepository itemRepository;

    public String createRootCategory() {
        var category = Category.builder()
                .title("Root Category")
                .key("0-0")
                .build();

        return itemRepository.save(category).getId();
    }

    public List<Item> getLibraryItemsListForUserId(@NotNull final User user) {
        var optionalRootCategory = itemRepository.findById(user.getRootCategoryId());

        if (optionalRootCategory.isEmpty() || !(optionalRootCategory.get() instanceof Category)) {
            throw new IllegalStateException("user is missing root category");
        }

        return List.of(optionalRootCategory.get());
    }

    public <T> T getItemById(@NotNull final String id, Class<T> clazz) throws Exception {
        var optionalItem = itemRepository.findById(id).map(clazz::cast);
        if (optionalItem.isEmpty() || !clazz.isInstance(optionalItem.get())) {
            throw new Exception("item of type " + clazz.getName() + " with id " + id + " doesn't exist");
        }
        return optionalItem.get();
    }

    public List<Question> getQuestionsWithGivenPointsFromSubtree(@NotNull final Category root, final Optional<Integer> points) {
        List<Question> questions = new ArrayList<>();
        for (var child : root.getChildren()) {
            if (child instanceof Question) {
                var question = (Question) child;
                // add question if points or unspecified (-1) or equal
                if (points.isEmpty() || points.get() == -1 || question.getPoints().equals(points.get())) {
                    questions.add((Question) child);
                }
            } else if (child instanceof Category) {
                questions.addAll(getQuestionsWithGivenPointsFromSubtree((Category) child, points));
            }
        }
        return questions;
    }

    /**
     * Adds an item to user's library
     *
     * @param item item to add
     * @throws Exception if parent is missing or has invalid type
     */
    public <T extends Item> T addItem(@NotNull @Validated final Item item, Class<T> clazz) throws Exception {
        var optionalParent = itemRepository.findById(item.getParentId());

        if (optionalParent.isEmpty() || !(optionalParent.get() instanceof Category)) {
            throw new Exception(String.format("parent item is missing or has an invalid type: %s", optionalParent));
        }

        var parent = (Category) optionalParent.get();

        // set key to correspond with the last slot in parent's list of children
        var parentsChildCount = parent.getChildren().size();
        item.setKey(parent.getKey() + "-" + parentsChildCount);

        var savedItem = clazz.cast(itemRepository.save(item));
        parent.getChildren().add(savedItem);
        itemRepository.save(parent);

        return savedItem;
    }

    public void moveItem(@RequestParam @NotNull final String itemId, @RequestParam @NotNull final String newParentId, @RequestParam @NotNull final Integer position) throws Exception {
        final var item = getItemById(itemId, Item.class);
        var currParent = getItemById(item.getParentId(), Category.class);
        var newParent = getItemById(newParentId, Category.class);

        // remove from current
        currParent.getChildren().removeIf(e -> e.getId().equals(item.getId()));
        if (currParent.getId().equals(newParent.getId())) {
            // if new parent is the same, just re-add the item at the correct position
            currParent.getChildren().add(position < 0 ? 0 : position, item);
        }
        currParent = itemRepository.save(currParent);

        // if parents differ, update new parent and item's parent ref
        if (!currParent.getId().equals(newParent.getId())) {
            newParent.getChildren().add(position < 0 ? 0 : position, item);

            newParent = itemRepository.save(newParent);
        }

        rebuildSubtree(findRootCategory(newParent.getId()));
    }

    public Category updateCategory(@NotNull final String id, @NotNull @Validated final Category item) throws Exception {
        var savedItem = getItemById(id, Category.class);

        savedItem.setTitle(item.getTitle());

        return itemRepository.save(savedItem);
    }

    public <T extends Item> T updateItem(@NotNull final String id, @NotNull @Validated final Item item, Class<T> clazz) throws Exception {
        var savedItem = getItemById(id, clazz);

        item.setId(savedItem.getId());
        item.setParentId(savedItem.getParentId());
        item.setKey(savedItem.getKey());
        item.setLeaf(savedItem.isLeaf());

        return clazz.cast(itemRepository.save(item));
    }

    public void removeItemById(@NotNull final String itemId, final boolean deleteChildren) throws NotFoundException {
        var optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("can't find item with id " + itemId);
        }

        var item = optionalItem.get();

        if (Objects.isNull(item.getParentId())) {
            throw new IllegalStateException("can't remove root category");
        }

        var optionalParent = itemRepository.findById(item.getParentId());
        if (optionalParent.isEmpty()) {
            throw new IllegalStateException("can't remove item with null parent - should only be root category");
        }

        if (!(optionalParent.get() instanceof Category)) {
            throw new IllegalStateException("parent is not a category");
        }

        var parent = (Category) optionalParent.get();
        parent.getChildren().remove(item);

        if (item instanceof Category) {
            var category = (Category) item;
            if (deleteChildren) {
                itemRepository.deleteAll(category.getChildren());
            } else {
                // move children under parent of the category being removed
                parent.getChildren().addAll(category.getChildren());
            }
        }

        // removing items may have messed up item keys in the hierarchy
        rebuildSubtree(parent);

        itemRepository.save(parent);
        itemRepository.deleteById(itemId);
    }

    private void rebuildSubtree(Category subtreeRoot) {
        for (var i = 0; i < subtreeRoot.getChildren().size(); i++) {
            var child = subtreeRoot.getChildren().get(i);
            child.setParentId(subtreeRoot.getId());
            child.setKey(subtreeRoot.getKey() + "-" + i);
            child = itemRepository.save(child);
            if (child instanceof Category) {
                rebuildSubtree((Category) child);
            }
        }
    }

    private Category findRootCategory(String itemId) throws Exception {
        var category = getItemById(itemId, Category.class);
        if (Objects.isNull(category.getParentId())) {
            return category;
        }
        return findRootCategory(category.getParentId());
    }
}
