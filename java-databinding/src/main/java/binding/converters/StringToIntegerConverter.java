package binding.converters;

/**
 * Converter between String and Integer.
 *
 * @author igor.kostromin
 *         26.06.13 19:37
 */
public class StringToIntegerConverter implements IBindingConverter<String, Integer> {
    public Class<String> getFirstClazz() {
        return String.class;
    }

    public Class<Integer> getSecondClazz() {
        return Integer.class;
    }

    @Override
    public ConversionResult<Integer> convert(String s) {
        try {
            if (s == null) return new ConversionResult<Integer>( null);
            int value = Integer.parseInt(s);
            return new ConversionResult<Integer>(value);
        } catch (NumberFormatException e) {
            return new ConversionResult<Integer>(false, "Incorrect number");
        }
    }

    @Override
    public ConversionResult<String> convertBack(Integer integer) {
        if (null == integer) return new ConversionResult<String>( null);
        return new ConversionResult<String>(integer.toString() );
    }


}
