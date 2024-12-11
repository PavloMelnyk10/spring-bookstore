package mate.academy.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Length(min = 8, max = 35)
        @Email
        String email,
        @NotBlank
        String password
) {
}
