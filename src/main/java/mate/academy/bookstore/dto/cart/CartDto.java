package mate.academy.bookstore.dto.cart;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> cartItems;
}
