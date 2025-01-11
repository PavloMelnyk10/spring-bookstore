package mate.academy.bookstore.controller.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Harry Potter")
                .setAuthor("J.K. Rowling")
                .setDescription("A wizard's journey")
                .setIsbn("978-1234567897")
                .setPrice(BigDecimal.valueOf(29.99))
                .setCategoryIds(Set.of(1L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertEquals("Harry Potter", actual.getTitle());
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
        MvcResult result = mockMvc.perform(get("/books/" + 1L))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());

        // Then

        String actualResponse = result.getResponse().getContentAsString();
        assertNotNull(actualResponse);
        assertTrue(actualResponse.contains("Harry Potter"));
    }

    @Test
    @DisplayName("Delete a book")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/add-one-book-with-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_ValidId_Success() throws Exception {
        // Given
        long bookId = 1L;

        // When & Then
        mockMvc.perform(delete("/books/" + bookId))
                .andExpect(status().isNoContent());

        // Then

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
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto()
                .setTitle("Updated Harry Potter")
                .setAuthor("J.K. Rowling")
                .setIsbn("978-1234567891")
                .setPrice(BigDecimal.valueOf(39.99));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When & Then
        MvcResult result = mockMvc.perform(patch("/books/" + bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertEquals("Updated Harry Potter", actual.getTitle());
        assertEquals("J.K. Rowling", actual.getAuthor());
        assertEquals("978-1234567891", actual.getIsbn());
        assertEquals(BigDecimal.valueOf(39.99), actual.getPrice());
    }
}
