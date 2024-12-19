package mate.academy.bookstore.service.shoppingcart;

import mate.academy.bookstore.dto.cart.AddCartItemRequestDto;
import mate.academy.bookstore.dto.cart.CartDto;
import mate.academy.bookstore.dto.cart.UpdateCartItemRequestDto;
import mate.academy.bookstore.model.ShoppingCart;

public interface ShoppingCartService {
    CartDto getShoppingCartForCurrentUser();

    CartDto addBookToCart(AddCartItemRequestDto addCartItemRequestDto);

    CartDto updateCartItemQuantity(
            Long cartItemId,
            UpdateCartItemRequestDto updateCartItemRequestDto);

    void removeCartItem(Long cartItemId);

    void clearShoppingCart(ShoppingCart shoppingCart);
}
