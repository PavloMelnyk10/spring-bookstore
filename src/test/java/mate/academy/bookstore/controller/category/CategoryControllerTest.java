package mate.academy.bookstore.controller.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.bookstore.dto.category.UpdateCategoryRequestDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
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
class CategoryControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Create a new category")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/categories/remove-single-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_Success() throws Exception {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Category")
                .setDescription("Description");

        CategoryDto expected = new CategoryDto()
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect((status().isCreated()))
                .andReturn();

        // Then
        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Update an existing category")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts =
            "classpath:database/categories/revert-changes-of-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_ValidRequestDto_Success() throws Exception {
        // Given
        long categoryId = 1L;
        UpdateCategoryRequestDto requestDto = new UpdateCategoryRequestDto()
                .setName("Updated Category")
                .setDescription("Updated Description");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(patch("/categories/" + categoryId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        assertEquals("Updated Category", actual.getName());
        assertEquals("Updated Description", actual.getDescription());
    }

    @Test
    @DisplayName("Get books by category ID")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = {
            "classpath:database/books/add-books-with-categories.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/delete-books-with-categories.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBooksByCategoryId_ValidId_ReturnsBooks() throws Exception {
        // Given
        long categoryId = 1L;

        // When
        MvcResult result = mockMvc.perform(get("/categories/" + categoryId + "/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String actualResponse = result.getResponse().getContentAsString();
        assertNotNull(actualResponse);
        assertTrue(actualResponse.contains("Harry Potter"));
        assertTrue(actualResponse.contains("The Hobbit"));
    }

    @Test
    @DisplayName("Find category by ID")
    @WithMockUser(username = "user", roles = {"USER"})
    void findCategoryById_ValidId_ReturnsCategory() throws Exception {
        // Given
        long categoryId = 1L;

        // When
        MvcResult result = mockMvc.perform(get("/categories/" + categoryId))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String actualResponse = result.getResponse().getContentAsString();
        assertNotNull(actualResponse);
        assertTrue(actualResponse.contains("Fiction"));
        assertTrue(actualResponse.contains("Books that contain fictional stories."));
    }

    @Test
    @DisplayName("Get all categories")
    @WithMockUser(username = "user", roles = {"USER"})
    void findAllCategories_ReturnsPaginatedCategories() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String actualResponse = result.getResponse().getContentAsString();
        assertNotNull(actualResponse);
        assertTrue(actualResponse.contains("Fiction"));
        assertTrue(actualResponse.contains("History"));
        assertTrue(actualResponse.contains("Fantasy"));
    }
}
