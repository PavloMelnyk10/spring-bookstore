package mate.academy.bookstore.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.dto.book.UpdateBookRequestDto;
import mate.academy.bookstore.exception.CategoryNotFoundException;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.BookMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.book.BookSpecificationBuilder;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Save a book with valid category IDs")
    void save_WithValidCategoryIds_Success() {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setCategoryIds(Set.of(1L));
        Book book = new Book();
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository
                .findAllById(requestDto.getCategoryIds())).thenReturn(List.of(category));
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(new BookDto());

        // When
        BookDto actual = bookService.save(requestDto);

        // Then
        assertNotNull(actual);
        verify(categoryRepository).findAllById(requestDto.getCategoryIds());
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Save a book with invalid category IDs throws exception")
    void save_WithInvalidCategoryIds_ThrowsException() {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setCategoryIds(Set.of(1L, 2L));
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository
                .findAllById(requestDto.getCategoryIds())).thenReturn(List.of(category));

        // When
        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> bookService.save(requestDto)
        );

        // Then
        assertEquals("Categories not found with IDs: [2]", exception.getMessage());
        verify(categoryRepository).findAllById(requestDto.getCategoryIds());
    }

    @Test
    @DisplayName("Find book by ID successfully")
    void findById_WithValidId_ReturnsBookDto() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findByIdWithCategories(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(new BookDto());

        // When
        BookDto actual = bookService.findById(bookId);

        // Then
        assertNotNull(actual);
        verify(bookRepository).findByIdWithCategories(bookId);
    }

    @Test
    @DisplayName("Find book by ID throws exception when not found")
    void findById_WithInvalidId_ThrowsException() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findByIdWithCategories(bookId)).thenReturn(Optional.empty());

        // When
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.findById(bookId)
        );

        // Then
        assertEquals("Book with id 1 not found", exception.getMessage());
        verify(bookRepository).findByIdWithCategories(bookId);
    }

    @Test
    @DisplayName("Search books by parameters")
    void search_WithParameters_ReturnsBooks() {
        // Given
        BookSearchParametersDto searchParameters = new BookSearchParametersDto();
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Book> specification = mock(Specification.class);
        Page<Book> bookPage = new PageImpl<>(List.of(new Book()));

        when(bookSpecificationBuilder.build(searchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(any(Book.class))).thenReturn(new BookDto());

        // When
        Page<BookDto> actual = bookService.search(searchParameters, pageable);

        // Then
        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
        verify(bookSpecificationBuilder).build(searchParameters);
        verify(bookRepository).findAll(specification, pageable);
    }

    @Test
    @DisplayName("Find books by category ID")
    void findBooksByCategoryId_WithValidCategoryId_ReturnsBooks() {
        // Given
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(new Book()));

        when(bookRepository.findAllByCategoryId(categoryId, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(any(Book.class))).thenReturn(new BookDto());

        // When
        Page<BookDto> actual = bookService.findBooksByCategoryId(categoryId, pageable);

        // Then
        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
        verify(bookRepository).findAllByCategoryId(categoryId, pageable);
    }

    @Test
    @DisplayName("Delete book by ID")
    void deleteById_WithValidId_Success() {
        // Given
        Long bookId = 1L;

        // When
        bookService.deleteById(bookId);

        // Then
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    @DisplayName("Update book successfully")
    void update_WithValidId_Success() {
        // Given
        Long bookId = 1L;
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto();
        requestDto.setCategoryIds(Set.of(1L));

        Book book = new Book();
        Category category = new Category();
        category.setId(1L);

        when(bookRepository.findByIdWithCategories(bookId)).thenReturn(Optional.of(book));
        when(categoryRepository
                .findAllById(requestDto.getCategoryIds())).thenReturn(List.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(new BookDto());

        // When
        BookDto actual = bookService.update(bookId, requestDto);

        // Then
        assertNotNull(actual);
        verify(bookRepository).findByIdWithCategories(bookId);
        verify(categoryRepository).findAllById(requestDto.getCategoryIds());
        verify(bookRepository).save(book);
    }
}
