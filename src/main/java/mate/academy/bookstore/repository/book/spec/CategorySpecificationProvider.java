package mate.academy.bookstore.repository.book.spec;

import java.util.Arrays;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CategorySpecificationProvider implements SpecificationProvider<Book> {
    public static final String KEY = "category";

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.join("categories").get("id").in(Arrays.asList(params));
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
