package binding;

/**
 * Represents result of one synchronization operation from Target to Source.
 * If hasConversionError is true, message will represent conversion error message.
 * If hasValidationError is true, message will represent validation error message.
 * Both hasConversionError and hasValidationError cannot be set to true.
 *
 * User: igor.kostromin
 * Date: 26.06.13
 * Time: 21:57
 */
public class BindingResult {
    public boolean hasError;
    public boolean hasConversionError;
    public boolean hasValidationError;
    public String message;

    public BindingResult( boolean hasError ) {
        this.hasError = hasError;
    }

    public BindingResult( boolean hasConversionError, boolean hasValidationError, String message ) {
        this.hasConversionError = hasConversionError;
        this.hasValidationError = hasValidationError;
        this.hasError = hasConversionError || hasValidationError;
        this.message = message;
    }
}
