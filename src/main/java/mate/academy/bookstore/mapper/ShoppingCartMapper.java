package mate.academy.bookstore.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookstore.config.MapperConfig;
import mate.academy.bookstore.dto.cart.CartDto;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItems", expression = "java(mapCartItems(shoppingCart.getCartItems()))")
    CartDto toDto(ShoppingCart shoppingCart);

    default List<CartDto.CartItemDto> mapCartItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> CartDto.CartItemDto.builder()
                        .id(cartItem.getId())
                        .bookId(cartItem.getBook().getId())
                        .bookTitle(cartItem.getBook().getTitle())
                        .quantity(cartItem.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }
}
