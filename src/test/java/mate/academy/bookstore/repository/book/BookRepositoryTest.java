package mate.academy.bookstore.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Category;
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
        Long bookId = 2L;

        // When
        Optional<Book> actualOptional = bookRepository.findByIdWithCategories(bookId);

        // Then
        assertTrue(actualOptional.isPresent());
        Book actual = actualOptional.get();

        Book expected = createExpectedBookWithCategories(bookId);

        assertEquals(expected, actual);
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

        Book expected = createExpectedBookWithCategories(1L);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual.getContent().stream().toList().getFirst());
    }

    private Book createExpectedBookWithCategories(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(id == 1L ? "Harry Potter" : "The Hobbit");
        book.setIsbn(id == 1L ? "978-1234567897" : "978-9876543210");
        book.setAuthor(id == 1L ? "J.K. Rowling" : "J.R.R. Tolkien");
        book.setDescription(id == 1L ? "A wizard story" : "A journey story");
        book.setPrice(id == 1L ? BigDecimal.valueOf(14.99) : BigDecimal.valueOf(12.49));
        book.setCoverImage(id == 1L ? "https://example.com/hp.jpg" : "https://example.com/hobbit.jpg");

        Category categoryOne = new Category();
        categoryOne.setId(1L);
        categoryOne.setName("Fiction");
        categoryOne.setDescription("Books that contain fictional stories.");

        Category categoryTwo = new Category();
        categoryTwo.setId(2L);
        categoryTwo.setName("Science");
        categoryTwo.setDescription("Books that explore scientific topics.");

        book.setCategories(book.getId().equals(1L) ? Set.of(categoryOne)
                : Set.of(categoryOne, categoryTwo));
        return book;
    }
}
