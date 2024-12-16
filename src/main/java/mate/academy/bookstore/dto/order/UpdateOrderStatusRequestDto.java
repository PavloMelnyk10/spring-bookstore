package mate.academy.bookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.validation.EnumValidator;

@Getter
@Setter
public class UpdateOrderStatusRequestDto {
    @NotNull(message = "Status is required")
    @EnumValidator(enumClass = Order.Status.class,
            message = "Invalid status. Allowed values: PENDING, COMPLETED, DELIVERED")
    private String status;
}
