package binding.validators;

/**
 * Represents the result of data binding validation.
 *
 * User: igor.kostromin
 * Date: 26.06.13
 * Time: 21:53
 */
public class ValidationResult {
    public boolean valid;
    public String message;

    public ValidationResult(boolean valid) {
        this.valid = valid;
    }

    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
}
