package rs.ac.uns.ftn.iss.Komsiluk.util.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

import java.time.LocalDate;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object[]> {

    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        if (value == null  || value.length != 4) return true;

        LocalDate from = (LocalDate) value[1];
        LocalDate to = (LocalDate) value[2];

        if (from != null && to != null) {
            return !from.isAfter(to);
        }
        return true;
    }
}
