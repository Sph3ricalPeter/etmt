package cz.cvut.fel.pro.etmt.repository;

import cz.cvut.fel.pro.etmt.TestConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public void setUp() {
        userRepository.save(TestConstants.REGISTERED_USER);
    }

    @Test
    public void testFindById_shouldReturnNonNullUser() {
        var user = userRepository.getById(TestConstants.REGISTERED_USER.getId());
        assertTrue(user.isPresent());
    }

    @Test
    public void testFindByUsername_shouldReturnNonNullUser() {
        var user = userRepository.getByUsername(TestConstants.REGISTERED_USER.getUsername());
        assertTrue(user.isPresent());
    }

    @Test
    public void testExistsByUsername_shouldReturnTrue() {
        var user = userRepository.getByUsername(TestConstants.REGISTERED_USER.getUsername());
        assertTrue(user.isPresent());
    }

    @Test
    public void testExistsByUsername_shouldReturnEmptyOptional() {
        var user = userRepository.getByUsername("INVALID_USER");
        assertTrue(user.isEmpty());
    }

}
