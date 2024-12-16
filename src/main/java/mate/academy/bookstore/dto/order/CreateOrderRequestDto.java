package mate.academy.bookstore.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequestDto {
    @NotBlank(message = "Shipping address can't be blank")
    @Size(min = 5, max = 255, message = "Shipping address must be between 5 and 255 characters")
    private String shippingAddress;
}
