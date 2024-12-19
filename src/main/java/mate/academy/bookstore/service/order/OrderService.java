package mate.academy.bookstore.service.order;

import java.util.List;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderItemDto;
import mate.academy.bookstore.dto.order.UpdateOrderStatusRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto placeOrder(String shippingAddress);

    Page<OrderDto> getAllOrders(Pageable pageable);

    List<OrderItemDto> getOrderItems(Long orderId);

    OrderItemDto getOrderItem(Long orderId, Long itemId);

    OrderDto updateOrder(Long orderId, UpdateOrderStatusRequestDto requestDto);
}
