package mate.academy.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryRequestDto {
    @NotBlank(message = "Category name is required")
    @Size(min = 3, max = 50, message = "The category name must be between 3 and 50 characters")
    private String name;

    private String description;
}
