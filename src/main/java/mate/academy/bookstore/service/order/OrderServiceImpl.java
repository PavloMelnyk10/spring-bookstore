package mate.academy.bookstore.service.order;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderItemDto;
import mate.academy.bookstore.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.order.OrderRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.service.shoppingcart.ShoppingCartService;
import mate.academy.bookstore.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    @Override
    public OrderDto placeOrder(String shippingAddress) {
        User user = userService.getCurrentUser();

        Order order = new Order();
        order.setShippingAddress(shippingAddress);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user: " + user.getId()));

        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            total = total.add(orderItem.getPrice());
            order.getOrderItems().add(orderItem);
        }
        order.setTotal(total);

        shoppingCartService.clearShoppingCart(shoppingCart);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        User user = userService.getCurrentUser();

        if (isAdmin(user)) {
            return orderRepository.findAll(pageable).map(orderMapper::toDto);
        }

        return orderRepository.findAllByUserId(user.getId(), pageable)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional
    public List<OrderItemDto> getOrderItems(Long orderId) {
        User user = userService.getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .filter(o -> isAdmin(user) || o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId + " for user: " + user.getId()));

        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderItemDto getOrderItem(Long orderId, Long itemId) {
        User user = userService.getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .filter(o -> isAdmin(user) || o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId + " for user: " + user.getId()));

        OrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "OrderItem not found with id: " + itemId + " in order: " + orderId));

        return orderItemMapper.toDto(orderItem);
    }

    @Override
    @Transactional
    public OrderDto updateOrder(Long orderId, UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId));
        order.setStatus(Order.Status.valueOf(requestDto.getStatus()));

        return orderMapper.toDto(orderRepository.save(order));
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
