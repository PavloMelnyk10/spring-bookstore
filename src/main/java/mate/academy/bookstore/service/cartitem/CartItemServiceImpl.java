package mate.academy.bookstore.service.cartitem;

import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.exception.CartItemNotFoundException;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;

    @Override
    public CartItem findById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(
                                "Cart item not found with ID: " + cartItemId));
    }

    @Override
    public void deleteById(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
