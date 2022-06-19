package cz.cvut.fel.pro.etmt.repository;

import cz.cvut.fel.pro.etmt.model.library.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {
}
