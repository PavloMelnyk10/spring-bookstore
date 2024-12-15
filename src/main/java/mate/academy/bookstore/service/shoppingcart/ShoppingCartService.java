package mate.academy.bookstore.service.shoppingcart;

import mate.academy.bookstore.dto.cart.AddCartItemRequestDto;
import mate.academy.bookstore.dto.cart.CartDto;
import mate.academy.bookstore.dto.cart.UpdateCartItemRequestDto;

public interface ShoppingCartService {
    CartDto getShoppingCartForCurrentUser();

    void addBookToCart(AddCartItemRequestDto addCartItemRequestDto);

    void updateCartItemQuantity(Long cartItemId, UpdateCartItemRequestDto updateCartItemRequestDto);

    void removeCartItem(Long cartItemId);
}
