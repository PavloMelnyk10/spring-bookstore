package mate.academy.bookstore.controller.book;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Set;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.dto.book.UpdateBookRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Create a new book")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/delete-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_Success() throws Exception {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);

        BookDto expected = createBookDto(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Find book by ID")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = "classpath:database/books/add-one-book-with-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById_ValidId_ReturnsBook() throws Exception {
        // Given
        long bookId = 1L;

        // When
        MvcResult result = mockMvc.perform(get("/books/" + bookId))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);

        BookDto expected = new BookDto()
                .setId(bookId)
                .setTitle("Harry Potter")
                .setAuthor("J.K. Rowling")
                .setDescription("A young wizard's adventures.")
                .setIsbn("978-1234567897")
                .setPrice(BigDecimal.valueOf(14.99))
                .setCoverImage("https://example.com/hp.jpg")
                .setCategoryIds(Set.of(1L));

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Delete a book by ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/add-one-book-with-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_ValidId_Success() throws Exception {
        // Given
        long bookId = 1L;

        // When
        mockMvc.perform(delete("/books/" + bookId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Verify book absence after deletion")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/add-one-book-with-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void verifyBookAbsenceAfterDeletion() throws Exception {
        // Given
        long bookId = 1L;

        // When
        mockMvc.perform(delete("/books/" + bookId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/books/" + bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update a book")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/add-one-book-with-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_ValidRequestDto_Success() throws Exception {
        // Given
        long bookId = 1L;
        UpdateBookRequestDto requestDto = createUpdateBookRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(patch("/books/" + bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);

        BookDto expected = new BookDto()
                .setId(bookId)
                .setTitle("Updated Harry Potter")
                .setAuthor("J.K. Rowling")
                .setIsbn("978-1234567891")
                .setPrice(BigDecimal.valueOf(39.99));

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual,
                "description", "coverImage", "categoryIds"));
    }

    private CreateBookRequestDto createBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle("Harry Potter")
                .setAuthor("J.K. Rowling")
                .setDescription("A wizard's journey")
                .setIsbn("978-1234567897")
                .setPrice(BigDecimal.valueOf(29.99))
                .setCoverImage("https://example.com/hp.jpg")
                .setCategoryIds(Set.of(1L));
    }

    private BookDto createBookDto(Long id) {
        return new BookDto()
                .setId(id)
                .setTitle("Harry Potter")
                .setAuthor("J.K. Rowling")
                .setDescription("A wizard's journey")
                .setIsbn("978-1234567897")
                .setPrice(BigDecimal.valueOf(29.99))
                .setCoverImage("https://example.com/hp.jpg")
                .setCategoryIds(Set.of(1L));
    }

    private UpdateBookRequestDto createUpdateBookRequestDto() {
        return new UpdateBookRequestDto()
                .setTitle("Updated Harry Potter")
                .setAuthor("J.K. Rowling")
                .setIsbn("978-1234567891")
                .setPrice(BigDecimal.valueOf(39.99));
    }
}
