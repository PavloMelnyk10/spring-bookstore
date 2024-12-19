package mate.academy.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, CharSequence> {
    private Enum<?>[] enumValues;

    @Override
    public void initialize(EnumValidator annotation) {
        Class<? extends Enum<?>> enumClass = annotation.enumClass();
        this.enumValues = enumClass.getEnumConstants();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Arrays.stream(enumValues)
                .anyMatch(enumValue -> enumValue.name().equals(value.toString()));
    }
}
