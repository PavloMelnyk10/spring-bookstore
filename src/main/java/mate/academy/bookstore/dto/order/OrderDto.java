package mate.academy.bookstore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import mate.academy.bookstore.model.Order;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long userId;
    private Order.Status status;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private String shippingAddress;
}
