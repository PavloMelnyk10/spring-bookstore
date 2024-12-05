package mate.academy.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;

public class FieldMatchValidator implements
        ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {
    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(UserRegistrationRequestDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String firstValue = null;
        String secondValue = null;

        if (firstFieldName.equals("password")) {
            firstValue = value.getPassword();
        }
        if (secondFieldName.equals("repeatPassword")) {
            secondValue = value.getConfirmPassword();
        }

        boolean valid = firstValue != null && firstValue.equals(secondValue);

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(secondFieldName)
                    .addConstraintViolation();
        }

        return valid;
    }
}
