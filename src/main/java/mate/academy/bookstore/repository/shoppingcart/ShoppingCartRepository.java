package mate.academy.bookstore.repository.shoppingcart;

import java.util.Optional;
import mate.academy.bookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUserId(Long userId);

    @Query("SELECT sc FROM ShoppingCart sc "
            + "LEFT JOIN FETCH sc.cartItems WHERE sc.user.id = :userId AND sc.isDeleted = false")
    Optional<ShoppingCart> findByUserIdWithItems(@Param("userId") Long userId);
}
