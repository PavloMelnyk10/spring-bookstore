package mate.academy.bookstore.service.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.bookstore.dto.category.UpdateCategoryRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CategoryMapper;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.repository.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Find all categories")
    void findAll_ReturnsPageOfCategories() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(new CategoryDto());

        // When
        Page<CategoryDto> actual = categoryService.findAll(pageable);

        // Then
        assertNotNull(actual);
        assertEquals(1, actual.getTotalElements());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find category by ID successfully")
    void findById_WithValidId_ReturnsCategoryDto() {
        // Given
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(new CategoryDto());

        // When
        CategoryDto actual = categoryService.findById(categoryId);

        // Then
        assertNotNull(actual);
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    @DisplayName("Find category by ID throws exception when not found")
    void findById_WithInvalidId_ThrowsException() {
        // Given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.findById(categoryId)
        );

        // Then
        assertEquals("Category with id 1 not found", exception.getMessage());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    @DisplayName("Delete category by ID")
    void delete_WithValidId_Success() {
        // Given
        Long categoryId = 1L;

        // When
        categoryService.delete(categoryId);

        // Then
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("Update category successfully")
    void update_WithValidId_Success() {
        // Given
        Long categoryId = 1L;
        UpdateCategoryRequestDto requestDto = new UpdateCategoryRequestDto();
        requestDto.setName("Science");

        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(new CategoryDto());

        // When
        CategoryDto actual = categoryService.update(categoryId, requestDto);

        // Then
        assertNotNull(actual);
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Save category successfully")
    void save_WithValidData_Success() {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Fantasy");

        Category category = new Category();
        category.setName(requestDto.getName());

        when(categoryRepository.existsByName(requestDto.getName())).thenReturn(false);
        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(new CategoryDto());

        // When
        CategoryDto actual = categoryService.save(requestDto);

        // Then
        assertNotNull(actual);
        verify(categoryRepository).existsByName(requestDto.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Save category with duplicate name throws exception")
    void save_WithDuplicateName_ThrowsException() {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Romance");

        when(categoryRepository.existsByName(requestDto.getName())).thenReturn(true);

        // When
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.save(requestDto)
        );

        // Then
        assertEquals("Category with name Romance already exists", exception.getMessage());
        verify(categoryRepository).existsByName(requestDto.getName());
    }
}
