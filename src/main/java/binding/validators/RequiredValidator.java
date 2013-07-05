package binding.validators;

/**
 * Validator checks the value is not null or empty (if value string).
 *
 * User: igor.kostromin
 * Date: 26.06.13
 * Time: 22:04
 */
public class RequiredValidator implements IBindingValidator<Object> {
    @Override
    public ValidationResult validate(Object value) {
        if (value == null || value instanceof String && ((String) value).isEmpty())
            return new ValidationResult(false, "Value is required");
        return new ValidationResult(true);
    }
}
