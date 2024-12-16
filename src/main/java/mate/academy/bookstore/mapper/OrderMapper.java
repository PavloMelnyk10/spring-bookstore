package mate.academy.bookstore.mapper;

import mate.academy.bookstore.config.MapperConfig;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "createdAt", source = "orderDate")
    OrderDto toDto(Order order);

    Order toModel(CreateOrderRequestDto requestDto);
}
