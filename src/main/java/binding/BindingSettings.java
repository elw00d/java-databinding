package binding;

import binding.adapters.*;
import binding.converters.ConversionResult;
import binding.converters.IBindingConverter;
import binding.converters.StringToIntegerConverter;

import java.util.HashMap;

/**
 * Contains converters, validators and adapters.
 *
 * @author igor.kostromin
 *         26.06.13 16:26
 */
public class BindingSettings {
    public static BindingSettings DEFAULT_SETTINGS ;

    static  {
        DEFAULT_SETTINGS = new BindingSettings();
        DEFAULT_SETTINGS.initializeDefault();
    }

    private HashMap<Class, HashMap<Class, IBindingConverter>> converters = new HashMap<Class, HashMap<Class, IBindingConverter>>(  );
    private HashMap<Class, IBindingAdapter> adapters = new HashMap<Class, IBindingAdapter>(  );

    public BindingSettings() {
    }

    /**
     * Adds default set of converters and ui adapters.
     */
    public void initializeDefault() {
        addConverter( new StringToIntegerConverter() );

        addAdapter( new JTextFieldAdapter() );
        addAdapter( new JPasswordFieldAdapter() );
        addAdapter( new JCheckBoxAdapter() );
        addAdapter( new JListAdapter());
        addAdapter( new JLabelAdapter());
    }

    public <T> void addAdapter(IBindingAdapter<T> adapter) {
        Class<T> targetClazz = adapter.getTargetClazz();
        if ( adapters.containsKey( targetClazz ))
            throw new RuntimeException( String.format( "Adapter for class %s is already registered.", targetClazz.getName() ) );
        adapters.put( targetClazz, adapter );
    }

    public <T> IBindingAdapter<T> getAdapterFor(Class<T> clazz) {
        IBindingAdapter<T> adapter = adapters.get( clazz );
        if (null == adapter) throw new RuntimeException( String.format( "Adapter for class %s not found.", clazz.getName() ) );
        return adapter;
    }

    public <TFirst, TSecond> void addConverter( IBindingConverter<TFirst, TSecond> converter) {
        registerConverter( converter );
        registerConverter( new ReversedConverter<TSecond, TFirst>( converter ) );
    }

    private void registerConverter(IBindingConverter converter) {
        Class first = converter.getFirstClazz();
        Class second = converter.getSecondClazz();
        if (converters.containsKey( first )) {
            HashMap<Class, IBindingConverter> firstClassConverters = converters.get( first );
            if (firstClassConverters.containsKey( second )) {
                throw new RuntimeException( String.format( "Converter for %s -> %s classes is already registered.", first.getName(), second.getName() ) );
            }
            firstClassConverters.put( second, converter );
        } else {
            HashMap<Class, IBindingConverter> firstClassConverters = new HashMap<Class, IBindingConverter>(  );
            firstClassConverters.put( second, converter );
            converters.put( first, firstClassConverters );
        }
    }

    public <TFirst, TSecond> IBindingConverter<TFirst, TSecond> getConverterFor(Class<TFirst> first, Class<TSecond > second) {
        if (!converters.containsKey( first ))
            //throw new RuntimeException( String.format( "Converter for %s -> %s classes not found.", first.getName(), second.getName() ) );
            return null;
        HashMap<Class, IBindingConverter> firstClassConverters = converters.get( first );
        if (!firstClassConverters.containsKey( second ))
            //throw new RuntimeException( String.format( "Converter for %s -> %s classes not found.", first.getName(), second.getName() ) );
            return null;
        return firstClassConverters.get( second );
    }

    private static class ReversedConverter<TFirst, TSecond> implements IBindingConverter<TFirst, TSecond> {

        IBindingConverter<TSecond, TFirst> converter;

        ReversedConverter(IBindingConverter<TSecond, TFirst> converter) {
            this.converter = converter;
        }

        public Class<TFirst> getFirstClazz() {
            return converter.getSecondClazz();
        }

        public Class<TSecond> getSecondClazz() {
            return converter.getFirstClazz();
        }

        @Override
        public ConversionResult<TSecond> convert(TFirst tFirst) {
            return converter.convertBack(tFirst);
        }

        @Override
        public ConversionResult<TFirst> convertBack(TSecond tSecond) {
            return converter.convert(tSecond);
        }


    }
}
