package mate.academy.bookstore.repository.category;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Given a category name, when it exists, then should return true")
    @Sql(value = "classpath:database/categories/add-single-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:database/categories/remove-single-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void existsByName_CategoryExists_ReturnsTrue() {
        // Given
        String categoryName = "Category";

        // When
        boolean exists = categoryRepository.existsByName(categoryName);

        // Then
        assertTrue(exists, "Category should exist in the database");
    }

    @Test
    @DisplayName("Given a category name, when it does not exist, then should return false")
    public void existsByName_CategoryDoesNotExist_ReturnsFalse() {
        // Given & When
        boolean exists = categoryRepository.existsByName("Category");

        // Then
        assertFalse(exists, "Category should not exist in the database");
    }
}
