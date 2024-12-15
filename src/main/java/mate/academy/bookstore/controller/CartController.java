package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.cart.AddCartItemRequestDto;
import mate.academy.bookstore.dto.cart.CartDto;
import mate.academy.bookstore.dto.cart.UpdateCartItemRequestDto;
import mate.academy.bookstore.service.shoppingcart.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping Cart Management", description = "Endpoints for managing the shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get shopping cart",
            description = "Retrieve the current user's shopping cart including all items in it")
    public CartDto getShoppingCart() {
        return shoppingCartService.getShoppingCartForCurrentUser();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add book to cart",
            description = "Add a book to the current user's shopping cart")
    public void addBookToCart(@Valid @RequestBody AddCartItemRequestDto requestDto) {
        shoppingCartService.addBookToCart(requestDto);
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update cart item quantity",
            description = "Update the quantity of a specific book"
                    + " in the shopping cart by its item ID")
    public void updateCartItemQuantity(@PathVariable Long cartItemId,
                                       @Valid @RequestBody UpdateCartItemRequestDto requestDto) {
        shoppingCartService.updateCartItemQuantity(cartItemId, requestDto);
    }

    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove book from cart",
            description = "Remove a specific book from the shopping cart by its item ID")
    public void removeCartItem(@PathVariable Long cartItemId) {
        shoppingCartService.removeCartItem(cartItemId);
    }
}
