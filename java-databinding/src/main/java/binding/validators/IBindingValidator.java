package binding.validators;

/**
 * Defines the interface that objects that participate binding validation must implement.
 *
 * @author igor.kostromin
 *         26.06.13 17:50
 */
public interface IBindingValidator<T> {
    /**
     * Validates value T.
     */
    ValidationResult validate(T value);
}
