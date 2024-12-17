package mate.academy.bookstore.repository.order;

import java.util.Optional;
import mate.academy.bookstore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = "orderItems")
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findById(Long id);
}
