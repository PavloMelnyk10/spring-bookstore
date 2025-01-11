package mate.academy.bookstore.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.bookstore.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find book by ID with categories")
    @Sql(scripts = "classpath:database/books/add-books-with-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-with-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithCategories_ValidId_ReturnsBookWithCategories() {
        // Given
        Long bookId = 1L;

        // When
        Optional<Book> actual = bookRepository.findByIdWithCategories(bookId);

        // Then
        assertTrue(actual.isPresent());
        assertEquals("Harry Potter", actual.get().getTitle());
        assertFalse(actual.get().getCategories().isEmpty());
    }

    @Test
    @DisplayName("Find books by category ID")
    @Sql(scripts = "classpath:database/books/add-books-with-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-with-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidCategoryId_ReturnsBooks() {
        // Given
        Long categoryId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<Book> actual = bookRepository.findAllByCategoryId(categoryId, pageable);

        // Then
        assertNotNull(actual);
        assertEquals(2, actual.getContent().size());
    }
}
