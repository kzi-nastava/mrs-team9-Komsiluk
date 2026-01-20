package rs.ac.uns.ftn.iss.Komsiluk.util.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {
    String message() default "From date cannot be after To date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


