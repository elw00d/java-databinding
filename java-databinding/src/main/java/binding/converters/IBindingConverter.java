package binding.converters;

/**
 * Provides value conversion logic from first class to second and back.
 *
 * @author igor.kostromin
 *         26.06.13 16:37
 */
public interface IBindingConverter<TFirst, TSecond> {
    /**
     * Returns class object for TFirst class.
     */
    Class<TFirst> getFirstClazz();

    /**
     * Returns class object for TSecond class.
     */
    Class<TSecond> getSecondClazz();

    /**
     * Converts value from TFirst class to TSecond.
     */
    ConversionResult<TSecond> convert(TFirst first);

    /**
     * Converts value from TSecond class to TFirst.
     */
    ConversionResult<TFirst> convertBack(TSecond second);
}
