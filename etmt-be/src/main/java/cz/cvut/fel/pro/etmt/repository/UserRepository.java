package cz.cvut.fel.pro.etmt.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import cz.cvut.fel.pro.etmt.model.User;

public interface UserRepository extends MongoRepository<User, Long> {
    
    @Query("{id:?0}")
    Optional<User> getById(String id);

    @Query("{username:?0}")
    Optional<User> getByUsername(String username);
    
}
