package mate.academy.bookstore.dto.book;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchParametersDto {
    private String[] title;
    private String[] author;
    private String[] isbn;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String[] description;
}
