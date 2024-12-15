package mate.academy.bookstore.service.shoppingcart;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.cart.AddCartItemRequestDto;
import mate.academy.bookstore.dto.cart.CartDto;
import mate.academy.bookstore.dto.cart.UpdateCartItemRequestDto;
import mate.academy.bookstore.exception.CartItemNotFoundException;
import mate.academy.bookstore.exception.ShoppingCartNotFoundException;
import mate.academy.bookstore.mapper.BookMapper;
import mate.academy.bookstore.mapper.ShoppingCartMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.service.book.BookService;
import mate.academy.bookstore.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserService userService;
    private final BookService bookService;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    @Override
    public CartDto getShoppingCartForCurrentUser() {
        User user = userService.getCurrentUser();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(()
                        -> new ShoppingCartNotFoundException("Shopping cart not found for user: "
                        + user.getId()));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public void addBookToCart(AddCartItemRequestDto requestDto) {
        User user = userService.getCurrentUser();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(()
                        -> new ShoppingCartNotFoundException("Shopping cart not found for user: "
                        + user.getId()));

        Optional<CartItem> existingCartItem = shoppingCart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().getId().equals(requestDto.getBookId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            existingCartItem.get().setQuantity(existingCartItem
                    .get().getQuantity() + requestDto.getQuantity());
        } else {
            Book book = bookMapper.toModel(bookService.findById(requestDto.getBookId()));
            CartItem newCartItem = new CartItem();
            newCartItem.setBook(book);
            newCartItem.setQuantity(requestDto.getQuantity());
            newCartItem.setShoppingCart(shoppingCart);
            shoppingCart.getCartItems().add(newCartItem);
        }

        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public void updateCartItemQuantity(Long cartItemId,
                                       UpdateCartItemRequestDto updateCartItemRequestDto) {
        User user = userService.getCurrentUser();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(()
                        -> new ShoppingCartNotFoundException("Shopping cart not found for user: "
                        + user.getId()));

        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(()
                        -> new CartItemNotFoundException("Cart item not found: " + cartItemId));

        cartItem.setQuantity(updateCartItemRequestDto.getQuantity());
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public void removeCartItem(Long cartItemId) {
        User user = userService.getCurrentUser();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(()
                        -> new ShoppingCartNotFoundException("Shopping cart not found for user: "
                        + user.getId()));

        shoppingCart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        shoppingCartRepository.save(shoppingCart);
    }
}
