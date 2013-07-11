package binding.converters;

/**
 * Represents value conversion result.
 *
 * User: igor.kostromin
 * Date: 26.06.13
 * Time: 21:46
 */
public class ConversionResult<T> {
    public T value;
    public boolean success;
    public String failReason;

    public ConversionResult(T value) {
        this.value = value;
        this.success = true;
    }

    public ConversionResult(boolean success, String failReason) {
        this.success = success;
        this.failReason = failReason;
    }
}
