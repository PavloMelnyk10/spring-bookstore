package mate.academy.bookstore.repository.book;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.BookSearchParametersDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.SpecificationBuilder;
import mate.academy.bookstore.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements
        SpecificationBuilder<Book, BookSearchParametersDto> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> spec = Specification.where(null);

        if (searchParameters.getTitle() != null && searchParameters.getTitle().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("title")
                    .getSpecification(searchParameters.getTitle()));
        }

        if (searchParameters.getAuthor() != null && searchParameters.getAuthor().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("author")
                    .getSpecification(searchParameters.getAuthor()));
        }

        if (searchParameters.getIsbn() != null && searchParameters.getIsbn().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("isbn")
                    .getSpecification(searchParameters.getIsbn()));
        }

        if (searchParameters.getDescription() != null
                && searchParameters.getDescription().length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider("description")
                    .getSpecification(searchParameters.getDescription()));
        }

        if (searchParameters.getMinPrice() != null || searchParameters.getMaxPrice() != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Predicate predicate = criteriaBuilder.conjunction();

                if (searchParameters.getMinPrice() != null) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.greaterThanOrEqualTo(root.get("price"),
                                    searchParameters.getMinPrice()));
                }

                if (searchParameters.getMaxPrice() != null) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.lessThanOrEqualTo(root.get("price"),
                                    searchParameters.getMaxPrice()));
                }

                return predicate;
            });
        }
        return spec;
    }
}
