package mate.academy.bookstore.dto.cart;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemRequestDto {
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
