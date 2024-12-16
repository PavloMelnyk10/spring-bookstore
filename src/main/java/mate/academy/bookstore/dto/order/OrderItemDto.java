package mate.academy.bookstore.dto.order;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private int quantity;
    private BigDecimal price;
}
