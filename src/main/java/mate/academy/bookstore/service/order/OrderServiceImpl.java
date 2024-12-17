package mate.academy.bookstore.service.order;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.OrderItemDto;
import mate.academy.bookstore.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.bookstore.exception.EmptyCartException;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.order.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepository;

    @Transactional
    @Override
    public OrderDto placeOrder(String shippingAddress) {
        User user = userService.getCurrentUser();

        Order order = initializeOrder(user, shippingAddress);

        ShoppingCart shoppingCart = getShoppingCartForUser(user);

        if (shoppingCart.getCartItems().isEmpty()) {
            throw new EmptyCartException(
                    "Cannot place an order. Should be at least one item in the shopping cart");
        }

        order.setTotal(calculateTotalAndAddItems(order, shoppingCart));

        shoppingCartService.clearShoppingCart(shoppingCart);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        User user = userService.getCurrentUser();

        Page<Order> orders = isAdmin(user)
                ? orderRepository.findAll(pageable)
                : orderRepository.findAllByUserId(user.getId(), pageable);

        return orders.map(orderMapper::toDto);
    }

    @Override
    @Transactional
    public List<OrderItemDto> getOrderItems(Long orderId) {
        User user = userService.getCurrentUser();

        Order order = isAdmin(user)
                ? orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId))
                : orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + orderId + " for user: " + user.getId()));

        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderItemDto getOrderItem(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(itemId, orderId)
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

    private Order initializeOrder(User user, String shippingAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(Order.Status.PENDING);
        return order;
    }

    private ShoppingCart getShoppingCartForUser(User user) {
        return shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user: " + user.getId()));
    }

    private BigDecimal calculateTotalAndAddItems(Order order, ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems().stream()
                .map(cartItem -> createOrderItem(order, cartItem))
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItem createOrderItem(Order order, CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity())));

        order.getOrderItems().add(orderItem);
        return orderItem;
    }
}
