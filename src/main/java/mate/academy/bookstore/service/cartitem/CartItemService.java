package mate.academy.bookstore.service.cartitem;

import mate.academy.bookstore.model.CartItem;

public interface CartItemService {
    CartItem findById(Long cartItemId);

    void deleteById(Long cartItemId);
}
