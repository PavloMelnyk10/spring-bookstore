package mate.academy.bookstore.service.book;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategories;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);

        Set<Category> categories = validateAndRetrieveCategories(requestDto.getCategoryIds());
        book.setCategories(categories);

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDtoWithoutCategories> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDtoWithoutCategories);
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findByIdWithCategories(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + id + " not found"));
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto update(Long id, UpdateBookRequestDto requestDto) {
        Book book = bookRepository.findByIdWithCategories(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + id + " not found"));

        bookMapper.updateBookFromDto(requestDto, book);

        if (requestDto.getCategoryIds() != null) {
            Set<Category> categories = validateAndRetrieveCategories(requestDto.getCategoryIds());
            book.setCategories(categories);
        }

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDto> search(BookSearchParametersDto searchParameters, Pageable pageable) {
        Specification<Book> specification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(specification, pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public Page<BookDto> findBooksByCategoryId(Long categoryId, Pageable pageable) {
        return bookRepository.findAllByCategoryId(categoryId, pageable)
                .map(bookMapper::toDto);
    }

    private Set<Category> validateAndRetrieveCategories(Set<Long> categoryIds) {
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));

        Set<Long> foundCategoryIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        Set<Long> missingCategoryIds = new HashSet<>(categoryIds);
        missingCategoryIds.removeAll(foundCategoryIds);

        if (!missingCategoryIds.isEmpty()) {
            throw new CategoryNotFoundException(
                    "Categories not found with IDs: " + missingCategoryIds);
        }
        return categories;
    }
}
