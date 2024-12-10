package mate.academy.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import mate.academy.bookstore.validation.FieldMatch;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords do not match")
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 8, max = 35)
    private String password;

    @NotBlank
    @Length(min = 8, max = 35)
    private String confirmPassword;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String shippingAddress;
}
