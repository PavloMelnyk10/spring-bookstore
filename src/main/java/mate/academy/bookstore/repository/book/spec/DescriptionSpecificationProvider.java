package mate.academy.bookstore.repository.book.spec;

import java.util.Arrays;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DescriptionSpecificationProvider implements SpecificationProvider<Book> {
    public static final String KEY = "description";

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get("description").in(Arrays.stream(params).toArray());
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
