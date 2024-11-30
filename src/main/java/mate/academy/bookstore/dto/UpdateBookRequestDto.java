package mate.academy.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequestDto {
    @NotNull(message = "Book title can't be null during update")
    @Size(min = 3, max = 100, message = "The book title must be between 3 and 100 characters")
    private String title;

    @NotNull(message = "Please provide a book author")
    @Size(min = 3, max = 100, message = "The author's name must be between 3 and 100 characters")
    private String author;

    @NotNull(message = "ISBN can't be null during update")
    @Pattern(regexp = "^(978|979)-\\d{10}$",
            message = "Invalid ISBN format. It should be like 978-1234567891")
    private String isbn;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @Size(max = 500, message = "The description cannot exceed 500 characters")
    private String description;

    @Pattern(regexp = "^(http://|https://)(www\\.)?([a-zA-Z0-9-]+)\\.[a-zA-Z]{2,6}(/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)?$",
            message = "Cover image must be a valid URL")
    private String coverImage;
}
